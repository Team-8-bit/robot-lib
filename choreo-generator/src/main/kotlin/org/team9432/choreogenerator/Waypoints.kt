package org.team9432.choreogenerator

import org.team9432.choreogenerator.json.JsonChoreoWaypoint
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.inRadians

class ChoreoWaypoint internal constructor(internal val jsonWaypoint: JsonChoreoWaypoint)

fun Position.asPoseWaypoint() =
    ChoreoWaypoint(JsonChoreoWaypoint(x.inMeters, y.inMeters, heading.inRadians, isInitialGuess = false, translationConstrained = true, headingConstrained = true, controlIntervalCount = 0))

fun Position.asTranslationWaypoint() =
    ChoreoWaypoint(JsonChoreoWaypoint(x.inMeters, y.inMeters, heading.inRadians, isInitialGuess = false, translationConstrained = true, headingConstrained = false, controlIntervalCount = 0))