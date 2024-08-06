package org.team9432.lib.util

import com.choreo.lib.ChoreoTrajectory
import edu.wpi.first.math.geometry.Pose2d

fun ChoreoTrajectory.getAutoFlippedInitialPose(): Pose2d = allianceSwitch(blue = initialPose, red = flippedInitialPose)