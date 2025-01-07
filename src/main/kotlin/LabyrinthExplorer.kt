package de.fhkiel.rob.legoosctester

class LabyrinthExplorer(private val labyrinthStateService: LabyrinthStateService, private val movementPlanner: MovementPlanner){
    private var lastCellDirection :RoboterDirection? = null


    fun driveWest(){
        lastCellDirection = RoboterDirection.EAST
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first-1,roboterPosition.second)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.WEST)
    }
    fun driveNorth(){
        lastCellDirection = RoboterDirection.SOUTH
        val roboterPosition = labyrinthStateService.getRobotPosition()
        labyrinthStateService.setRobotPosition(roboterPosition.first,roboterPosition.second+1)
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
        labyrinthStateService.setRobotPosition(roboterPosition.first,roboterPosition.second-1)
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.SOUTH)
    }

    fun scanCell() {
       var currentCell = labyrinthStateService.getCell(labyrinthStateService.getRobotPosition())
        if(currentCell == null){
            currentCell = Cell()
        }
        labyrinthStateService.updateCell(labyrinthStateService.getRobotPosition().first,labyrinthStateService.getRobotPosition().second,currentCell)
        val direction = lastCellDirection
        if (direction != null) {
            currentCell.setBorder(direction, CellBoarder.DISCOVERED)
        }
        val undiscoveredBoardersList = currentCell.getUndiscoveredBorders()
        if(undiscoveredBoardersList.isNotEmpty()){
            for(undiscoveredBoarder in undiscoveredBoardersList){
                movementPlanner.planAndExecuteEyeMovement(undiscoveredBoarder)
            }
        }
        movementPlanner.resetEyes()
    }
    fun printCellBoarders(){
        val currentCell = labyrinthStateService.getCell(labyrinthStateService.getRobotPosition())
        print(currentCell!!.borders.toString())
    }
}