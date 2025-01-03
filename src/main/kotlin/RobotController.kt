package de.fhkiel.rob.legoosctester

import org.koin.mp.KoinPlatform.getKoin
import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import java.lang.Thread.sleep

enum class RoboterControls{
    LEFTTURN,RIGHTTURN,FORWARD,TURN180DEGREES,AGAINSTWALL,NONE
}

class RobotController {
    val roboterState : RobotStateService = getKoin().get()
    var currentControl: RoboterControls = RoboterControls.NONE

    init {
        OSCReceiver.addListener { path, args ->
            onMessageReceived(path, args)
        }
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/c/angle", 0)
    }

    fun driveToNextCell(){
        currentControl = RoboterControls.FORWARD
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, 140)
    }

    fun headbuttWall(){
        currentControl = RoboterControls.AGAINSTWALL
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, 140)

    }

    fun turnLeft90Degree(){
        currentControl = RoboterControls.LEFTTURN
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/gyroscope/s3/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, 140, -140)
    }

    fun turnRight90Degree(){
        currentControl = RoboterControls.RIGHTTURN
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.179/gyroscope/s1/angle", 0)
        sleep(5)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/ab/angle", 0)
        sleep(100)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/ab/multirun/target", 200, -170, 170)
    }

    fun turn180Degree(){
        currentControl = RoboterControls.TURN180DEGREES
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/gyroscope/s3/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/angle", 0)
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/bc/multirun/target", 100, -280, 280)

    }

    fun turnEyes(eyesDirection: EyesDirection){

        when (eyesDirection){
            EyesDirection.LEFT->{
                OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/c/run/target", 300, 0)
                roboterState.setEyesDirection(eyesDirection)
            }
            EyesDirection.FRONT->{
                OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/c/run/target", 300, 95)
                roboterState.setEyesDirection(eyesDirection)
            }
            EyesDirection.RIGHT -> {
                OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/c/run/target", 300, 185)
                roboterState.setEyesDirection(eyesDirection)
            }
            EyesDirection.BACK -> {
                OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/motor/c/run/target", 300, 275)
                roboterState.setEyesDirection(eyesDirection)
            }
        }
    }

    fun look(){
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/ultrasonic/s1/distance")
    }

    fun smellColor(){
        OSCSender(roboterState.robotIp, roboterState.robotPort).send("/192.168.178.152/color/s2")
    }

    private fun onMessageReceived(path: String, args: List<Any>) {
        if (path == "/OSCBrick@192.168.178.152/motor/c/target/reached") {
            when(args[0]){
                0-> {print("augen gucken nach links\n")
                    roboterState.setEyesDirection(EyesDirection.LEFT)
                }
                95 -> {
                    print("augen gucken nach vorne\n")
                    roboterState.setEyesDirection(EyesDirection.FRONT)
                }
                185 -> {
                    print("augen gucken nach rechts\n")
                    roboterState.setEyesDirection(EyesDirection.RIGHT)
                }
                275 -> {
                    print("augen gucken nach hinten\n")
                    roboterState.setEyesDirection(EyesDirection.BACK)
                }
            }
        }
    }
}