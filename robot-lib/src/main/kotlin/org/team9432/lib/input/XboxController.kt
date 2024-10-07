package org.team9432.lib.input

import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.GenericHID
import kotlinx.coroutines.delay
import kotlin.time.Duration

/** XboxController with Trigger factories for easier command binding. */
class XboxController(
    port: Int,
    private val triggerButtonDistance: Double = 0.2,
): GenericHID(port) {
    enum class Button(val value: Int) {
        LEFT_BUMPER(5), RIGHT_BUMPER(6), LEFT_STICK(9), RIGHT_STICK(10), A(1), B(2), X(3), Y(4), BACK(7), START(8),
    }

    enum class Axis(val value: Int) {
        LEFT_X(0), RIGHT_X(4), LEFT_Y(1), RIGHT_Y(5), LEFT_TRIGGER(2), RIGHT_TRIGGER(3),
    }

    init {
        HAL.report(tResourceType.kResourceType_XboxController, port + 1)
    }

    val leftX get() = getRawAxis(Axis.LEFT_X.value)
    val leftY get() = getRawAxis(Axis.LEFT_Y.value)
    val rightX get() = getRawAxis(Axis.RIGHT_X.value)
    val rightY get() = getRawAxis(Axis.RIGHT_Y.value)
    val leftTriggerAxis get() = getRawAxis(Axis.LEFT_TRIGGER.value)
    val rightTriggerAxis get() = getRawAxis(Axis.RIGHT_TRIGGER.value)

    val leftBumper get() = Trigger { getRawButton(Button.LEFT_BUMPER.value) }
    val rightBumper get() = Trigger { getRawButton(Button.RIGHT_BUMPER.value) }
    val leftStick get() = Trigger { getRawButton(Button.LEFT_STICK.value) }
    val rightStick get() = Trigger { getRawButton(Button.RIGHT_STICK.value) }
    val leftTrigger get() = Trigger { leftTriggerAxis > triggerButtonDistance }
    val rightTrigger get() = Trigger { rightTriggerAxis > triggerButtonDistance }
    val a get() = Trigger { getRawButton(Button.A.value) }
    val b get() = Trigger { getRawButton(Button.B.value) }
    val x get() = Trigger { getRawButton(Button.X.value) }
    val y get() = Trigger { getRawButton(Button.Y.value) }
    val back get() = Trigger { getRawButton(Button.BACK.value) }
    val start get() = Trigger { getRawButton(Button.START.value) }

    val povUp get() = Trigger { getPOV(0) == 0 }
    val povRight get() = Trigger { getPOV(0) == 90 }
    val povDown get() = Trigger { getPOV(0) == 180 }
    val povLeft get() = Trigger { getPOV(0) == 270 }
    val povUpRight get() = Trigger { getPOV(0) == 45 }
    val povDownRight get() = Trigger { getPOV(0) == 135 }
    val povDownLeft get() = Trigger { getPOV(0) == 225 }
    val povUpLeft get() = Trigger { getPOV(0) == 315 }

    suspend fun rumbleDuration(duration: Duration) {
        setRumble(RumbleType.kBothRumble, 1.0)
        delay(duration)
        setRumble(RumbleType.kBothRumble, 0.0)
    }
}
