package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin
import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import java.awt.Color
import java.lang.Thread.sleep

enum class RoboterControls{
    DISTANCE,AGAINSTWALL,NONE
}

class RobotController {
    private val roboterState : RobotStateService = getKoin().get()
    private val movementListeners = mutableListOf<(RoboterControls) -> Unit>()
    private val labyrinthState: LabyrinthStateService = getKoin().get()


    init {
        OSCReceiver.addListener { path, args ->
            onMessageReceived(path, args)
        }
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/c/angle", 0)
    }

    fun driveToNextCell(): List<() -> Unit>{
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/multirun/target", 200, 600, 600) }
        )
    }

    fun headbuttWall():List<() -> Unit>{
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/multirun/target", 200, 150, 150) }
        )

    }

    fun turnLeft90Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/multirun/target", 200, 185, -185) },
        )
    }

    fun turnRight90Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/multirun/target", 200, -185, 185) }
        )
    }

    fun turn180Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/ab/multirun/target", 200, -370, 370) }
        )
    }

    fun turnEyes(eyesDirection: EyesDirection): List<() -> Unit> {
        return when (eyesDirection) {
            EyesDirection.FRONT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/c/run/target", 300, 0) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/ultrasonic/s1/distance")}
            )
            EyesDirection.RIGHT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/c/run/target", 300, 95) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/ultrasonic/s1/distance")}
            )
            EyesDirection.BACK -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/c/run/target", 300, 185) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/ultrasonic/s1/distance")}
            )
            EyesDirection.LEFT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/motor/c/run/target", 300, 275) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotIp}/ultrasonic/s1/distance")}
            )
        }
    }


    fun look(): List<() -> Unit> {
        return listOf {
            OSCSender(
                roboterState.robotIp,
                roboterState.robotPort
            ).send("/${roboterState.robotIp}/ultrasonic/s1/distance")
        }
    }


    fun smellColor(): List<() -> Unit> {
        return listOf {
            OSCSender(
                roboterState.robotIp,
                roboterState.robotPort
            ).send("/${roboterState.robotIp}/color/s2")
        }
    }





    fun addMovementCompleteListener(listener: (RoboterControls) -> Unit) {
        movementListeners.add(listener)
    }

    private fun notifyMovementComplete(control: RoboterControls) {
        movementListeners.forEach { it(control) }
    }

    private fun onMessageReceived(path: String, args: List<Any>) {

     if (path == "/OSCBrick@${roboterState.robotIp}/motor/a/target/reached" || path == "/OSCBrick@${roboterState.robotIp}/motor/a/angle/is") {
                roboterState.motorATargetReached = true
                if(roboterState.motorATargetReached && roboterState.motorBTargetReached) {
                    roboterState.motorATargetReached = false
                    roboterState.motorBTargetReached = false
                    notifyMovementComplete(RoboterControls.NONE)
                }
        }else if (path == "/OSCBrick@${roboterState.robotIp}/motor/b/target/reached" || path == "/OSCBrick@${roboterState.robotIp}/motor/b/angle/is") {
                roboterState.motorBTargetReached = true
                if (roboterState.motorATargetReached && roboterState.motorBTargetReached) {
                    roboterState.motorATargetReached = false
                    roboterState.motorBTargetReached = false
                    notifyMovementComplete(RoboterControls.NONE)
                }
        }else if(path == "/OSCBrick@${roboterState.robotIp}/motor/c/angle/is" && args[0] == 0){
            // do nothing
        }else if(path == "/OSCBrick@${roboterState.robotIp}/ultrasonic/s1/distance/is"){
            val currentsPosition = labyrinthState.getRobotPosition()
            val currentCell = labyrinthState.getCell(currentsPosition.first,currentsPosition.second)
            val list = currentCell?.getUndiscoveredBorders()
            if(list == null){
                notifyMovementComplete(RoboterControls.NONE)
                return
            }
            if(list.isEmpty()){
                notifyMovementComplete(RoboterControls.NONE)
                return
            }
            if(args[0] as Int >250){
                println(list.first())
                labyrinthState.setCurrentCellBorder(list.first(),CellBoarder.UNDISCOVERED)
            }else{
                println(list.first())
                labyrinthState.setCurrentCellBorder(list.first(),CellBoarder.WALL)
            }
         notifyMovementComplete(RoboterControls.NONE)
        } else if (path == "/OSCBrick@${roboterState.robotIp}/color/s2/is") {
         labyrinthState.processColorSensorData(args)
         notifyMovementComplete(RoboterControls.NONE)
        }else{
         notifyMovementComplete(RoboterControls.NONE)
     }
    }
}