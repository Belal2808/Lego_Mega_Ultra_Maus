package de.fhkiel.rob.legoosctester

class MovementPlanner(
    private val movementManager: MovementManager,
    private val robotState: RobotStateService,
    private val robotController: RobotController
) {
    fun planAndExecuteRoboterMovement(targetDirection: RoboterDirection) {
        val currentDirection = robotState.getRoboterDirection()
        val movements = calculateMovementToTargetDirection(currentDirection, targetDirection)
        robotState.setRoboterDirection(targetDirection)
        movementManager.enqueueMovements(movements)
    }

    fun planAndExecuteEyeMovement(targetDirection: RoboterDirection){
        val currentRoboterDirection = robotState.getRoboterDirection()
        val movements = calculateEyeMovementToTargetDirection(currentRoboterDirection,targetDirection)
        movementManager.enqueueMovements(movements)

    }

    private fun calculateEyeMovementToTargetDirection( currentDirection: RoboterDirection, targetDirection: RoboterDirection): List<() -> Unit> {
        val movements = mutableListOf<() -> Unit>()

        val difference =  (targetDirection.value - currentDirection.value + 4) % 4

        val direction = EyesDirection.fromValue(difference) ?: return movements

        movements.addAll(robotController.turnEyes(direction))

        return movements
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

        movements.addAll(robotController.driveToNextCell())

        return movements
    }
}
