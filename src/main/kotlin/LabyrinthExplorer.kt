package de.fhkiel.rob.legoosctester

class LabyrinthExplorer(private val labyrinthStateService: LabyrinthStateService, private val movementPlanner: MovementPlanner){
    fun explore() {
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.WEST)
        movementPlanner.planAndExecuteEyeMovement(RoboterDirection.NORTH)
    }
}