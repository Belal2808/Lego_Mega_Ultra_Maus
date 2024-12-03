package de.fhkiel.rob.legoosctester

import org.koin.core.context.startKoin
import org.koin.dsl.module
import de.fhkiel.rob.legoosctester.gui.Base
import de.fhkiel.rob.legoosctester.osc.OSCReceiver

val appModule = module {
    single<LabyrinthStateService> { LabyrinthState(20, 20) }
    single<RobotStateService> { RoboterState() }
}

fun main() {
    startKoin { modules(appModule) }
    Base()

    OSCReceiver.start()
}