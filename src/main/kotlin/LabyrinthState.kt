package de.fhkiel.rob.legoosctester

class LabyrinthState(rows: Int, columns: Int) : LabyrinthStateService {
    private val labyrinth : Array<Array<Cell?>> = Array(rows) { Array(columns) {null}}
    private val currentX : Int = rows/2
    private val currentY : Int = columns


    override fun updateCell(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun getCell(x: Int, y: Int): Cell {
        TODO("Not yet implemented")
    }

    override fun getRobotPosition(): Pair<Int, Int> {
        TODO("Not yet implemented")
    }

    override fun setRobotPosition(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

}