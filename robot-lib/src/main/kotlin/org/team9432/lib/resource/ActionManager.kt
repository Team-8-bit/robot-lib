package org.team9432.lib.resource

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.*

internal object ActionManager {
    private const val DEBUG = false

    private val messageChannel = Channel<Message>(capacity = Channel.UNLIMITED)

    private lateinit var actionScope: CoroutineScope

    suspend fun run() = coroutineScope {
        actionScope = this

        for (message in messageChannel) {
            handleMessage(message)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleMessage(message: Message) {
        when (message) {
            is Message.ScheduleAction -> {
                // resources required by the coroutine starting this action
                val parentResources: Set<Resource> = message.callerContext[Requirements] ?: emptySet()
                // newly required resources (not including ones already required by the parent)
                val thisResources = message.requirements - parentResources

                val allResources = message.requirements + parentResources

                // find conflicting subsystems by checking weather or not they have an active job
                val conflictingResources = thisResources.filter { it.activeJob != null }

                // verify that all conflicting subsystems can be canceled
                if (!message.cancelConflicts && conflictingResources.isNotEmpty()) {
                    message.continuation.resumeWithException(
                        CancellationException("Action ${message.name} is not allowed to cancel conflicts that are using { ${conflictingResources.joinToString { it.name }} }")
                    )
                    return
                }

                val conflictingJobs = conflictingResources.map { it.activeJob!! }

                // spawn action coroutine
                val actionJob = actionScope.launch(message.callerContext + Requirements(allResources), CoroutineStart.ATOMIC) {
                    val job = coroutineContext[Job]!!

                    try {
                        printDebug("Resources { ${thisResources.joinToString { it.name }} } are being used by ${message.name}")
                        if (message.name == null || message.name.toString() == "null") {
                            printDebug("message.name = null: ${message.action}")
                        }

                        // Cancel conflicting jobs. It is critical that coroutines must not complete until it's conflict's jobs have finished execution
                        withContext(NonCancellable) {
                            conflictingJobs.forEach { it.cancel() }
                            conflictingJobs.joinAll()
                        }

                        // run the action
                        coroutineScope { message.action.invoke(this) }

                        // resume calling coroutine
                        message.continuation.resume(Unit)

                    } catch (exception: Throwable) {
                        // pass exception to calling coroutine
                        message.continuation.resumeWithException(exception)
                    } finally {
                        // tell the scheduler that the action job has finished executing
                        messageChannel.trySend(Message.ReleaseResources(thisResources, job))
                    }
                }

                // write over state - future actions that use these subsystems will cancel and wait for the action job to complete
                // only writes newly required subsystems and does not overwrite parent requirements
                thisResources.forEach {
                    it.activeJob = actionJob
                    it.currentActionName = message.name
                }
            }

            is Message.ReleaseResources -> {
                message.resources
                    .filter { it.activeJob === message.job }
                    .forEach { resource ->
                        resource.activeJob = null
                        resource.currentActionName = null
                        printDebug("released ${resource.name}")

                        if (resource.hasDefault) {
                            actionScope.launch {
                                use(resource, name = "Default", cancelConflicts = false) { resource.default() }
                            }
                        }
                    }
            }
        }
    }

    private fun printDebug(message: String) = if (DEBUG) println(message) else { /* no-op */
    }

    internal suspend fun useResources(
        resources: Set<Resource>,
        name: String?,
        cancelConflicts: Boolean,
        action: Action,
    ) {
        val context = coroutineContext

        suspendCancellableCoroutine { cont ->
            val message = Message.ScheduleAction(
                resources,
                context,
                action,
                name,
                cancelConflicts,
                cont
            )
            messageChannel.trySend(message)
        }
    }

    private sealed class Message {
        class ScheduleAction(
            val requirements: Set<Resource>,
            val callerContext: CoroutineContext,
            val action: Action,
            val name: String?,
            val cancelConflicts: Boolean,
            val continuation: CancellableContinuation<Unit>,
        ): Message()

        class ReleaseResources(val resources: Set<Resource>, val job: Job): Message()
    }


    private class Requirements(
        requirements: Set<Resource>,
    ): Set<Resource> by requirements, AbstractCoroutineContextElement(Key) {
        companion object Key: CoroutineContext.Key<Requirements>
    }
}

/**
 * Attempts to run the provided [action] with exclusive access to all provided [resources].
 *
 * If [cancelConflicts] is set to false and one of the [resources] is being used by another coroutine,
 * an exception will be thrown and the provided [action] will not be invoked. If it is set to true, all coroutines requiring
 * any of the [resources] will be cancelled and completed before the [action] is invoked.
 *
 * Use calls are re-entrant, meaning if a coroutine using subsystems A and B calls [use] with subsystems B and C,
 * the code inside the nested [use] call's [action] will effectively be using subsystems A, B, and C, instead of
 * cancelling itself.
 */
suspend fun use(vararg resources: Resource, name: String? = null, cancelConflicts: Boolean = false, action: Action) =
    ActionManager.useResources(resources.toSet(), name, cancelConflicts, action)