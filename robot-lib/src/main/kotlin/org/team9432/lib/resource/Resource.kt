package org.team9432.lib.resource

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import kotlinx.coroutines.Job

/**
 * A robot subsystem. Subsystems are the basic unit of robot organization in the Command-based
 * framework; they encapsulate low-level hardware objects (motor controllers, sensors, etc.) and
 * provide methods through which they can be used by [KCommand]s. Subsystems are used by the
 * [ActionManager]'s resource management system to ensure multiple robot actions are not
 * "fighting" over the same hardware; Commands that use a subsystem should include that subsystem in
 * their [KCommand.requirements], and resources used within a subsystem should
 * generally remain encapsulated and not be shared by other parts of the robot.
 */
abstract class Resource(val name: String) {
    internal var activeJob: Job? = null
    var currentActionName: String? = null
        internal set

    val isFree: Boolean
        get() = activeJob == null

    val isInUse: Boolean
        get() = activeJob != null

    val isRunningDefault: Boolean
        get() = currentActionName == ActionManager.DEFAULT_ACTION_NAME

    /**
     * Default function to be called when the subsystem doesn't have another action. It will
     * automatically require this resource. It will also only be called once between actions,
     * and not upon resource creation.
     */
    open val defaultAction: Action? = null

    open fun akitUpdate() {}
}
