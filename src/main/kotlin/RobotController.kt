package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin
import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver

enum class RoboterControls{
    LEFTTURN,RIGHTTURN,FORWARD,TURNEYESRIGHT,TURNEYESLEFT,TURN180DEGREES,AGAINSTWALL,NONE
}

class RobotController {
    val robotState : RobotStateService = getKoin().get()
    var currentControl: RoboterControls = RoboterControls.NONE

    init {
        // Registriere Listener für OSC-Nachrichten
        OSCReceiver.addListener { path, args ->
            onMessageReceived(path, args)
        }
    }

    fun driveIntoNextCell(){
        currentControl = RoboterControls.FORWARD
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, 140)
    }

    fun headbuttWall(){
        currentControl = RoboterControls.AGAINSTWALL
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, 140)

    }

    fun turnLeft90Degree(){
        currentControl = RoboterControls.LEFTTURN
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/gyroscope/s3/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, -140)
    }

    fun turnRight90Degree(){
        currentControl = RoboterControls.RIGHTTURN
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/gyroscope/s3/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, -140, 140)

    }

    fun turn180Degree(){
        currentControl = RoboterControls.TURN180DEGREES
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/gyroscope/s3/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(robotState.robotIp, robotState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, -280, 280)

    }


    private fun onMessageReceived(path: String, args: List<Any>) {
        if (path == "/OSCBrick@192.168.178.152/gyroscope/s3/angle/is" && args[0] != 0) {
            println("Gyroscope angle data: $args")
            // Logik für Gyroscope-Daten hier einfügen
        }
    }
}