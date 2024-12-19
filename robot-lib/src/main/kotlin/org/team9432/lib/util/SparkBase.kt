package org.team9432.lib.util

import com.revrobotics.spark.SparkBase

val SparkBase.temperatureFahrenheit get() = (motorTemperature * (9 / 5)) + 32