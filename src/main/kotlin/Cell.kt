package de.fhkiel.rob.legoosctester

import java.awt.Color

data class Cell(
    val borders: MutableMap<RoboterDirection, CellBoarder> = mutableMapOf(
        RoboterDirection.NORTH to CellBoarder.NONE,
        RoboterDirection.EAST to CellBoarder.NONE,
        RoboterDirection.SOUTH to CellBoarder.NONE,
        RoboterDirection.WEST to CellBoarder.NONE
    ),

    var color: Color = Color.LIGHT_GRAY, // Standardfarbe
    var isEntrance: Boolean = false, // Eingang
    var isColorField: Boolean = false, // Farbfeld
    var priority: Int = 0, // Priorität für Farbplatten
    var isBlocked: Boolean = false, //
){
    fun getBorder(direction: RoboterDirection): CellBoarder = borders[direction] ?: CellBoarder.NONE

    fun setBorder(direction: RoboterDirection, border: CellBoarder) {
        borders[direction] = border
    }

    fun getUndiscoveredBorders(): List<RoboterDirection> {
        return borders.filter { it.value == CellBoarder.NONE }
            .map { it.key }
    }
}

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED,NONE
}
