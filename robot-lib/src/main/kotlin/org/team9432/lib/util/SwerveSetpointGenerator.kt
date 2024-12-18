// By team 254: https://github.com/Team254/FRC-2023-Public/blob/3856510c06ea67407be9bad08557ddba35b62c71/src/main/java/com/team254/lib/swerve/SwerveSetpointGenerator.java#L47
package org.team9432.lib.util

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.geometry.Twist2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import org.team9432.lib.util.SwerveSetpointGenerator.Function2d
import java.util.*
import kotlin.math.*


/**
 * Takes a prior setpoint (ChassisSpeeds), a desired setpoint (from a driver, or from a path follower), and outputs a new setpoint
 * that respects all of the kinematic constraints on module rotation speed and wheel velocity/acceleration. By generating a new
 * setpoint every iteration, the robot will converge to the desired setpoint quickly while avoiding any intermediate state that is
 * kinematically infeasible (and can result in wheel slip or robot heading drift as a result).
 */
class SwerveSetpointGenerator(private val kinematics: SwerveDriveKinematics, private val moduleLocations: Array<Translation2d>) {
    /**
     * Check if it would be faster to go to the opposite of the goal heading (and reverse drive
     * direction).
     *
     * @param prevToGoal The rotation from the previous state to the goal state (i.e.
     * prev.inverse().rotateBy(goal)).
     * @return True if the shortest path to achieve this rotation involves flipping the drive
     * direction.
     */
    private fun flipHeading(prevToGoal: Rotation2d): Boolean {
        return abs(prevToGoal.radians) > Math.PI / 2.0
    }

    private fun unwrapAngle(ref: Double, angle: Double): Double {
        val diff = angle - ref
        return if (diff > Math.PI) {
            angle - 2.0 * Math.PI
        } else if (diff < -Math.PI) {
            angle + 2.0 * Math.PI
        } else {
            angle
        }
    }

    private fun interface Function2d {
        fun f(x: Double, y: Double): Double
    }

    /**
     * Find the root of the generic 2D parametric function 'func' using the regula falsi technique.
     * This is a pretty naive way to do root finding, but it's usually faster than simple bisection
     * while being robust in ways that e.g. the Newton-Raphson method isn't.
     *
     * @param func The Function2d to take the root of.
     * @param x_0 x value of the lower bracket.
     * @param y_0 y value of the lower bracket.
     * @param f_0 value of 'func' at x_0, y_0 (passed in by caller to save a call to 'func' during
     * recursion)
     * @param x_1 x value of the upper bracket.
     * @param y_1 y value of the upper bracket.
     * @param f_1 value of 'func' at x_1, y_1 (passed in by caller to save a call to 'func' during
     * recursion)
     * @param iterations_left Number of iterations of root finding left.
     * @return The parameter value 's' that interpolating between 0 and 1 that corresponds to the
     * (approximate) root.
     */
    private fun findRoot(
        func: Function2d,
        x_0: Double,
        y_0: Double,
        f_0: Double,
        x_1: Double,
        y_1: Double,
        f_1: Double,
        iterations_left: Int,
    ): Double {
        if (iterations_left < 0 || epsilonEquals(f_0, f_1)) {
            return 1.0
        }
        val s_guess = max(0.0, min(1.0, -f_0 / (f_1 - f_0)))
        val x_guess = (x_1 - x_0) * s_guess + x_0
        val y_guess = (y_1 - y_0) * s_guess + y_0
        val f_guess = func.f(x_guess, y_guess)
        return if (sign(f_0) == sign(f_guess)) {
            // 0 and guess on same side of root, so use upper bracket.
            (s_guess + (1.0 - s_guess) * findRoot(func, x_guess, y_guess, f_guess, x_1, y_1, f_1, iterations_left - 1))
        } else {
            // Use lower bracket.
            (s_guess * findRoot(func, x_0, y_0, f_0, x_guess, y_guess, f_guess, iterations_left - 1))
        }
    }

