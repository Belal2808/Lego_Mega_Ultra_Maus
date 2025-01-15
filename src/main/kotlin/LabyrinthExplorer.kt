package de.fhkiel.rob.legoosctester

import java.awt.Color

class LabyrinthExplorer(private val labyrinthStateService: LabyrinthStateService, private val movementPlanner: MovementPlanner){
    var lastCellDirection :RoboterDirection? = null


    fun driveWest(){
        lastCellDirection = RoboterDirection.EAST
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first-1,roboterPosition.second)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.WEST)
    }
    fun driveNorth(){
        lastCellDirection = RoboterDirection.SOUTH
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first,roboterPosition.second-1)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.NORTH)
    }
    fun driveEast(){
        lastCellDirection = RoboterDirection.WEST
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first+1,roboterPosition.second)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.EAST)
    }
    fun driveSouth(){
        lastCellDirection = RoboterDirection.NORTH
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first,roboterPosition.second+1)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.SOUTH)
    }

    fun headButtWall(){
        val walls = labyrinthStateService.getCurrentCell()!!.getWallBorders()
        if(walls.isEmpty()){
            return
        }else{
            print(walls[0])
            movementPlanner.headButtWall(walls[0])
        }
    }

    fun scanCell() {
        var currentCell = labyrinthStateService.getCurrentCell()
        if(currentCell == null){
            currentCell = Cell()
        }
        labyrinthStateService.updateCell(labyrinthStateService.getRobotPosition().first,labyrinthStateService.getRobotPosition().second,currentCell)
        val direction = lastCellDirection
        if (direction != null) {
            currentCell.setBorder(direction, CellBoarder.DISCOVERED)
        }
        val undiscoveredBoardersList = currentCell.getNoneBorders()
        if(undiscoveredBoardersList.isNotEmpty()){
            for(undiscoveredBoarder in undiscoveredBoardersList){
                movementPlanner.planAndExecuteEyeMovement(undiscoveredBoarder)
            }
        }
        if(currentCell.color == Color.DARK_GRAY) {
            movementPlanner.planAndExecuteColorScan()
        }
        movementPlanner.resetEyes()
    }

    fun resetRoboter() {
        movementPlanner.resetEyes()
        movementPlanner.resetRoboter()
    }
}