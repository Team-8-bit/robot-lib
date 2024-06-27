package org.team9432.lib.robot

import edu.wpi.first.hal.DriverStationJNI
import edu.wpi.first.hal.FRCNetComm.tInstances
import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.DSControlWord
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj.util.WPILibVersion
import kotlinx.coroutines.*
import org.team9432.lib.LibraryState
import org.team9432.lib.coroutines.CoroutineNotifier
import org.team9432.lib.input.Trigger
import org.team9432.lib.resource.Action
import org.team9432.lib.resource.ActionManager
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.milliseconds

lateinit var RobotScope: CoroutineScope
    private set

open class CoroutineRobot: RobotBase() {
    @Volatile
    private var shouldExit = false

    private val dsControlWord = DSControlWord()
    private var lastMode = Mode.NONE

    open suspend fun init() {}
    open suspend fun disabled() {}
    open suspend fun autonomous() {}
    open suspend fun teleop() {}
    open suspend fun test() {}
    open suspend fun periodic() {}

    private enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST
    }

    override fun startCompetition() = runBlocking {
        LibraryState.isSimulation = isSimulation()

        RobotScope = this

        // Report the use of the Kotlin Language for "FRC Usage Report" statistics
        HAL.report(tResourceType.kResourceType_Language, tInstances.kLanguage_Kotlin, 0, WPILibVersion.Version)

        val notifier = CoroutineNotifier(20.milliseconds)

        launch { ActionManager.run() }

        // Robot code initialization
        init()

        // Tell the DS that the robot is ready to be enabled
        DriverStationJNI.observeUserProgramStarting()

        // Tell the DS that the robot is ready to be enabled
        println("********** Robot program startup complete **********")

        while (isActive && !shouldExit) {
            notifier.suspendTime()

            DriverStation.refreshData()
            dsControlWord.refresh()

            val currentMode = when {
                dsControlWord.isDisabled -> Mode.DISABLED
                dsControlWord.isAutonomous -> Mode.AUTONOMOUS
                dsControlWord.isTeleop -> Mode.TELEOP
                dsControlWord.isTest -> Mode.TEST
                else -> Mode.NONE
            }

            if (currentMode != lastMode) {
                when (currentMode) {
                    Mode.NONE -> {}
                    Mode.DISABLED -> disabled()
                    Mode.AUTONOMOUS -> autonomous()
                    Mode.TELEOP -> teleop()
                    Mode.TEST -> test()
                }

                lastMode = currentMode
            }

            when (currentMode) {
                Mode.NONE -> {}
                Mode.DISABLED -> DriverStationJNI.observeUserProgramDisabled()
                Mode.AUTONOMOUS -> DriverStationJNI.observeUserProgramAutonomous()
                Mode.TELEOP -> DriverStationJNI.observeUserProgramTeleop()
                Mode.TEST -> DriverStationJNI.observeUserProgramTest()
            }

            Trigger.poll()
            periodics.map { launch { it.invoke(this) } }.joinAll()
            DriverStation.getAlliance().getOrNull()?.let { LibraryState.alliance = it }
            periodic()

            SmartDashboard.updateValues()
            LiveWindow.updateValues()
            Shuffleboard.update()

            if (isSimulation()) {
                HAL.simPeriodicBefore()
                HAL.simPeriodicAfter()
            }
        }

        notifier.close()
    }

    companion object {
        private val periodics = mutableListOf<Action>()

        fun addPeriodic(periodic: Action) {
            periodics.add(periodic)
        }
    }

    override fun endCompetition() {
        shouldExit = true
    }
}