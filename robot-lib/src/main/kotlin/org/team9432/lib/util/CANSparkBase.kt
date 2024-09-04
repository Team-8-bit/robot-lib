package org.team9432.lib.util

import com.revrobotics.CANSparkBase

val CANSparkBase.temperatureFahrenheit get() = (motorTemperature * (9 / 5)) + 32