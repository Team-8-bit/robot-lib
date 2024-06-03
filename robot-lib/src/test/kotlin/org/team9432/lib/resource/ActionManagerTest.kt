package org.team9432.lib.resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ActionManagerTest {
    @RepeatedTest(20)
    fun actionAssignment(): Unit = runTest {
        // Run the animation manager in the background
        backgroundScope.launch { ActionManager.run() }

        // Declare a resource to test with
        val resource = object: Resource("Test Resource") {}

        // Use the resource
        launch { use(resource, name = "Test Action") { delay(1) } }
        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the resource is being used
        assertTrue(resource.isInUse)
        // Wait 2ms for it to finish
        testScheduler.advanceTimeBy(2)
        // Make sure the resource is free
        assertTrue(resource.isFree)
    }

    @Test
    fun actionCancellation(): Unit = runTest {
        // Run the animation manager in the background
        backgroundScope.launch { ActionManager.run() }

        // Declare a resource to test with
        val resource = object: Resource("Test Resource") {}

        // Use the first resource
        launch { use(resource, name = "Initial Action") { delay(10) } }
        testScheduler.runCurrent()

        // Override with the new task
        launch { use(resource, name = "Override Action", cancelConflicts = true) { delay(10) } }
        testScheduler.runCurrent()

        assertTrue(resource.currentActionName == "Override Action")
    }

    // Opposite of above
    @Test
    fun actionNonCancellation(): Unit = runTest {
        // Run the animation manager in the background
        backgroundScope.launch { ActionManager.run() }

        // Declare a resource to test with
        val resource = object: Resource("Test Resource") {}

        // Use the first resource
        launch { use(resource, name = "Initial Action") { delay(10) } }
        testScheduler.runCurrent()

        // Override with the new task
        launch { use(resource, name = "Override Action", cancelConflicts = false) { delay(10) } }
        testScheduler.runCurrent()

        assertTrue(resource.currentActionName == "Initial Action")
    }
}

