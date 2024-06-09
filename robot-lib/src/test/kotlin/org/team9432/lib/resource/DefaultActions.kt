package org.team9432.lib.resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultActions {
    @Test
    fun singleDefaultCall() = runTest {
        backgroundScope.launch { ActionManager.run() }

        var defaultCalledCount = 0
        val resource = object: Resource("Resource") {
            override val defaultAction: Action = { defaultCalledCount++ }
        }

        launch { use(resource, name = "Action") { delay(1) } }
        testScheduler.runCurrent()

        assertTrue(resource.isInUse)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        // Make sure the resource has called its default
        assertTrue(resource.isRunningDefault)
        assertEquals(1, defaultCalledCount)
    }

    @Test
    fun expiringDefault() = runTest {
        backgroundScope.launch { ActionManager.run() }

        var defaultCalledCount = 0
        val resource = object: Resource("Resource") {
            override val defaultAction: Action = { defaultCalledCount++ }
        }

        launch { use(resource, name = "Action") { delay(1) } }
        testScheduler.runCurrent()

        assertTrue(resource.isInUse)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        // Make sure the resource has called its default
        assertTrue(resource.isRunningDefault)
        assertEquals(1, defaultCalledCount)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        // And make sure it's still running and hasn't been called again
        assertTrue(resource.isRunningDefault)
        assertEquals(1, defaultCalledCount)
    }

    @Test
    fun intermediaryDefault() = runTest {
        backgroundScope.launch { ActionManager.run() }

        var defaultCalledCount = 0
        val resource = object: Resource("Resource") {
            override val defaultAction: Action = { defaultCalledCount++; delay(1) }
        }

        launch { use(resource, name = "Action") { delay(1) } }
        testScheduler.runCurrent()

        assertTrue(resource.isInUse)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        // Make sure the resource has called its default
        assertTrue(resource.isRunningDefault)
        assertEquals(1, defaultCalledCount)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        launch { use(resource, name = "Override Action", cancelConflicts = true) { delay(1) } }
        testScheduler.runCurrent()

        // Ensure default has been overridden
        assertEquals("Override Action", resource.currentActionName)
        assertEquals(1, defaultCalledCount)

        testScheduler.advanceTimeBy(1)
        testScheduler.runCurrent()

        // Make sure the default is called again
        assertTrue(resource.isRunningDefault)
        assertEquals(2, defaultCalledCount)
    }
}

