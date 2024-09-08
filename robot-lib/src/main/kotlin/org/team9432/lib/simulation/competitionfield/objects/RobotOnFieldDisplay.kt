package org.team9432.lib.simulation.competitionfield.objects

import org.team9432.lib.simulation.competitionfield.CompetitionFieldVisualizer.Object2dOnFieldDisplay

/**
 * displays a robot on field
 * note that the main robot also inherits this class,
 * but it will not be displayed in "Robots" Pose3d array
 * it will be displayed as "Robot" and with a single Pose2d
 */
interface RobotOnFieldDisplay: Object2dOnFieldDisplay {
    override val typeName: String
        get() = "Robots"
}
