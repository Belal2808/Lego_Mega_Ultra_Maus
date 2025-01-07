package de.fhkiel.rob.legoosctester

import java.awt.Color

data class Cell(
    val borders: MutableMap<RoboterDirection, CellBoarder> = mutableMapOf(
        RoboterDirection.NORTH to CellBoarder.UNDISCOVERED,
        RoboterDirection.EAST to CellBoarder.UNDISCOVERED,
        RoboterDirection.SOUTH to CellBoarder.UNDISCOVERED,
        RoboterDirection.WEST to CellBoarder.UNDISCOVERED
    ), val color: Color = Color.LIGHT_GRAY, // Standardfarbe
    val isEntrance: Boolean = false, // Eingang
    val isColorField: Boolean = false, // Farbfeld
    val priority: Int = 0, // Priorität für Farbplatten
    val isBlocked: Boolean = false, //
){
    fun getBorder(direction: RoboterDirection): CellBoarder = borders[direction] ?: CellBoarder.UNDISCOVERED

    fun setBorder(direction: RoboterDirection, border: CellBoarder) {
        borders[direction] = border
    }
}

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