    private fun findSteeringMaxS(
        x_0: Double,
        y_0: Double,
        f_0: Double,
        x_1: Double,
        y_1: Double,
        f_1: Double,
        max_deviation: Double,
        max_iterations: Int,
    ): Double {
        var f_1 = f_1
        f_1 = unwrapAngle(f_0, f_1)
        val diff = f_1 - f_0
        if (abs(diff) <= max_deviation) {
            // Can go all the way to s=1.
            return 1.0
        }
        val offset = f_0 + sign(diff) * max_deviation
        val func = Function2d { x: Double, y: Double -> unwrapAngle(f_0, atan2(y, x)) - offset }
        return findRoot(func, x_0, y_0, f_0 - offset, x_1, y_1, f_1 - offset, max_iterations)
    }

    private fun findDriveMaxS(
        x_0: Double,
        y_0: Double,
        f_0: Double,
        x_1: Double,
        y_1: Double,
        f_1: Double,
        max_vel_step: Double,
        max_iterations: Int,
    ): Double {
        val diff = f_1 - f_0
        if (abs(diff) <= max_vel_step) {
            // Can go all the way to s=1.
            return 1.0
        }
        val offset = f_0 + sign(diff) * max_vel_step
        val func = Function2d { x: Double, y: Double -> hypot(x, y) - offset }
        return findRoot(func, x_0, y_0, f_0 - offset, x_1, y_1, f_1 - offset, max_iterations)
    }

    data class SwerveSetpoint(val chassisSpeeds: ChassisSpeeds, val moduleStates: Array<SwerveModuleState>)
    data class ModuleLimits(val maxDriveVelocity: Double, val maxDriveAcceleration: Double, val maxSteeringVelocity: Double)

