package de.fhkiel.rob.legoosctester

class LabyrinthExplorer(private val labyrinthStateService: LabyrinthStateService, private val movementPlanner: MovementPlanner){
    var lastCellDirection :RoboterDirection? = null

    fun exploreCell() {
        labyrinthStateService.getCell(labyrinthStateService.getRobotPosition())
    }

    fun driveWest(){
        lastCellDirection = RoboterDirection.EAST
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.WEST)
    }
    fun driveNorth(){
        lastCellDirection = RoboterDirection.SOUTH
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.NORTH)
    }
    fun driveEast(){
        lastCellDirection = RoboterDirection.WEST
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.EAST)
    }
    fun driveSouth(){
        lastCellDirection = RoboterDirection.NORTH
        movementPlanner.planAndExecuteRoboterMovement(RoboterDirection.SOUTH)
    }

    fun scanCell() {
       var currentCell = labyrinthStateService.getCell(labyrinthStateService.getRobotPosition())
        if(currentCell == null){
            currentCell = Cell()
        }
        val direction = lastCellDirection
        if (direction != null) {
            currentCell.setBorder(direction, CellBoarder.DISCOVERED)
        }
        val undiscoveredBoardersList = currentCell.getUndiscoveredBorders()
        if(undiscoveredBoardersList.isNotEmpty()){

        }


    }
}