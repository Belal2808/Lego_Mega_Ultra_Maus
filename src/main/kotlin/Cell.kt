package de.fhkiel.rob.legoosctester

data class Cell(
    val borders: MutableMap<RoboterDirection, CellBoarder> = mutableMapOf(
        RoboterDirection.NORTH to CellBoarder.UNDISCOVERED,
        RoboterDirection.EAST to CellBoarder.UNDISCOVERED,
        RoboterDirection.SOUTH to CellBoarder.UNDISCOVERED,
        RoboterDirection.WEST to CellBoarder.UNDISCOVERED
    )
){
    fun getBorder(direction: RoboterDirection): CellBoarder = borders[direction] ?: CellBoarder.UNDISCOVERED

    fun setBorder(direction: RoboterDirection, border: CellBoarder) {
        borders[direction] = border
    }
}

enum class CellBoarder{
    WALL, UNDISCOVERED, DISCOVERED
}
