package org.team9432.lib.coroutines

import edu.wpi.first.wpilibj.DriverStation.Alliance
import kotlinx.coroutines.CoroutineScope

interface Team8BitRobot {
    val alliance: Alliance?
    val isSimulated: Boolean
    val isNotSimulated: Boolean get() = !isSimulated
    val mode: Mode
    val coroutineScope: CoroutineScope

    enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST;

        /** True if the robot is disabled. */
        val isDisabled get() = this == DISABLED

        /** True if the robot is enabled in autonomous mode. */
        val isAutonomous get() = this == AUTONOMOUS

        /** True if the robot is enabled in teleop mode. */
        val isTeleop get() = this == TELEOP

        /** True if the robot is enabled in test mode. */
        val isTest get() = this == TEST
    }
}