    /**
     * Generate a new setpoint.
     *
     * @param limits The kinematic limits to respect for this setpoint.
     * @param prevSetpoint The previous setpoint motion. Normally, you'd pass in the previous
     * iteration setpoint instead of the actual measured/estimated kinematic state.
     * @param desiredState The desired state of motion, such as from the driver sticks or a path
     * following algorithm.
     * @param dt The loop time.
     * @return A Setpoint object that satisfies all of the KinematicLimits while converging to
     * desiredState quickly.
     */
    fun generateSetpoint(
        limits: ModuleLimits,
        prevSetpoint: SwerveSetpoint,
        desiredState: ChassisSpeeds,
        dt: Double,
    ): SwerveSetpoint {
        var desiredState = desiredState
        val modules: Array<Translation2d> = moduleLocations

        val desiredModuleState: Array<SwerveModuleState> = kinematics.toSwerveModuleStates(desiredState)
        // Make sure desiredState respects velocity limits.
        if (limits.maxDriveVelocity > 0.0) {
            SwerveDriveKinematics.desaturateWheelSpeeds(desiredModuleState, limits.maxDriveVelocity)
            desiredState = kinematics.toChassisSpeeds(*desiredModuleState)
        }

        // Special case: desiredState is a complete stop. In this case, module angle is arbitrary, so
        // just use the previous angle.
        var need_to_steer = true
        if (desiredState.toTwist2d().epsilonEquals(Twist2d())) {
            need_to_steer = false
            for (i in modules.indices) {
                desiredModuleState[i].angle = prevSetpoint.moduleStates.get(i).angle
                desiredModuleState[i].speedMetersPerSecond = 0.0
            }
        }

        // For each module, compute local Vx and Vy vectors.
        val prev_vx = DoubleArray(modules.size)
        val prev_vy = DoubleArray(modules.size)
        val prev_heading = arrayOfNulls<Rotation2d>(modules.size)
        val desired_vx = DoubleArray(modules.size)
        val desired_vy = DoubleArray(modules.size)
        val desired_heading = arrayOfNulls<Rotation2d>(modules.size)
        var all_modules_should_flip = true

        for (i in modules.indices) {
            prev_vx[i] = (prevSetpoint.moduleStates[i].angle.cos * prevSetpoint.moduleStates[i].speedMetersPerSecond)
            prev_vy[i] = (prevSetpoint.moduleStates[i].angle.sin * prevSetpoint.moduleStates[i].speedMetersPerSecond)
            prev_heading[i] = prevSetpoint.moduleStates[i].angle

            if (prevSetpoint.moduleStates[i].speedMetersPerSecond < 0.0) {
                prev_heading[i] = prev_heading[i]!!.rotateBy(Rotation2d.fromRadians(Math.PI))
            }
            desired_vx[i] = desiredModuleState[i].angle.cos * desiredModuleState[i].speedMetersPerSecond
            desired_vy[i] = desiredModuleState[i].angle.sin * desiredModuleState[i].speedMetersPerSecond
            desired_heading[i] = desiredModuleState[i].angle

            if (desiredModuleState[i].speedMetersPerSecond < 0.0) {
                desired_heading[i] = desired_heading[i]!!.rotateBy(Rotation2d.fromRadians(Math.PI))
            }

            if (all_modules_should_flip) {
                val required_rotation_rad = abs(prev_heading[i]!!.unaryMinus().rotateBy(desired_heading[i]).radians)
                if (required_rotation_rad < Math.PI / 2.0) {
                    all_modules_should_flip = false
                }
            }
        }
        if (all_modules_should_flip && !prevSetpoint.chassisSpeeds.toTwist2d().epsilonEquals(Twist2d()) && !desiredState.toTwist2d().epsilonEquals(Twist2d())) {
            // It will (likely) be faster to stop the robot, rotate the modules in place to the complement
            // of the desired
            // angle, and accelerate again.
            return generateSetpoint(limits, prevSetpoint, ChassisSpeeds(), dt)
        }

        // Compute the deltas between start and goal. We can then interpolate from the start state to
        // the goal state; then
        // find the amount we can move from start towards goal in this cycle such that no kinematic
        // limit is exceeded.
        val dx: Double = desiredState.vxMetersPerSecond - prevSetpoint.chassisSpeeds.vxMetersPerSecond
        val dy: Double = desiredState.vyMetersPerSecond - prevSetpoint.chassisSpeeds.vyMetersPerSecond
        val dtheta: Double = desiredState.omegaRadiansPerSecond - prevSetpoint.chassisSpeeds.omegaRadiansPerSecond

        // 's' interpolates between start and goal. At 0, we are at prevState and at 1, we are at
        // desiredState.
        var min_s = 1.0

        // In cases where an individual module is stopped, we want to remember the right steering angle
        // to command (since
        // inverse kinematics doesn't care about angle, we can be opportunistically lazy).
        val overrideSteering: MutableList<Optional<Rotation2d>> = ArrayList(modules.size)
        // Enforce steering velocity limits. We do this by taking the derivative of steering angle at
        // the current angle,
        // and then backing out the maximum interpolant between start and goal states. We remember the
        // minimum across all modules, since
        // that is the active constraint.
        val max_theta_step: Double = dt * limits.maxSteeringVelocity
        for (i in modules.indices) {
            if (!need_to_steer) {
                overrideSteering.add(Optional.of(prevSetpoint.moduleStates[i].angle))
                continue
            }
            overrideSteering.add(Optional.empty())
            if (epsilonEquals(prevSetpoint.moduleStates[i].speedMetersPerSecond, 0.0)) {
                // If module is stopped, we know that we will need to move straight to the final steering
                // angle, so limit based
                // purely on rotation in place.
                if (epsilonEquals(desiredModuleState[i].speedMetersPerSecond, 0.0)) {
                    // Goal angle doesn't matter. Just leave module at its current angle.
                    overrideSteering[i] = Optional.of(prevSetpoint.moduleStates[i].angle)
                    continue
                }

                var necessaryRotation = prevSetpoint.moduleStates[i].angle.unaryMinus().rotateBy(desiredModuleState[i].angle)
                if (flipHeading(necessaryRotation)) {
                    necessaryRotation = necessaryRotation.rotateBy(Rotation2d.fromRadians(Math.PI))
                }
                // getRadians() bounds to +/- Pi.
                val numStepsNeeded: Double = abs(necessaryRotation.radians) / max_theta_step

                if (numStepsNeeded <= 1.0) {
                    // Steer directly to goal angle.
                    overrideSteering[i] = Optional.of(desiredModuleState[i].angle)
                    // Don't limit the global min_s;
                    continue
                } else {
                    // Adjust steering by max_theta_step.
                    overrideSteering[i] = Optional.of(
                        prevSetpoint.moduleStates[i].angle.rotateBy(
                            Rotation2d.fromRadians(
                                sign(necessaryRotation.radians) * max_theta_step
                            )
                        )
                    )
                    min_s = 0.0
                    continue
                }
            }
            if (min_s == 0.0) {
                // s can't get any lower. Save some CPU.
                continue
            }

            val kMaxIterations = 8
            val s = findSteeringMaxS(
                prev_vx[i], prev_vy[i], prev_heading[i]!!.radians, desired_vx[i], desired_vy[i], desired_heading[i]!!.radians, max_theta_step, kMaxIterations
            )
            min_s = min(min_s, s)
        }

        // Enforce drive wheel acceleration limits.
        val max_vel_step: Double = dt * limits.maxDriveAcceleration
        for (i in modules.indices) {
            if (min_s == 0.0) {
                // No need to carry on.
                break
            }
            val vx_min_s = if (min_s == 1.0) desired_vx[i] else (desired_vx[i] - prev_vx[i]) * min_s + prev_vx[i]
            val vy_min_s = if (min_s == 1.0) desired_vy[i] else (desired_vy[i] - prev_vy[i]) * min_s + prev_vy[i]
            // Find the max s for this drive wheel. Search on the interval between 0 and min_s, because we
            // already know we can't go faster
            // than that.
            val kMaxIterations = 10
            val s = (min_s * findDriveMaxS(
                prev_vx[i], prev_vy[i], hypot(prev_vx[i], prev_vy[i]), vx_min_s, vy_min_s, hypot(vx_min_s, vy_min_s), max_vel_step, kMaxIterations
            ))
            min_s = min(min_s, s)
        }

        val retSpeeds = ChassisSpeeds(
            prevSetpoint.chassisSpeeds.vxMetersPerSecond + min_s * dx,
            prevSetpoint.chassisSpeeds.vyMetersPerSecond + min_s * dy,
            prevSetpoint.chassisSpeeds.omegaRadiansPerSecond + min_s * dtheta
        )

        val retStates = kinematics.toSwerveModuleStates(retSpeeds)
        for (i in modules.indices) {
            val maybeOverride = overrideSteering[i]
            if (maybeOverride.isPresent) {
                val override = maybeOverride.get()
                if (flipHeading(retStates[i].angle.unaryMinus().rotateBy(override))) {
                    retStates[i].speedMetersPerSecond *= -1.0
                }
                retStates[i].angle = override
            }
            val deltaRotation = prevSetpoint.moduleStates[i].angle.unaryMinus().rotateBy(retStates[i].angle)
            if (flipHeading(deltaRotation)) {
                retStates[i].angle = retStates[i].angle.rotateBy(Rotation2d.fromRadians(Math.PI))
                retStates[i].speedMetersPerSecond *= -1.0
            }
        }
        return SwerveSetpoint(retSpeeds, retStates)
    }

    private fun ChassisSpeeds.toTwist2d() = Twist2d(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond)

    private fun Twist2d.epsilonEquals(other: Twist2d) = epsilonEquals(this.dx, other.dx) && epsilonEquals(this.dy, other.dy) && epsilonEquals(this.dtheta, other.dtheta)
}