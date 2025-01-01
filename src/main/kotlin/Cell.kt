package de.fhkiel.rob.legoosctester

import java.awt.Color

data class Cell(
    val north : CellBoarder,
    val east : CellBoarder,
    val south : CellBoarder,
    val west : CellBoarder,
    val color: Color = Color.LIGHT_GRAY
)

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
