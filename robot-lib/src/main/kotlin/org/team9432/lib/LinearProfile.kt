// By 6328: https://github.com/Mechanical-Advantage/RobotCode2024/blob/a025615a52193b7709db7cf14c51c57be17826f2/src/main/java/org/littletonrobotics/frc2024/util/LinearProfile.java#L14
package org.team9432.lib

import org.team9432.lib.util.epsilonEquals


/**
 * Creates a new LinearProfile
 *
 * @param maxAcceleration The max ramp rate in velocity in rpm/sec
 * @param period Period of control loop (0.02)
 *
 * Class by Team 6328
 */
class LinearProfile(maxAcceleration: Double, private val period: Double) {
    private var dv = 0.0

    var currentSetpoint = 0.0
        private set

    var goal = 0.0

    init {
        setMaxAcceleration(maxAcceleration)
    }

    /** Set the max acceleration constraint in rpm/sec  */
    fun setMaxAcceleration(maxAcceleration: Double) {
        dv = maxAcceleration * period
    }

    /**
     * Sets the target setpoint, starting from the current speed
     *
     * @param goal Target setpoint
     * @param currentSpeed Current speed, to be used as the starting setpoint
     */
    fun setGoal(goal: Double, currentSpeed: Double) {
        this.goal = goal
        currentSetpoint = currentSpeed
    }

    /** Resets target setpoint and current setpoint  */
    fun reset() {
        currentSetpoint = 0.0
        goal = 0.0
    }

    /**
     * Returns the current setpoint to send to motors
     *
     * @return Setpoint to send to motors
     */
    fun calculateSetpoint(): Double {
        if (epsilonEquals(goal, currentSetpoint)) {
            return currentSetpoint
        }
        if (goal > currentSetpoint) {
            currentSetpoint += dv
            if (currentSetpoint > goal) {
                currentSetpoint = goal
            }
        } else if (goal < currentSetpoint) {
            currentSetpoint -= dv
            if (currentSetpoint < goal) {
                currentSetpoint = goal
            }
        }
        return currentSetpoint
    }
}