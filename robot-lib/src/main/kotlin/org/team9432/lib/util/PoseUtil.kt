package org.team9432.lib.util

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.constants.EvergreenFieldConstants
import org.team9432.lib.ifBlueElse
import org.team9432.lib.unit.Translation2d
import org.team9432.lib.unit.meters

/** Flips this [Pose2d] to the opposite side of a mirrored field. */
fun Pose2d.flip() = Pose2d(translation.flip(), rotation.flip())

/** Flips this [Translation2d] to the opposite side of a mirrored field. */
fun Translation2d.flip() = Translation2d(EvergreenFieldConstants.centerX + (EvergreenFieldConstants.centerX - x.meters), y.meters)

/** Flips this [Rotation2d] to the opposite side of a mirrored field. */
fun Rotation2d.flip(): Rotation2d = Rotation2d.fromDegrees((degrees + 180) * -1)

/** Flips this [Pose2d] to the correct side of a mirrored field based on the current alliance color. */
fun Pose2d.applyFlip() = this ifBlueElse this.flip()

/** Flips this [Translation2d] to the correct side of a mirrored field based on the current alliance color. */
fun Translation2d.applyFlip() = this ifBlueElse this.flip()

/** Flips this [Rotation2d] to the correct side of a mirrored field based on the current alliance color. */
fun Rotation2d.applyFlip() = this ifBlueElse this.flip()