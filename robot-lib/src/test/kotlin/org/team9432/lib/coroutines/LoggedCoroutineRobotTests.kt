package org.team9432.lib.coroutines

import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.simulation.SimHooks
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.littletonrobotics.junction.LoggedRobot
import kotlin.test.Test

internal class LoggedCoroutineRobotTests {
    @BeforeEach
    fun setup() {
        HAL.initialize(500, 0)
        SimHooks.pauseTiming()
    }

    @AfterEach
    fun teardown() {
        LoggedCoroutineRobot.close()
    }

    @Test
    fun aeiou() = runTest {
        val robot = object: LoggedRobot() {}

        val robotThread = Thread(robot::startCompetition)
        robotThread.start()


        robot.endCompetition()
        try {
            robotThread.interrupt()
            robotThread.join()
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}