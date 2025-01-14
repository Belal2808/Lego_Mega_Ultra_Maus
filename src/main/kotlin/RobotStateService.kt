package de.fhkiel.rob.legoosctester

interface RobotStateService {
    var robotIp: String
    var robotName: String
    var robotPort: Int
    var motorATargetReached : Boolean
    var motorBTargetReached : Boolean
    var scannedDistance: Int

    fun getAngle(): Pair<Int, Int>
    fun setAngle(x: Int, y: Int)
    fun getRoboterDirection(): RoboterDirection
    fun setRoboterDirection(direction: RoboterDirection)
    fun setEyesDirection(direction: EyesDirection)
    fun getEyesDirection(): EyesDirection
    fun getBackwardsDirection(): RoboterDirection
}