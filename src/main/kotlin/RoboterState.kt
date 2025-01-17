package de.fhkiel.rob.legoosctester

interface RoboterStateListener {
    fun onStateChanged()
}

class RoboterState() : RobotStateService {
    private val listeners = mutableListOf<RoboterStateListener>()
    private var roboterDirection: RoboterDirection = RoboterDirection.NORTH
    private var eyesDirection : EyesDirection = EyesDirection.FRONT
    override var realRoboterXCord: Int = 10
    override var realRoboterYCord: Int = 10
    override var robotPort: Int = 9001
    override var robotIp: String = "192.168.178.143"
    override var robotName: String = "peter"
    override var motorATargetReached: Boolean = false
    override var motorBTargetReached: Boolean = false
    override var scannedDistance: Int = 0

    override fun addListener(listener: RoboterStateListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: RoboterStateListener) {
        listeners.remove(listener)
    }

    override fun getRobotPosition(): Pair<Int, Int> {
        return Pair(realRoboterXCord, realRoboterYCord)
    }

    private fun notifyListeners() {
        listeners.forEach { it.onStateChanged() }
    }

    override fun setAngle(x: Int, y: Int) {
        TODO("Not yet implemented")
    }
    override fun getAngle(): Pair<Int, Int> {
        TODO("Not yet implemented")
    }
    override fun getRoboterDirection(): RoboterDirection {
        return roboterDirection
    }

    override fun setRoboterPositionWithDirection(direction: RoboterDirection){
        print("ich hab die echt position erneuert")
        when(direction){
            RoboterDirection.EAST -> {
                realRoboterXCord++
                notifyListeners()
            }
            RoboterDirection.NORTH -> {
                realRoboterYCord--
                notifyListeners()
            }
            RoboterDirection.WEST -> {
                realRoboterXCord--
                notifyListeners()
            }
            RoboterDirection.SOUTH -> {
                realRoboterYCord++
                notifyListeners()
            }
        }
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
