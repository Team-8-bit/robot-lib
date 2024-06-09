package org.team9432.lib.wrappers.neo

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.wrappers.Spark

class LoggedNeoIOReal(val config: LoggedNeo.Config): LoggedNeoIO {
    private val spark = Spark(config.canID, config.deviceName, config.motorType)

    private val encoder = spark.encoder

    init {
        spark.applyConfig(config.sparkConfig)
    }

    override fun getAngle() = Rotation2d.fromRotations(encoder.position) / config.gearRatio
    override fun getVelocityRadPerSec() = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity) / config.gearRatio
    override fun getAppliedVolts() = spark.appliedOutput * spark.busVoltage
    override fun getCurrentAmps() = spark.outputCurrent

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    override fun setBrakeMode(enabled: Boolean) = spark.setBrakeMode(enabled)
    override fun stop() = setVoltage(0.0)
}