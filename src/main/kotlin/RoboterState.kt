package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin

class RoboterState() : RobotStateService {
    private var roboterDirection: RoboterDirection = RoboterDirection.NORTH
    private var eyesDirection : EyesDirection = EyesDirection.LEFT
    override var robotPort: Int = 9001
    override var robotIp: String = "192.168.178.152"
    var eyesTurnCheck : Int = 0

    override fun setAngle(x: Int, y: Int) {
        TODO("Not yet implemented")
    }
    override fun getAngle(): Pair<Int, Int> {
        TODO("Not yet implemented")
    }
    override fun getRoboterDirection(): RoboterDirection {
        return roboterDirection
    }
    override fun setRoboterDirection(direction: RoboterDirection) {
        roboterDirection = direction
    }
    override fun setEyesDirection(direction: EyesDirection) {
        eyesDirection = direction
    }
    override fun getEyesDirection(): EyesDirection {
        return eyesDirection
    }
}