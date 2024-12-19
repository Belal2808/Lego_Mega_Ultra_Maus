package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin
import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver

enum class RoboterControls{
    LEFTTURN,RIGHTTURN,FOWARD,TURNEYESRIGHT,TURNEYESLEFT,TURN180DEGREES,NONE
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

    private fun onMessageReceived(path: String, args: List<Any>) {
        if (path == "/OSCBrick@192.168.178.152/gyroscope/s3/angle/is" && args[0] != 0) {
            println("Gyroscope angle data: $args")
            // Logik für Gyroscope-Daten hier einfügen
        }
    }
}