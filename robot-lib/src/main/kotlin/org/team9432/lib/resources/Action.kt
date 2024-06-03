package org.team9432.lib.resources

import kotlinx.coroutines.CoroutineScope

typealias Action = suspend CoroutineScope.() -> Unit