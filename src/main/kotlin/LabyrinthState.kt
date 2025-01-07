package de.fhkiel.rob.legoosctester

class LabyrinthState(rows: Int, columns: Int) : LabyrinthStateService {
    private val labyrinth : Array<Array<Cell?>> = Array(rows) { Array(columns) {null}}
    private var currentX : Int = rows/2
    private var currentY : Int = columns/2


    override fun updateCell(x: Int, y: Int, cell: Cell) {
        labyrinth[x][y] = cell
    }


    override fun getCell(position: Pair<Int, Int>): Cell? {
            return labyrinth[position.first][position.second]
    }

    override fun getRobotPosition(): Pair<Int, Int> {
        return Pair(currentX, currentY)
    }

    override fun setRobotPosition(x: Int, y: Int) {
        currentX = x
        currentY = y
    }
}