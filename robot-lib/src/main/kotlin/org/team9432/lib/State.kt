package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase

object State {
    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    var alliance: DriverStation.Alliance? = null
        internal set

    /** The mode the robot is currently running in, always uses real when running on the robot. */
    val mode = if (RobotBase.isReal()) Mode.REAL else Mode.SIM

    enum class Mode {
        REAL, SIM, REPLAY
    }
}