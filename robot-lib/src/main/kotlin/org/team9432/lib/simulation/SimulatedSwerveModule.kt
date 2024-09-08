package org.team9432.lib.simulation

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModuleState

abstract class SimulatedSwerveModule {
    val physicsSimulationResults = SwerveModulePhysicsSimulationResults()

    /** This replaces DC Motor Sim for drive wheels. */
    class SwerveModulePhysicsSimulationResults {
        var driveWheelFinalRevolutions: Double = 0.0
        var driveWheelFinalVelocityRadPerSec: Double = 0.0

        val odometryDriveWheelRevolutions: DoubleArray = DoubleArray(SIMULATION_TICKS_IN_1_PERIOD)
        val odometrySteerPositions: Array<Rotation2d> = Array(SIMULATION_TICKS_IN_1_PERIOD) { Rotation2d() }
    }

    abstract fun updateSteerSim(periodSecs: Double)

    abstract fun getSimulationTorque(): Double

    abstract fun getSimulationSwerveState(): SwerveModuleState
    abstract fun getDesiredSimulationSwerveState(): SwerveModuleState
}