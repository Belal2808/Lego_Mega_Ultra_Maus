package de.fhkiel.rob.legoosctester


class RoboterState() : RobotStateService {
    private var roboterDirection: RoboterDirection = RoboterDirection.NORTH
    private var eyesDirection : EyesDirection = EyesDirection.FRONT
    override var robotPort: Int = 9001
    override var robotIp: String = "192.168.178.154"
    override var motorATargetReached: Boolean = false
    override var motorBTargetReached: Boolean = false
    override var scannedDistance: Int = 0

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

    override fun getBackwardsDirection(): RoboterDirection {
        return RoboterDirection.fromValue((roboterDirection.value + 2) %4)
    }
}
