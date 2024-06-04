package org.team9432.lib.resource

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ActionConflict {
    @Test
    fun singleResourceConflict() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        // Declare a resource to test with
        val resource = object: Resource("Test Resource") {}

        // Use the first resource
        launch { use(resource, name = "Initial Action") { delay(10) } }
        testScheduler.runCurrent()

        // Override with the new task
        launch { use(resource, name = "Override Action", cancelConflicts = true) { delay(10) } }
        testScheduler.runCurrent()

        assertEquals("Override Action", resource.currentActionName)

        // Fail to override
        launch { use(resource, name = "Non-Overriding Action", cancelConflicts = false) { delay(10) } }
        testScheduler.runCurrent()

        assertEquals("Override Action", resource.currentActionName)
    }

    @Test
    fun multipleResourceConflict() = runTest {
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}
        val resourceThree = object: Resource("Resource Three") {}

        // Use the first resource
        launch { use(resourceOne, resourceTwo, name = "Initial Action") { delay(10) } }
        testScheduler.runCurrent()

        // Override with the new task
        launch { use(resourceTwo, resourceThree, name = "Override Action", cancelConflicts = true) { delay(10) } }
        testScheduler.runCurrent()

        assertTrue { resourceOne.isFree }
        assertTrue { resourceTwo.activeJob == resourceThree.activeJob }

        // Fail to override
        launch { use(resourceOne, resourceThree, name = "Non-Overriding Action", cancelConflicts = false) { delay(10) } }
        testScheduler.runCurrent()

        assertTrue { resourceOne.isFree }
        assertTrue { resourceTwo.activeJob == resourceThree.activeJob }
    }
}

