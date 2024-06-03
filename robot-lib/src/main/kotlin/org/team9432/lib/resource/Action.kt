package org.team9432.lib.resource

import kotlinx.coroutines.CoroutineScope

typealias Action = suspend CoroutineScope.() -> Unit