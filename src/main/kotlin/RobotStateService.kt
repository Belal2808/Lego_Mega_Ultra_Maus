package de.fhkiel.rob.legoosctester

interface RobotStateService {
    var robotIp: String
    var robotPort: Int

    fun getAngle(): Pair<Int, Int>
    fun setAngle(x: Int, y: Int)
    fun getRoboterDirection(): RoboterDirection
    fun setRoboterDirection(direction: RoboterDirection)
    fun setEyesDirection(direction: EyesDirection)
    fun getEyesDirection(): EyesDirection
}