package org.team9432.lib.util

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.constants.EvergreenFieldConstants
import org.team9432.lib.unit.*
import kotlin.math.atan2
import kotlin.math.hypot

/** Flips this [Pose2d] to the opposite side of a mirrored field. */
fun Pose2d.flip() = Pose2d(translation.flip(), rotation.flip())

/** Flips this [Translation2d] to the opposite side of a mirrored field. */
fun Translation2d.flip() = Translation2d(EvergreenFieldConstants.centerX + (EvergreenFieldConstants.centerX - x.meters), y.meters)

/** Flips this [Rotation2d] to the opposite side of a mirrored field. */
fun Rotation2d.flip(): Rotation2d = Rotation2d.fromDegrees((degrees + 180) * -1)

/** Flips this [Pose2d] to the correct side of a mirrored field based on the current alliance color. */
fun Pose2d.applyFlip() = allianceSwitch(blue = this, red = this.flip())

/** Flips this [Translation2d] to the correct side of a mirrored field based on the current alliance color. */
fun Translation2d.applyFlip() = allianceSwitch(blue = this, red = this.flip())

/** Flips this [Rotation2d] to the correct side of a mirrored field based on the current alliance color. */
fun Rotation2d.applyFlip() = allianceSwitch(blue = this, red = this.flip())


/** Returns the angle this pose would need to be at to point at the given pose. */
fun Translation2d.angleTo(pose: Translation2d) = atan2(pose.y - this.y, pose.x - this.x).radians

/** Returns the angle this pose would need to be at to point at the given pose. */
fun Pose2d.angleTo(pose: Pose2d) = atan2(pose.y - this.y, pose.x - this.x).radians

/** Returns true if this pose is within [epsilon] of the given pose. */
fun Translation2d.isNear(pose: Translation2d, epsilon: Length) = hypot(this.x - pose.x, this.y - pose.y) < epsilon.inMeters

/** Returns true if this pose is within [epsilon] of the given pose. */
fun Pose2d.isNear(pose: Pose2d, epsilon: Length) = hypot(this.x - pose.x, this.y - pose.y) < epsilon.inMeters

/** Return the distance from this pose to another. */
fun Translation2d.distanceTo(pose: Translation2d) = this.getDistance(pose).meters

/** Return the distance from this pose to another. */
fun Pose2d.distanceTo(pose: Pose2d) = this.translation.getDistance(pose.translation).meters

/** Returns a Pose2d at this translation with rotation to point at the given pose. */
fun Translation2d.pointAt(pose: Translation2d) = Pose2d(x, y, this.angleTo(pose))

/** Returns a Pose2d at this translation with rotation to point at the given pose. */
fun Translation2d.pointAt(pose: Pose2d) = Pose2d(x, y, this.angleTo(pose.translation))