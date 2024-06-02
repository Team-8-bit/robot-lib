package org.team9432.lib.commandbased

import kotlinx.coroutines.CoroutineScope

typealias Action = suspend CoroutineScope.() -> Unit