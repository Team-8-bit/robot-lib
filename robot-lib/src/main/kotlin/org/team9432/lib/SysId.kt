package org.team9432.lib

import edu.wpi.first.units.Units.Seconds
import edu.wpi.first.units.Units.Volts
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction
import org.littletonrobotics.junction.Logger
import org.team9432.lib.resource.Action
import org.team9432.lib.resource.toAction
import edu.wpi.first.wpilibj2.command.Subsystem as WPISubsystem


/** Sysid config wrapper that hides the Java unit library. */
class KSysIdConfig(
    /** The voltage ramp rate used for quasistatic test routines in volts/sec. */
    rampRate: Double? = null,

    /** The step voltage output used for dynamic test routines. */
    stepVoltage: Double? = null,

    /** Safety timeout for the test routine commands in seconds. */
    timeout: Double? = null,

    /** Method to record the current state for importing into SysId*/
    recordState: (String) -> Unit = { Logger.recordOutput("SysIdState", it) },
): SysIdRoutine.Config(
    rampRate?.let { Volts.of(it).per(Seconds) },
    stepVoltage?.let { Volts.of(it) },
    timeout?.let { Seconds.of(it) },
    { state -> recordState.invoke(state.toString()) }
)

/** Sysid mechanism wrapper that hides the Java unit library and some options we don't use. */
class KSysIdMechanism(
    /** Sends the SysId-specified drive signal to the mechanism motors during test routines. */
    drive: ((Double) -> Unit)? = null,
): SysIdRoutine.Mechanism(drive?.let { { drive(it.`in`(Volts)) } }, null, object: WPISubsystem {}, "")

object SysIdUtil {
    /** Get a set of Sysid tests using the given parameters. */
    fun getSysIdTests(config: KSysIdConfig = KSysIdConfig(), setMotors: (Double) -> Unit): SysIdTestContainer {
        val routine = SysIdRoutine(
            config,
            KSysIdMechanism { volts -> setMotors(volts) }
        )

        return SysIdTestContainer(
            quasistaticForward = routine.quasistatic(Direction.kForward).toAction(),
            quasistaticReverse = routine.quasistatic(Direction.kReverse).toAction(),
            dynamicForward = routine.dynamic(Direction.kForward).toAction(),
            dynamicReverse = routine.dynamic(Direction.kReverse).toAction()
        )
    }
}

data class SysIdTestContainer(
    val quasistaticForward: Action,
    val quasistaticReverse: Action,
    val dynamicForward: Action,
    val dynamicReverse: Action,
)