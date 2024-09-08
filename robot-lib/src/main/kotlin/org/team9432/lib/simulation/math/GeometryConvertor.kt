package org.team9432.lib.simulation.math

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.dyn4j.geometry.Rotation
import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2

/**
 * utils to convert between WPILIB and dyn4j geometry classes
 */
object GeometryConvertor {
    fun toDyn4jVector2(wpilibTranslation2d: Translation2d): Vector2 {
        return Vector2(wpilibTranslation2d.getX(), wpilibTranslation2d.getY())
    }

    fun toWpilibTranslation2d(dyn4jVector2: Vector2): Translation2d {
        return Translation2d(dyn4jVector2.x, dyn4jVector2.y)
    }

    fun toDyn4jRotation(wpilibRotation2d: Rotation2d): Rotation {
        return Rotation(wpilibRotation2d.getRadians())
    }

    fun toWpilibRotation2d(dyn4jRotation: Rotation): Rotation2d {
        return Rotation2d(dyn4jRotation.toRadians())
    }

    fun toDyn4jTransform(wpilibPose2d: Pose2d): Transform {
        val transform = Transform()
        transform.setTranslation(toDyn4jVector2(wpilibPose2d.translation))
        transform.setRotation(toDyn4jRotation(wpilibPose2d.rotation))
        return transform
    }

    fun toWpilibPose2d(dyn4jTransform: Transform): Pose2d {
        return Pose2d(
            toWpilibTranslation2d(dyn4jTransform.translation),
            toWpilibRotation2d(dyn4jTransform.rotation)
        )
    }

    fun toDyn4jLinearVelocity(wpilibChassisSpeeds: ChassisSpeeds): Vector2 {
        return Vector2(wpilibChassisSpeeds.vxMetersPerSecond, wpilibChassisSpeeds.vyMetersPerSecond)
    }

    fun toWpilibChassisSpeeds(dyn4jLinearVelocity: Vector2, angularVelocityRadPerSec: Double): ChassisSpeeds {
        return ChassisSpeeds(
            dyn4jLinearVelocity.x,
            dyn4jLinearVelocity.y,
            angularVelocityRadPerSec
        )
    }
}
