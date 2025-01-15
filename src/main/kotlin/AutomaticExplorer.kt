package de.fhkiel.rob.legoosctester

import java.util.Stack

interface MovementQueueListener {
    fun onQueueEmpty()
}

class AutomaticExplorer(private val labyrinthStateService: LabyrinthStateService, private val movementManager: MovementManager,private val labyrinthExplorer: LabyrinthExplorer): MovementQueueListener{
    private var fullyExplored: Boolean = false
    private var drivenPath: Stack<Pair<Int, Int>> = Stack()

    fun exploreAutomatic(){
        movementManager.setMovementQueueListener(this)

        // Erste Bewegungen starten oder Logik für die Exploration hinzufügen
        if (!fullyExplored) {
            driveToNextCell()
        } else {
            println("Labyrinth ist bereits vollständig erkundet.")
        }
    }

    override fun onQueueEmpty() {
        println("Die Bewegungsschlange ist leer! Exploration wird fortgesetzt.")
        if (!fullyExplored) {
            getCellInformation()
        } else {
            movementManager.removeMovementQueueListener(this)
            println("Labyrinth vollständig erkundet!")
        }
    }

    private fun getCellInformation(){
        val currentCell = labyrinthStateService.getCurrentCell()
        if(currentCell != null){
            if(currentCell.getNoneBorders().isEmpty()){
                driveToNextCell()
            }
        }
        labyrinthExplorer.scanCell()
    }

    private fun driveToNextCell(){
        val currentCell = labyrinthStateService.getCurrentCell()
        if(currentCell == null){
            getCellInformation()
            return
        }
        if(currentCell.getNoneBorders().isNotEmpty()){
            getCellInformation()
            return
        }
        val undiscoveredBoarders = currentCell.getUndiscoveredBorders()
        if(undiscoveredBoarders.isEmpty()){
            driveToLastCell()
            return
        }
        labyrinthExplorer.headButtWall()

        when(undiscoveredBoarders[0]){
            RoboterDirection.NORTH -> {
                drivenPath.push(labyrinthStateService.getRobotPosition())
                labyrinthExplorer.driveNorth()
            }
            RoboterDirection.WEST -> {
                drivenPath.push(labyrinthStateService.getRobotPosition())
                labyrinthExplorer.driveWest()
            }
            RoboterDirection.EAST -> {
                drivenPath.push(labyrinthStateService.getRobotPosition())
                labyrinthExplorer.driveEast()
            }
            RoboterDirection.SOUTH -> {
                drivenPath.push(labyrinthStateService.getRobotPosition())
                labyrinthExplorer.driveSouth()
            }
        }
    }

    private fun driveToLastCell() {
        labyrinthExplorer.headButtWall()
        if(drivenPath.isEmpty()){
            fullyExplored = true
            labyrinthExplorer.resetRoboter()
            return
        }
        val lastCellPosition = drivenPath.pop()
        val currentPosition = labyrinthStateService.getRobotPosition()
        val xDifference = lastCellPosition.first-currentPosition.first
        val yDifference = lastCellPosition.second-currentPosition.second
        if(xDifference == 1 ){
            labyrinthExplorer.driveEast()
            return
        }else if(xDifference == -1){
            labyrinthExplorer.driveWest()
            return
        }else if(yDifference == 1){
            labyrinthExplorer.driveSouth()
            return
        }else if(yDifference == -1){
            labyrinthExplorer.driveNorth()
            return
        }else{
           throw Exception("es geht heir nicht zurück und es ist alles kaputt")
        }
    }


}


