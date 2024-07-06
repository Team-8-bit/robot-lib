package org.team9432.lib.resource

import edu.wpi.first.wpilibj2.command.Command
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import org.team9432.lib.robot.CoroutineRobot
import kotlin.coroutines.resume

typealias Action = suspend CoroutineScope.() -> Unit

fun Command.toAction(): Action {
    val command = this
    return {
        suspendCancellableCoroutine { cont ->
            command.initialize()

            val periodic = CoroutineRobot.startPeriodic {
                command.execute()
                if (command.isFinished) {
                    command.end(false)
                    this.stopPeriodic()
                    cont.resume(Unit)
                }
            }

            cont.invokeOnCancellation {
                command.end(true)
                periodic.stopPeriodic()
            }
        }
    }
}