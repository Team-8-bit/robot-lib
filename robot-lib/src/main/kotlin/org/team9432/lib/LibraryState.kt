package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import kotlin.properties.Delegates

internal object LibraryState {
    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    var alliance: DriverStation.Alliance? = null
        internal set

    /** True if the robot is running in simulation. */
    var isSimulated: Boolean by Delegates.notNull()
        internal set
}