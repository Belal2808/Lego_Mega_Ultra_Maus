package de.fhkiel.rob.legoosctester

interface LabyrinthStateService {
    fun updateCell(x: Int, y: Int,cell: Cell)
    fun getCell(x: Int, y: Int): Cell?
    fun getCurrentCell(): Cell?
    fun processColorSensorData(args: List<Any>)
    fun getRobotPosition(): Pair<Int, Int>
    fun setRobotPosition(x: Int, y: Int)
    fun moveRoboterToDirection(direction: RoboterDirection)
    fun moveRoboterSouth()
    fun moveRoboterNorth()
    fun moveRoboterWest()
    fun moveRoboterEast()
    fun setCurrentCellBorder(roboterDirection: RoboterDirection, cellBoarder: CellBoarder)
    fun addListener(listener: LabyrinthStateListener)
    fun removeListener(listener: LabyrinthStateListener)
    fun addCell(x: Int, y: Int, cell: Cell)
    fun getCells(): Map<Pair<Int, Int>, Cell>
    fun getNeighbors(x: Int, y: Int): List<Pair<Int, Int>>

}