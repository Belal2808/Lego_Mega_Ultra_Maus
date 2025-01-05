package de.fhkiel.rob.legoosctester

import java.awt.Color

data class Cell(
    val north : CellBoarder,
    val east : CellBoarder,
    val south : CellBoarder,
    val west : CellBoarder,
    val color: Color = Color.LIGHT_GRAY, // Standardfarbe
    val isEntrance: Boolean = false, // Eingang
    val isColorField: Boolean = false, // Farbfeld
    val priority: Int = 0, // Priorität für Farbplatten
    val isBlocked: Boolean = false, // Unerreichbar
)

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
