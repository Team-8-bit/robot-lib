package org.team9432.lib.resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class NestedAction {
    @Test
    fun independentNestedActions() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}

        launch {
            use(resourceOne, name = "Base Action") {
                delay(1)
                use(resourceTwo, name = "Nested Action One") { delay(1) }
                delay(1)
            }
        }

        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the first resource is being used
        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertEquals("Nested Action One", resourceTwo.currentActionName)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertTrue(resourceTwo.isFree)
    }

    @Test
    fun sharedNestedActions() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}

        launch {
            use(resourceOne, name = "Base Action") {
                delay(1)
                use(resourceOne, resourceTwo, name = "Nested Action One") { delay(1) }
                delay(1)
            }
        }

        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the first resource is being used
        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertEquals("Nested Action One", resourceTwo.currentActionName)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertTrue(resourceTwo.isFree)
    }

    @Test
    fun conflictingNestedActions() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}

        launch {
            use(resourceOne, name = "Base Action") {
                delay(1)
                use(resourceOne, resourceTwo, name = "Nested Action One", cancelConflicts = true) { delay(1) }
                delay(1)
            }
        }

        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the first resource is being used
        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertEquals("Nested Action One", resourceTwo.currentActionName)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertTrue(resourceTwo.isFree)
    }


    @Test
    fun nestedActionCancellation() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}

        launch {
            use(resourceOne, name = "Base Action") {
                delay(1)
                use(resourceOne, resourceTwo, name = "Nested Action One", cancelConflicts = true) { delay(1) }
                delay(1)
            }
        }

        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the first resource is being used
        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertEquals("Nested Action One", resourceTwo.currentActionName)

        launch {
            use(resourceTwo, name = "Nested Cancellation", cancelConflicts = true) { delay(1) }
        }
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertEquals("Nested Cancellation", resourceTwo.currentActionName)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertTrue(resourceTwo.isFree)
    }

    @Test
    fun nestedActionBaseCancellation() = runTest {
        // Run the action manager in the background
        backgroundScope.launch { ActionManager.run() }

        val resourceOne = object: Resource("Resource One") {}
        val resourceTwo = object: Resource("Resource Two") {}

        launch {
            use(resourceOne, name = "Base Action") {
                delay(1)
                use(resourceTwo, name = "Nested Action One", cancelConflicts = true) { delay(1) }
                delay(1)
            }
        }

        testScheduler.runCurrent() // Runs the coroutine scheduled above

        // Make sure the first resource is being used
        assertEquals("Base Action", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertEquals("Base Action", resourceOne.currentActionName)
        assertEquals("Nested Action One", resourceTwo.currentActionName)

        launch {
            use(resourceOne, name = "Base Cancellation", cancelConflicts = true) { delay(1) }
        }
        testScheduler.runCurrent()

        assertEquals("Base Cancellation", resourceOne.currentActionName)
        assertTrue(resourceTwo.isFree)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        assertTrue(resourceOne.isFree)
        assertTrue(resourceTwo.isFree)
    }
}

