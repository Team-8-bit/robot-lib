package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase
import kotlin.properties.Delegates

object LibraryState {
    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    var alliance: DriverStation.Alliance? = null
        internal set

    /** True if the robot is running in simulation. */
    var isSimulation: Boolean by Delegates.notNull()
        internal set

    /** The mode the robot is currently running in, always uses real when running on the robot. */
    val mode = if (RobotBase.isReal()) Mode.REAL else Mode.SIM

    enum class Mode {
        REAL, SIM, REPLAY
    }
}