package de.fhkiel.rob.legoosctester

data class Cell(
    val top : CellBoarder,
    val bottom : CellBoarder,
    val left : CellBoarder,
    val right : CellBoarder
)

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
