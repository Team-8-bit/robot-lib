package org.team9432.lib.doglog

import dev.doglog.DogLogOptions
import edu.wpi.first.wpilibj.PowerDistribution

object Logger: KDogLog() {

    /**
     * Options for configuring DogLog.
     *
     * See [here](https://doglog.dev/reference/logger-options) for more information.
     */
    fun configure(
        /**
         * Whether logged values should be published to NetworkTables. You should not have this enabled
         * if you're using a competition flashed radio, otherwise you may consume too much bandwidth.
         */
        ntPublish: Boolean = false,
        /** Whether all NetworkTables fields should be saved to the log file. */
        captureNt: Boolean = false,
        /**
         * Whether driver station data (robot enable state and joystick inputs) should be saved to the
         * log file. Because of a limitation in WPILib, this option can't be disabled once it has been
         * enabled.
         */
        captureDs: Boolean = false,
        /** Whether to log extra data, like PDH currents, CAN usage, etc. Automatically creates a [PowerDistribution] instance. */
        logExtras: Boolean = true,
        /** The maximum size of the log entry queue to use. */
        logEntryQueueCapacity: Int = 1000,
    ) {
        if (logExtras) setPdh(PowerDistribution())
        setOptions(DogLogOptions(ntPublish, captureNt, captureDs, logExtras, logEntryQueueCapacity))
    }

    fun configureEventDefaults() = configure(ntPublish = false, captureNt = false, captureDs = true, logExtras = true, logEntryQueueCapacity = 1000)
    fun configureDevelopmentDefaults() = configure(ntPublish = true, captureNt = false, captureDs = true, logExtras = true, logEntryQueueCapacity = 1000)
}