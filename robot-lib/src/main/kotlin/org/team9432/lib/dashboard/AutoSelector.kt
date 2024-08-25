package org.team9432.lib.dashboard

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import java.util.*

/**
 * Class for selecting different auto configurations.
 */
class AutoSelector(private val choosers: Set<DashboardQuestion>, buildQuestions: AutoSelectorQuestionScope.() -> Unit) {
    private val baseQuestions = AutoSelectorQuestionScope().apply(buildQuestions)

    /** Updates this selector's questions based on the current state of the dashboard chooser. */
    fun update() {
        // Create a queue of all the available choosers
        val chooserQueue: Queue<DashboardQuestion> = LinkedList(choosers)

        // Update the choosers with questions from the auto selector
        baseQuestions.update(chooserQueue)

        // Blank out any that weren't set
        chooserQueue.forEach { it.clear() }
    }

    class AutoSelectorQuestionScope internal constructor() {
        private val questions = mutableListOf<AutoSelectorOptionScope>()

        /** Add a new question */
        fun addQuestion(question: String, addOptions: AutoSelectorOptionScope.() -> Unit) {
            questions.add(AutoSelectorOptionScope(question).apply(addOptions))
        }

        internal fun update(choosers: Queue<DashboardQuestion>) {
            questions.forEach { it.update(choosers) }
        }
    }

    class AutoSelectorOptionScope internal constructor(private val question: String) {
        private val options = mutableMapOf<String, AutoSelectorQuestionScope?>()

        /** Add an option to the question, with optional nested questions to show if this option is selected. */
        fun addOption(answer: String, buildQuestions: (AutoSelectorQuestionScope.() -> Unit)? = null) {
            if (buildQuestions != null) {
                options[answer] = AutoSelectorQuestionScope().apply(buildQuestions)
            } else {
                options[answer] = null
            }
        }

        internal fun update(choosers: Queue<DashboardQuestion>) {
            // Take a chooser from the list
            // This also removes the chooser so that the same one is never set twice in one loop
            val targetChooser = choosers.poll()

            // Display the question and options
            targetChooser.set(question, options.keys)

            // Pass the remaining choosers to any nested questions
            val answer = targetChooser.chooser.get()
            options[answer]?.update(choosers)
        }
    }

    data class DashboardQuestion(private val chooserKey: String, private val questionKey: String) {
        internal val chooser = SwitchableChooser(chooserKey)

        internal fun clear() = set("", emptySet())

        internal fun set(question: String, options: Collection<String>) {
            SmartDashboard.putString(questionKey, question)
            chooser.setOptions(options.toTypedArray())
        }
    }
}