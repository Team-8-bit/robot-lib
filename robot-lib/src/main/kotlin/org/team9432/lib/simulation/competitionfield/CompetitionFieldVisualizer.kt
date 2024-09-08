package org.team9432.lib.simulation.competitionfield

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Pose3d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.simulation.competitionfield.objects.GamePieceOnFlyDisplay
import org.team9432.lib.simulation.competitionfield.objects.RobotOnFieldDisplay

/**
 * visualizes a competition field on dashboard & Advantage Scope
 * displays robots, opponent robots and game pieces on field
 * the source of these information should either be captured from a vision system during a real competition
 * or by the Maple Physics Simulation during a simulated competition
 */
class CompetitionFieldVisualizer(val mainRobot: RobotOnFieldDisplay) {
    interface ObjectOnFieldDisplay {
        val typeName: String
        val pose3d: Pose3d
    }

    interface Object2dOnFieldDisplay: ObjectOnFieldDisplay {
        val objectOnFieldPose2d: Pose2d
        override val typeName: String
        override val pose3d: Pose3d
            get() {
                return Pose3d(objectOnFieldPose2d)
            }
    }

    private val objectsOnFieldWithGivenType: MutableMap<String?, MutableSet<ObjectOnFieldDisplay>> = HashMap()
    private val gamePiecesOnFlyDisplayWithGivenType: MutableMap<String?, MutableSet<GamePieceOnFlyDisplay>> = HashMap()

    fun addObject(`object`: ObjectOnFieldDisplay): ObjectOnFieldDisplay {
        if (!objectsOnFieldWithGivenType.containsKey(`object`.typeName)) objectsOnFieldWithGivenType[`object`.typeName] = HashSet()
        objectsOnFieldWithGivenType[`object`.typeName]!!.add(`object`)
        return `object`
    }

    fun deleteObject(`object`: ObjectOnFieldDisplay): ObjectOnFieldDisplay? {
        if (!objectsOnFieldWithGivenType.containsKey(`object`.typeName)) return null
        if (objectsOnFieldWithGivenType[`object`.typeName]!!.remove(`object`)) return `object`
        return null
    }

    fun addGamePieceOnFly(gamePieceOnFlyDisplay: GamePieceOnFlyDisplay): GamePieceOnFlyDisplay {
        addObject(gamePieceOnFlyDisplay)
        if (!gamePiecesOnFlyDisplayWithGivenType.containsKey(gamePieceOnFlyDisplay.typeName)) gamePiecesOnFlyDisplayWithGivenType.put(gamePieceOnFlyDisplay.typeName, HashSet())
        gamePiecesOnFlyDisplayWithGivenType[gamePieceOnFlyDisplay.typeName]!!.add(gamePieceOnFlyDisplay)
        return gamePieceOnFlyDisplay
    }

    fun clearObjectsWithGivenType(typeName: String?): Set<ObjectOnFieldDisplay> {
        if (!objectsOnFieldWithGivenType.containsKey(typeName)) return HashSet()
        val originalSet: Set<ObjectOnFieldDisplay> = objectsOnFieldWithGivenType[typeName]!!
        objectsOnFieldWithGivenType[typeName] = HashSet()
        return originalSet
    }

    fun updateObjectsToDashboardAndTelemetry() {
        removeGamePiecesOnFlyIfReachedTarget()
        for (typeName: String? in objectsOnFieldWithGivenType.keys) {
            val objects: Set<ObjectOnFieldDisplay> = objectsOnFieldWithGivenType[typeName]!!
            Logger.recordOutput("/Field/$typeName", *getPose3ds(objects))
        }

        Logger.recordOutput("/Field/Robot", mainRobot.objectOnFieldPose2d)
    }

    private fun removeGamePiecesOnFlyIfReachedTarget() {
        for (gamePieceSet: MutableSet<GamePieceOnFlyDisplay> in gamePiecesOnFlyDisplayWithGivenType.values) gamePieceSet.removeIf { gamePieceOnFlyDisplay: GamePieceOnFlyDisplay ->
            if (gamePieceOnFlyDisplay.isReached) deleteObject(gamePieceOnFlyDisplay)
            gamePieceOnFlyDisplay.isReached
        }
    }

    companion object {

        private fun getPose3ds(objects: Set<ObjectOnFieldDisplay>): Array<Pose3d> {
            return objects.map(ObjectOnFieldDisplay::pose3d).toTypedArray()
        }
    }
}
