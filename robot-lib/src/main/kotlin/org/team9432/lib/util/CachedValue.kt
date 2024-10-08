package org.team9432.lib.util

/** A class representing a generic cached value. */
class CachedValue<T> {
    var value: T? = null

    /** Invalidates the currently stored value. */
    fun invalidate() {
        value = null
    }

    /** Return true if there is a valid value currently cached. */
    val isValid get() = value != null

    /** Return true if there is no valid value currently cached. */
    val isInvalid get() = value == null

    /** Calls the given [block] with the cached value if present. */
    inline fun ifValid(block: (T) -> Unit) {
        value?.let { block.invoke(it) }
    }

    /** Calls the given [block] if there is no currently cached value. */
    inline fun ifInvalid(block: () -> Unit) {
        if (value == null) block.invoke()
    }
}