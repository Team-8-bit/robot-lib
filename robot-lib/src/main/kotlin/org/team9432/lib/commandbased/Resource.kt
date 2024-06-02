package org.team9432.lib.commandbased

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

    var hasDefault = true

    open suspend fun default() {
        hasDefault = false
    }
}
