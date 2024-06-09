package org.team9432.lib.coroutines

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun await(condition: () -> Boolean, period: Duration = 20.milliseconds) {
    while (!condition()) delay(period)
}
