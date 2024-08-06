// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package org.team9432.lib.doglog

import dev.doglog.DogLogOptions
import dev.doglog.internal.FaultLogger
import dev.doglog.internal.LogQueuer
import edu.wpi.first.hal.HALUtil
import edu.wpi.first.util.struct.StructSerializable
import edu.wpi.first.wpilibj.PowerDistribution

/** A logger based on WPILib's [DataLogManager]  */
open class KDogLog internal constructor() {

    /** The options to use for the logger.  */
    private var options: DogLogOptions = DogLogOptions()

    private val logger: LogQueuer = LogQueuer(options)

    /** Whether the logger is enabled.  */
    private var enabled: Boolean = true

    /** Get the options used by the logger.  */
    fun getOptions() = options

    /**
     * Update the options used by the logger.
     *
     *
     * Example:
     *
     * <pre>DogLog.setOptions(new DogLogOptions().withNtPublish(true));</pre>
     *
     *
     * See https://doglog.dev/reference/logger-options/ for more information.
     */
    fun setOptions(newOptions: DogLogOptions) {
        val oldOptions = options
        options = newOptions

        if (oldOptions != newOptions) {
            println("[DogLog] Options changed: $newOptions")
            logger.setOptions(newOptions)
        }
    }

    /**
     * Set the [PowerDistribution] instance to use for logging PDH/PDP data when logging extras
     * is enabled. If this is set to `null`, no PDH data will be logged. Otherwise, information like
     * battery voltage, device currents, etc. will be logged.
     *
     *
     * Example:
     *
     * <pre>DogLog.setPdh(new PowerDistribution());</pre>
     *
     * @param pdh The [PowerDistribution] instance to use for logging PDH/PDP data.
     */
    fun setPdh(pdh: PowerDistribution?) {
        logger.setPdh(pdh)
    }

    /**
     * Set whether the logger is enabled. If the logger is not enabled, calls to `log()` functions
     * will not do anything.
     */
    fun setEnabled(newEnabled: Boolean) {
        enabled = newEnabled
    }

    /** Log a boolean array.  */
    fun log(key: String?, value: BooleanArray?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a boolean.  */
    fun log(key: String?, value: Boolean) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a double array.  */
    fun log(key: String?, value: DoubleArray?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a double.  */
    fun log(key: String?, value: Double) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a float array.  */
    fun log(key: String?, value: FloatArray?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a float.  */
    fun log(key: String?, value: Float) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log an int array.  */
    fun log(key: String?, value: IntArray?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a long array.  */
    fun log(key: String?, value: LongArray?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a long.  */
    fun log(key: String?, value: Long) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    // TODO: Protobuf logs
    // TODO: Raw logs
    /** Log a string array.  */
    fun log(key: String?, value: Array<String?>?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log an enum array. Enums will be converted to strings with [Enum.name].  */
    fun log(key: String?, value: Array<Enum<*>>?) {
        if (value == null) {
            return
        }
        // Convert enum array to string array
        val stringArray = arrayOfNulls<String>(value.size)

        for (i in value.indices) {
            stringArray[i] = value[i].name
        }

        log(key, stringArray)
    }

    /** Log a string.  */
    fun log(key: String?, value: String?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log an enum. The enum will be converted to a string with [Enum.name].  */
    fun log(key: String?, value: Enum<*>?) {
        if (value == null) {
            return
        }
        log(key, value.name)
    }

    /** Log a struct array.  */
    fun <T: StructSerializable?> log(key: String?, value: Array<T>?) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /** Log a struct.  */
    fun <T: StructSerializable?> log(key: String?, value: T) {
        if (enabled) {
            val now = HALUtil.getFPGATime()
            logger.queueLog(now, key, value)
        }
    }

    /**
     * Log a fault.
     *
     *
     * See https://doglog.dev/guides/faults for more information.
     *
     * @param faultName The name of the fault to log.
     */
    fun logFault(faultName: String?) {
        if (enabled) {
            FaultLogger.logFault(logger, faultName)
        }
    }

    /**
     * Log a fault. The enum will be converted to a string with [Enum.name].
     *
     *
     * See https://doglog.dev/guides/faults for more information.
     *
     * @param faultName The name of the fault to log.
     */
    fun logFault(faultName: Enum<*>) {
        logFault(faultName.name)
    }

    /**
     * Check if faults have been logged using [DogLog.logFault].
     *
     * @return Whether any faults have been logged.
     */
    fun faultsLogged(): Boolean {
        return FaultLogger.faultsLogged()
    }
}