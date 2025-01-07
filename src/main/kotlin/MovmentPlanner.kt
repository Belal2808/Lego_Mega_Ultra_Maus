package de.fhkiel.rob.legoosctester

class MovementPlanner(
    private val movementManager: MovementManager,
    private val robotState: RobotStateService,
    private val robotController: RobotController
) {
    fun planAndExecuteRoboterMovement(targetDirection: RoboterDirection) {
        val currentDirection = robotState.getRoboterDirection()
        val movements = calculateMovementToTargetDirection(currentDirection, targetDirection)
        movements.forEach { movement ->
            movementManager.enqueueMovement(movement)
        }
    }

    fun planAndExecuteEyeMovement(targetDirection: EyesDirection){

    }

    private fun calculateMovementToTargetDirection(currentDirection: RoboterDirection, targetDirection: RoboterDirection): List<() -> Unit> {
        val movements = mutableListOf<() -> Unit>()

        val difference = (targetDirection.value - currentDirection.value + 4) % 4

        when (difference) {
            1 -> {
                movements.add  { robotController.turnRight90Degree() }
            }
            2 -> {
                movements.add { robotController.turn180Degree() }
            }
            3 -> {
                movements.add { robotController.turnLeft90Degree() }
            }
        }

        movements.add {  robotController.driveToNextCell() }

        return movements
    }
}
