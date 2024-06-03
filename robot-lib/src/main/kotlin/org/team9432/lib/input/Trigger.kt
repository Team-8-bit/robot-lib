package org.team9432.lib.input

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.team9432.lib.resource.Action
import org.team9432.lib.robot.RobotScope
import java.util.function.BooleanSupplier

/**
 * This class provides an easy way to link commands to conditions.
 *
 *
 * It is very easy to link a button to a command. For instance, you could link the trigger button
 * of a joystick to a "score" command.
 *
 *
 * Triggers can easily be composed for advanced functionality using the [.and], [.or], [.negate] operators.
 *
 *
 * This class is provided by the NewCommands VendorDep
 */
class Trigger(private val condition: () -> Boolean): () -> Boolean {
    companion object {
        private val buttons = mutableListOf<() -> Unit>()

        fun addButton(button: () -> Unit) {
            buttons.add(button)
        }

        fun poll() {
            buttons.forEach { it.invoke() }
        }
    }

    /**
     * Starts the given command whenever the condition changes from `false` to `true`.
     *
     * @param action the command to start
     * @return this trigger, so calls can be chained
     */
    fun onTrue(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null

        addButton {
            val pressed = condition()
            if (!pressedLast && pressed) {
                val previousJob = job
                job = RobotScope.launch {
                    previousJob?.cancelAndJoin()
                    action()
                }
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Starts the given command whenever the condition changes from `true` to `false`.
     *
     * @param action the command to start
     * @return this trigger, so calls can be chained
     */
    fun onFalse(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null

        addButton {
            val pressed = condition()
            if (pressedLast && !pressed) {
                val previousJob = job
                job = RobotScope.launch {
                    previousJob?.cancelAndJoin()
                    action()
                }
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Starts the given command when the condition changes to `true` and cancels it when the condition
     * changes to `false`.
     *
     *
     * Doesn't re-start the command if it ends while the condition is still `true`. If the command
     * should restart, see [edu.wpi.first.wpilibj2.command.RepeatCommand].
     *
     * @param action the command to start
     * @return this trigger, so calls can be chained
     */
    fun whileTrue(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null

        addButton {
            val pressed = condition()
            if (!pressedLast && pressed) {
                val previousJob = job
                job = RobotScope.launch {
                    previousJob?.cancelAndJoin()
                    action()
                }
            } else if (pressedLast && !pressed) {
                job?.cancel()
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Starts the given command when the condition changes to `false` and cancels it when the
     * condition changes to `true`.
     *
     *
     * Doesn't re-start the command if it ends while the condition is still `false`. If the command
     * should restart, see [edu.wpi.first.wpilibj2.command.RepeatCommand].
     *
     * @param action the command to start
     * @return this trigger, so calls can be chained
     */
    fun whileFalse(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null
        addButton {
            val pressed = condition()
            if (pressedLast && !pressed) {
                val previousJob = job
                job = RobotScope.launch {
                    previousJob?.cancelAndJoin()
                    action()
                }
            } else if (!pressedLast && pressed) {
                job?.cancel()
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Toggles a command when the condition changes from `false` to `true`.
     *
     * @param action the command to toggle
     * @return this trigger, so calls can be chained
     */
    fun toggleOnTrue(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null

        addButton {
            val pressed = condition()
            val previousJob = job
            if (!pressedLast && pressed) {
                if (previousJob?.isActive == true) {
                    previousJob.cancel()
                } else {
                    job = RobotScope.launch {
                        previousJob?.cancelAndJoin()
                        action()
                    }
                }
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Toggles a command when the condition changes from `true` to `false`.
     *
     * @param action the command to toggle
     * @return this trigger, so calls can be chained
     */
    fun toggleOnFalse(action: Action): Trigger {
        var pressedLast = condition()
        var job: Job? = null

        addButton {
            val pressed = condition()
            val previousJob = job
            if (pressedLast && !pressed) {
                if (previousJob?.isActive == true) {
                    previousJob.cancel()
                } else {
                    job = RobotScope.launch {
                        previousJob?.cancelAndJoin()
                        action()
                    }
                }
            }
            pressedLast = pressed
        }

        return this
    }

    /**
     * Composes two triggers with logical AND.
     *
     * @param trigger the condition to compose with
     * @return A trigger which is active when both component triggers are active.
     */
    fun and(trigger: BooleanSupplier): Trigger {
        return Trigger { condition() && trigger.asBoolean }
    }

    /**
     * Composes two triggers with logical OR.
     *
     * @param trigger the condition to compose with
     * @return A trigger which is active when either component trigger is active.
     */
    fun or(trigger: BooleanSupplier): Trigger {
        return Trigger { condition() || trigger.asBoolean }
    }

    /**
     * Creates a new trigger that is active when this trigger is inactive, i.e. that acts as the
     * negation of this trigger.
     *
     * @return the negated trigger
     */
    fun negate(): Trigger {
        return Trigger { !condition() }
    }

    override fun invoke() = condition()
}
