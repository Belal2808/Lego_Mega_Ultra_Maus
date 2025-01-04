package de.fhkiel.rob.legoosctester

class MovementPlanner(
    private val movementManager: MovementManager,
    private val robotState: RobotStateService,
    private val robotController: RobotController
) {
    fun planAndExecuteRoboterMovement(targetDirection: RoboterDirection) {
        val currentDirection = robotState.getRoboterDirection()
        val movements = determineRoboterMovementsForDirection(currentDirection, targetDirection)
        // Bewegungen an den MovementManager übergeben
        movements.forEach { movement ->
            movementManager.enqueueMovement(movement)
        }
    }

    fun planAndExecuteEyeMovement(targetDirection: RoboterDirection){

    }

    private fun determineRoboterMovementsForDirection(
        currentDirection: RoboterDirection,
        targetDirection: RoboterDirection
    ): List<() -> Unit> {
        val movements = mutableListOf<() -> Unit>()

        when (targetDirection) {
            RoboterDirection.NORTH -> {
                when (currentDirection) {
                    RoboterDirection.EAST -> {
                        movements.add {robotController.turnEyes(EyesDirection.FRONT)  }
                    }
                    RoboterDirection.WEST -> {
                        movements.add {robotController.turnEyes(EyesDirection.FRONT)  }
                    }
                    RoboterDirection.SOUTH -> {
                        movements.add { robotController.turnEyes(EyesDirection.FRONT)  }
                    }
                    RoboterDirection.NORTH -> {
                    }
                }
                movements.add {  robotController.turnEyes(EyesDirection.FRONT)  }
            }
            RoboterDirection.WEST -> movements.add { robotController.turnEyes(EyesDirection.FRONT) }
            RoboterDirection.SOUTH -> movements.add { robotController.turnEyes(EyesDirection.FRONT)  }
            RoboterDirection.EAST -> movements.add { robotController.turnEyes(EyesDirection.FRONT)  }
        }
        return movements
    }


}
