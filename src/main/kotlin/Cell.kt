package de.fhkiel.rob.legoosctester

data class Cell(
    val north : CellBoarder,
    val east : CellBoarder,
    val south : CellBoarder,
    val west : CellBoarder
)

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
