package de.fhkiel.rob.legoosctester

class MovementPlanner(
    private val movementManager: MovementManager,
    private val robotState: RobotStateService,
    private val robotController: RobotController
) {
    fun planAndExecuteRoboterMovement(targetDirection: RoboterDirection) {
        val currentDirection = robotState.getRoboterDirection()
        val movements = mutableListOf<() -> Unit>()
        movements.addAll(calculateMovementToTargetDirection(currentDirection, targetDirection))
        movements.addAll((robotController.driveToNextCell()))
        robotState.setRoboterDirection(targetDirection)
        movementManager.enqueueMovements(movements)
    }

    fun planAndExecuteEyeMovement(targetDirection: RoboterDirection){
        val currentRoboterDirection = robotState.getRoboterDirection()
        val movements = calculateEyeMovementToTargetDirection(currentRoboterDirection,targetDirection)
        movementManager.enqueueMovements(movements)

    }

    fun resetEyes(){
        val movements = mutableListOf<() -> Unit>()
        movements.addAll(robotController.turnEyes(EyesDirection.FRONT))
        movementManager.enqueueMovements(movements)
    }

    fun headButtWall(direction: RoboterDirection){
        val movements = mutableListOf<() -> Unit>()
        movements.addAll(calculateMovementToTargetDirection(robotState.getRoboterDirection(),direction))
        movements.add {robotController.headbuttWall()}
        movements.addAll(robotController.driveBack())
        robotState.setRoboterDirection(direction)
        movementManager.enqueueMovements(movements)
    }

    private fun calculateEyeMovementToTargetDirection( currentDirection: RoboterDirection, targetDirection: RoboterDirection): List<() -> Unit> {
        val movements = mutableListOf<() -> Unit>()

        val difference =  (targetDirection.value - currentDirection.value + 4) % 4

        val direction = EyesDirection.fromValue(difference) ?: return movements

        movements.addAll(robotController.turnEyes(direction))

        return movements
    }
    fun planAndExecuteColorScan() {
        val movements = robotController.smellColor()
        movementManager.enqueueMovements(movements)
    }
    private fun calculateMovementToTargetDirection(currentDirection: RoboterDirection, targetDirection: RoboterDirection): List<() -> Unit> {
        val movements = mutableListOf<() -> Unit>()

        val difference = (targetDirection.value - currentDirection.value + 4) % 4

        when (difference) {
            1 -> {
                movements.addAll(robotController.turnRight90Degree())
            }
            2 -> {
                movements.addAll(robotController.turn180Degree())
            }
            3 -> {
                movements.addAll(robotController.turnLeft90Degree())
            }
        }

        return movements
    }

    fun resetRoboter() {
        val currentDirection = robotState.getRoboterDirection()
        val movements = calculateMovementToTargetDirection(currentDirection, RoboterDirection.NORTH)
        robotState.setRoboterDirection(RoboterDirection.NORTH)
        movementManager.enqueueMovements(movements)
    }
}
