package org.team9432.lib

import edu.wpi.first.wpilibj.DriverStation
import kotlin.properties.Delegates

object Library {
    /** The alliance that the robot is on according to the driver station. Null if not connected. */
    val alliance: DriverStation.Alliance? get() = allianceSupplier.invoke()

    private lateinit var allianceSupplier: () -> DriverStation.Alliance?

    /** True if the robot is running in simulation. */
    internal val isSimulated: Boolean
        get() = runtime == Runtime.SIM

    /** True if the robot is running in simulation. */
    internal lateinit var runtime: Runtime
        private set

    internal var tuningMode by Delegates.notNull<Boolean>()
        private set

    /** Initializes the library, some features will not work unless this is called. */
    fun initialize(runtime: Runtime, tuningMode: Boolean, allianceSupplier: () -> DriverStation.Alliance?) {
        this.runtime = runtime
        this.tuningMode = tuningMode
        this.allianceSupplier = allianceSupplier
    }

    enum class Runtime {
        REAL, REPLAY, SIM
    }
}