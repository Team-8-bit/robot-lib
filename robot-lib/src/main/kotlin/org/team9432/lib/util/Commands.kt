package org.team9432.lib.util

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import org.team9432.lib.Library

fun Command.afterSimDelay(delaySeconds: Double, block: () -> Unit): Command =
    if (Library.isSimulated) this.alongWith(Commands.waitSeconds(delaySeconds).andThen(Commands.runOnce(block))) else this

fun Command.afterSimCondition(condition: () -> Boolean, block: () -> Unit): Command =
    if (Library.isSimulated) this.alongWith(Commands.waitUntil(condition).andThen(Commands.runOnce(block))) else this