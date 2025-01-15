package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin
import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver

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
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/c/angle", 0)
    }

    fun driveToNextCell(): List<() -> Unit>{
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/ab/multirun/target", 200, 625, 625) }
        )
    }

    fun headbuttWall(){
        val oscSender = OSCSender(roboterState.robotIp, roboterState.robotPort)

        oscSender.send("/${roboterState.robotName}/motor/a/run", 100)
        oscSender.send("/${roboterState.robotName}/motor/b/run", 100)
        oscSender.send("/${roboterState.robotName}/touch/s4")
    }

    fun turnLeft90Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/ab/multirun/target", 200, 185, -185) },
        )
    }

    fun turnRight90Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/ab/multirun/target", 200, -185, 185) }
        )
    }

    fun turn180Degree(): List<() -> Unit> {
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/ab/multirun/target", 200, -370, 370) }
        )
    }

    fun turnEyes(eyesDirection: EyesDirection): List<() -> Unit> {
        return when (eyesDirection) {
            EyesDirection.FRONT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/c/run/target", 300, 0) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/ultrasonic/s1/distance")}
            )
            EyesDirection.RIGHT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/c/run/target", 300, -275) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/ultrasonic/s1/distance")}
            )
            EyesDirection.BACK -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/c/run/target", 300, -185) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/ultrasonic/s1/distance")}
            )
            EyesDirection.LEFT -> listOf(
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/c/run/target", 300, -95) },
                { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/ultrasonic/s1/distance")}
            )
        }
    }


    fun smellColor(): List<() -> Unit> {
        return listOf {
            OSCSender(
                roboterState.robotIp,
                roboterState.robotPort
            ).send("/${roboterState.robotName}/color/s3")
        }
    }

    fun driveBack(): List<() -> Unit>{
        return listOf(
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/angle", 0) },
            { OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/ab/multirun/target", 200, -150, -150) }
        )
    }

    fun addMovementCompleteListener(listener: (RoboterControls) -> Unit) {
        movementListeners.add(listener)
    }

    private fun notifyMovementComplete(control: RoboterControls) {
        movementListeners.forEach { it(control) }
    }

    private fun onMessageReceived(path: String, args: List<Any>) {
        println("$path with args $args")

     if (path == "/${roboterState.robotName}/motor/a/target/reached") {
                roboterState.motorATargetReached = true
                if(roboterState.motorATargetReached && roboterState.motorBTargetReached) {
                    roboterState.motorATargetReached = false
                    roboterState.motorBTargetReached = false
                    notifyMovementComplete(RoboterControls.NONE)
                }
        }else if (path == "/${roboterState.robotName}/motor/b/target/reached") {
                roboterState.motorBTargetReached = true
                if (roboterState.motorATargetReached && roboterState.motorBTargetReached) {
                    roboterState.motorATargetReached = false
                    roboterState.motorBTargetReached = false
                    notifyMovementComplete(RoboterControls.NONE)
                }
        }else if(path == "/${roboterState.robotName}/motor/c/angle/is" && args[0] == 0){
            // do nothing
        }else if(path == "/${roboterState.robotName}/ultrasonic/s1/distance/is"){
            val currentsPosition = labyrinthState.getRobotPosition()
            val currentCell = labyrinthState.getCell(currentsPosition.first,currentsPosition.second)
            val list = currentCell?.getNoneBorders()
            if(list == null){
                notifyMovementComplete(RoboterControls.NONE)
                return
            }
            if(list.isEmpty()){
                notifyMovementComplete(RoboterControls.NONE)
                return
            }
            if(args[0] as Int >250){
                labyrinthState.setCurrentCellBorder(list.first(),CellBoarder.UNDISCOVERED)
            }else{
                labyrinthState.setCurrentCellBorder(list.first(),CellBoarder.WALL)
            }
         notifyMovementComplete(RoboterControls.NONE)
        } else if (path == "/${roboterState.robotName}/color/s3/is") {
         labyrinthState.processColorSensorData(args)
         notifyMovementComplete(RoboterControls.NONE)
        }else if(path == "/${roboterState.robotName}/touch/s4/pressed"){
           if(args[0]==true){
               OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/a/stop")
               OSCSender(roboterState.robotIp, roboterState.robotPort).send("/${roboterState.robotName}/motor/b/stop")
               notifyMovementComplete(RoboterControls.NONE)
           }else {
               Thread.sleep(1000)
               OSCSender(
                   roboterState.robotIp,
                   roboterState.robotPort
               ).send("/${roboterState.robotName}/touch/s4")
           }
        }else{
         notifyMovementComplete(RoboterControls.NONE)
     }
    }
}