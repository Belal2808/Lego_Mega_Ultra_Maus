package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin

class RoboterState() : RobotStateService {
    private val currentDirection: Direction = Direction.NORTH
    override var robotPort: Int = 9001
    override var robotIp: String = "192.168.178.152"

    override fun setAngle(x: Int, y: Int) {
        TODO("Not yet implemented")
    }
    override fun getAngle(): Pair<Int, Int> {
        TODO("Not yet implemented")
    }
    override fun getDirection(): Direction {
        TODO("Not yet implemented")
    }
    override fun setDirection(direction: Direction) {
        TODO("Not yet implemented")
    }
    override fun setEyesAngle(direction: Direction) {
        TODO("Not yet implemented")
    }
    override fun getEyesAngle(): Direction {
        TODO("Not yet implemented")
    }
}