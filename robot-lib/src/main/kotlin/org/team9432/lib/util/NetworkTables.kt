package org.team9432.lib.util

import edu.wpi.first.networktables.NetworkTable
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

inline fun <reified T: Enum<T>> NetworkTable.enumValue(key: String, initialValue: T): ReadWriteProperty<Any?, T> {
    set(key, initialValue.name)
    return Delegates.observable(initialValue) { _, _, newValue -> set(key, newValue.name) }
}

operator fun NetworkTable.set(key: String, newValue: Any) {
    getEntry(key).setValue(newValue)
}