package de.fhkiel.rob.legoosctester

interface LabyrinthStateService {
    fun updateCell(x: Int, y: Int,cell: Cell)
    fun getCell(x: Int, y: Int): Cell?
    fun getCurrentCell(): Cell?
    fun getRobotPosition(): Pair<Int, Int>
    fun setRobotPosition(x: Int, y: Int)
    fun getXRoboterPosition(): Int
    fun getYRoboterPosition(): Int
    fun addCell(x: Int, y: Int, cell: Cell)
    fun getCells(): Map<Pair<Int, Int>, Cell>
    fun findPathDijkstra(start: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>>
}