package org.team9432.lib.simulation

import edu.wpi.first.math.geometry.Rotation2d

abstract class SimulatedGyro {
    val gyroPhysicsSimulationResults = GyroPhysicsSimulationResults()

    class GyroPhysicsSimulationResults {
        var robotAngularVelocityRadPerSec: Double = 0.0
        var hasReading: Boolean = false

        val odometryYawPositions: Array<Rotation2d> = Array(SIMULATION_TICKS_IN_1_PERIOD) { Rotation2d() }
    }
}