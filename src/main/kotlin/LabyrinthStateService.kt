package de.fhkiel.rob.legoosctester

interface LabyrinthStateService {
    fun updateCell(x: Int, y: Int)
    fun getCell(x: Int, y: Int): Cell
    fun getRobotPosition(): Pair<Int, Int>
    fun setRobotPosition(x: Int, y: Int)
}