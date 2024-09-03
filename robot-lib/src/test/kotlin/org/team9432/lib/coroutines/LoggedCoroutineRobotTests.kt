package org.team9432.lib.coroutines

import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.simulation.SimHooks
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class LoggedCoroutineRobotTests {
    private val period = LoggedCoroutineRobot.PERIOD

    @BeforeEach
    fun setup() {
        HAL.initialize(500, 0)
        SimHooks.pauseTiming()
    }

    @AfterEach
    fun cleanup() {
        SimHooks.resumeTiming()
    }

    class TestRobot: LoggedCoroutineRobot(coroutineDebugOutput = true) {
        var coroutineRunCount = 0

        fun runSimpleCoroutine(initialDelay: Duration, launchedDelay: Duration, finalDelay: Duration) {
            RobotScope.launch {
                delay(initialDelay)
                launch {
                    delay(launchedDelay)
                    coroutineRunCount++
                }
                delay(finalDelay)
            }
        }
    }

    @Test
    /** Runs a simple coroutine and make sure all the parts of it execute correctly and at the right times. */
    fun SimpleCoroutineTest() = runTest {
        // Robot object for testing
        val robot = TestRobot()
        Thread(robot::startCompetition).start()

        // Run a coroutine and ensure it completes each step in the correct amount of time
        assertEquals(0, robot.currentDelayedCount) // Nothing has happened yet
        assertEquals(0, robot.coroutineRunCount)

        robot.runSimpleCoroutine(initialDelay = 1.seconds, launchedDelay = 1.seconds, finalDelay = 2.seconds)

        assertEquals(0, robot.coroutineRunCount) // Nothing should have happened yet
        assertEquals(1, robot.currentDelayedCount) // The first delay should have started
        SimHooks.stepTiming(0.99)
        assertEquals(0, robot.coroutineRunCount) // Still nothing should have happened
        assertEquals(1, robot.currentDelayedCount) // The first delay should still be going
        SimHooks.stepTiming(0.01)
        assertEquals(0, robot.coroutineRunCount) // Now it should be running the second delay but without activating the count
        assertEquals(2, robot.currentDelayedCount) // Now running the first launch{}'d delay and the final two-second delay
        SimHooks.stepTiming(0.99) // Nothing should have changed
        assertEquals(0, robot.coroutineRunCount)
        assertEquals(2, robot.currentDelayedCount)
        SimHooks.stepTiming(0.01) // Now it should have activated the count and finished one of the delays
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(1, robot.currentDelayedCount)
        SimHooks.stepTiming(0.99) // Again, nothing should have changed
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(1, robot.currentDelayedCount)
        SimHooks.stepTiming(0.01) // All delays should be finished
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(0, robot.currentDelayedCount)

        robot.endCompetition()
        robot.close()
    }

    @Test
    /** Same as the above test, but uses delays that aren't synced with the robot loop. */
    fun NonSyncedCoroutineTest() = runTest {
        // Robot object for testing
        val robot = TestRobot()
        Thread(robot::startCompetition).start()

        // Run a coroutine and ensure it completes each step in the correct amount of time
        assertEquals(0, robot.currentDelayedCount) // Nothing has happened yet
        assertEquals(0, robot.coroutineRunCount)

        robot.runSimpleCoroutine(initialDelay = 0.03.seconds, launchedDelay = 0.01.seconds, finalDelay = 0.05.seconds)

        assertEquals(0, robot.coroutineRunCount) // Nothing should have happened yet
        assertEquals(1, robot.currentDelayedCount) // The first delay should have started
        SimHooks.stepTiming(period)
        assertEquals(0, robot.coroutineRunCount) // Still nothing should have happened
        assertEquals(1, robot.currentDelayedCount) // The first delay should still be going
        SimHooks.stepTiming(period)
        assertEquals(0, robot.coroutineRunCount) // Now it should be running the second delay but without activating the count
        assertEquals(2, robot.currentDelayedCount) // Now running the first launch{}'d delay and the final two-second delay
        SimHooks.stepTiming(period) // Now it should have activated the count and finished one of the delays
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(1, robot.currentDelayedCount)
        SimHooks.stepTiming(period) // Again, nothing should have changed
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(1, robot.currentDelayedCount)
        SimHooks.stepTiming(period) // All delays should be finished
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(0, robot.currentDelayedCount)

        robot.endCompetition()
        robot.close()
    }

    @Test
    /** Test multiple coroutines running at the same time. */
    fun ConcurrentCoroutineTest() = runTest {
        // Robot object for testing
        val robot = TestRobot()
        Thread(robot::startCompetition).start()

        // Run a coroutine and ensure it completes each step in the correct amount of time
        assertEquals(0, robot.currentDelayedCount) // Nothing has happened yet
        assertEquals(0, robot.coroutineRunCount)

        robot.runSimpleCoroutine(initialDelay = 1.seconds, launchedDelay = 1.seconds, finalDelay = 2.seconds)

        assertEquals(0, robot.coroutineRunCount) // Nothing should have happened yet
        assertEquals(1, robot.currentDelayedCount) // The first delay should have started
        SimHooks.stepTiming(1.0)
        assertEquals(0, robot.coroutineRunCount) // Now it should be running the second delay but without activating the count
        assertEquals(2, robot.currentDelayedCount) // Now running the first launch{}'d delay and the final two-second delay

        robot.runSimpleCoroutine(initialDelay = 0.5.seconds, launchedDelay = 0.2.seconds, finalDelay = 1.2.seconds) // Start another coroutine

        // Ensure the new delay has started
        assertEquals(0, robot.coroutineRunCount)
        assertEquals(3, robot.currentDelayedCount)

        SimHooks.stepTiming(0.5)

        // Ensure the new coroutine has started it's launched delay and final delay
        assertEquals(0, robot.coroutineRunCount)
        assertEquals(4, robot.currentDelayedCount)

        SimHooks.stepTiming(0.2)

        // Ensure the new coroutine has added to the count and finished the first delay
        assertEquals(1, robot.coroutineRunCount)
        assertEquals(3, robot.currentDelayedCount)

        SimHooks.stepTiming(0.8)

        // The original coroutine has added to the count and finished it's first delay
        assertEquals(2, robot.coroutineRunCount)
        assertEquals(2, robot.currentDelayedCount)

        SimHooks.stepTiming(0.2)

        // The second coroutine has finished entirely
        assertEquals(2, robot.coroutineRunCount)
        assertEquals(1, robot.currentDelayedCount)

        SimHooks.stepTiming(0.8)

        // The first coroutine has finished entirely
        assertEquals(2, robot.coroutineRunCount)
        assertEquals(0, robot.currentDelayedCount)

        robot.endCompetition()
        robot.close()
    }
}