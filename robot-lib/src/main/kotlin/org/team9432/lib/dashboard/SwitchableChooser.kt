package org.team9432.lib.dashboard

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import org.team9432.lib.RobotPeriodicManager

private const val placeholder: String = "<NA>"

// https://github.com/Mechanical-Advantage/RobotCode2024/blob/main/src/main/java/org/littletonrobotics/frc2024/util/SwitchableChooser.java
class SwitchableChooser(name: String) {
    private var options = arrayOf(placeholder)
    private var active: String? = placeholder

    private val table: NetworkTable = NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(name)
    private val namePublisher = table.getStringTopic(".name").publish()
    private val typePublisher = table.getStringTopic(".type").publish()
    private val optionsPublisher = table.getStringArrayTopic("options").publish()
    private val defaultPublisher = table.getStringTopic("default").publish()
    private val activePublisher = table.getStringTopic("active").publish()
    private val selectedPublisher = table.getStringTopic("selected").publish()
    private val selectedInput = table.getStringTopic("selected").subscribe("")

    init {
        namePublisher.set(name)
        typePublisher.set("String Chooser")
        optionsPublisher.set(this.options)
        defaultPublisher.set(this.options.first())
        activePublisher.set(this.options.first())
        selectedPublisher.set(this.options.first())

        RobotPeriodicManager.startPeriodic { periodic() }
    }

    /** Updates the set of available options.  */
    fun setOptions(options: Array<String>) {
        if (options.contentEquals(this.options)) return
        this.options = if (options.isNotEmpty()) options else arrayOf(placeholder)
        optionsPublisher.set(this.options)
        periodic()
    }

    /** Returns the selected option.  */
    fun get(): String? = if (active == placeholder) null else active

    private fun periodic() {
        val selected = selectedInput.get()

        active = options.firstOrNull { it != placeholder && it == selected }

        if (active == null) {
            active = options.first()
            selectedPublisher.set(active)
        }

        defaultPublisher.set(active)
        activePublisher.set(active)
    }
}