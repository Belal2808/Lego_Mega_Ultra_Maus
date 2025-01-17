package de.fhkiel.rob.legoosctester

interface RobotStateService {
    var robotIp: String
    var robotName: String
    var robotPort: Int
    var motorATargetReached : Boolean
    var motorBTargetReached : Boolean
    var scannedDistance: Int
    var realRoboterXCord: Int
    var realRoboterYCord: Int

    fun addListener(listener: RoboterStateListener)
    fun removeListener(listener: RoboterStateListener)

    fun getAngle(): Pair<Int, Int>
    fun getRobotPosition(): Pair<Int, Int>
    fun setAngle(x: Int, y: Int)
    fun getRoboterDirection(): RoboterDirection
    fun setRoboterDirection(direction: RoboterDirection)
    fun setEyesDirection(direction: EyesDirection)
    fun getEyesDirection(): EyesDirection
    fun getBackwardsDirection(): RoboterDirection
    fun setRoboterPositionWithDirection(direction: RoboterDirection)
}