package org.team9432.lib.util

import com.ctre.phoenix6.StatusCode

fun StatusCode.printOnError(lazyMessage: (StatusCode) -> String) {
    if (!this.isOK) println(lazyMessage.invoke(this))
}