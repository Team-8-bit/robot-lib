package org.team9432.lib.resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ActionAssignment {
    @RepeatedTest(20)
    fun singleActionAssignment() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        // Declare a resource to test with
        val resource = object: Resource("Resource") {}

        // Use the resource
        launch { use(resource, name = "Action") { delay(1) } }
        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the resource is being used
        assertTrue(resource.isInUse)
        // Wait 2ms for it to finish
        testScheduler.advanceTimeBy(2)
        // Make sure the resource is free
        assertTrue(resource.isFree)
    }

    @RepeatedTest(20)
    fun multipleActionAssignment() = runTest {
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}
        val resourceThree = object: Resource("Resource Three") {}

        launch { use(resourceOne, resourceTwo, name = "One Two Action") { delay(1) } }
        launch { use(resourceThree, name = "Three Action") { delay(3) } }
        testScheduler.runCurrent()

        // Make sure the correct resources are being used in the correct places
        assertTrue { resourceOne.activeJob == resourceTwo.activeJob }
        assertTrue { resourceOne.isInUse && resourceTwo.isInUse }
        assertTrue { resourceOne.currentActionName == "One Two Action" }
        assertTrue { resourceThree.currentActionName == "Three Action" && resourceThree.isInUse }

        // Wait 2ms for the first task to finish
        testScheduler.advanceTimeBy(2)

        // Make sure the correct resources are free
        assertTrue { resourceOne.isFree && resourceTwo.isFree }
        assertTrue { resourceThree.isInUse }

        // Wait 2ms more for the second task to finish
        testScheduler.advanceTimeBy(2)

        assertTrue { resourceOne.isFree && resourceTwo.isFree && resourceThree.isFree }
    }
}

