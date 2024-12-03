package de.fhkiel.rob.legoosctester

interface RobotStateService {
    fun getAngle(): Pair<Int, Int>
    fun setAngle(x: Int, y: Int)
    fun getDirection(): Direction
    fun setDirection(direction: Direction)
    fun setEyesAngle(direction: Direction)
    fun getEyesAngle(): Direction
}