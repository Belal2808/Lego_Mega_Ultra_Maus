package de.fhkiel.rob.legoosctester

import org.koin.core.context.startKoin
import org.koin.dsl.module
import de.fhkiel.rob.legoosctester.gui.Base
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import org.koin.mp.KoinPlatform.getKoin

val appModule = module {
    single<LabyrinthStateService> { LabyrinthState(20, 20) }
    single<RobotStateService> { RoboterState() }
    single { RobotController() }
    single { MovementManager(get()) }
    single { MovementPlanner(get(), get(), get())}
    single { LabyrinthExplorer(get(), get()) }
}

fun main() {
    startKoin { modules(appModule) }
    Base()
    val labyrinthExplorer: LabyrinthExplorer = getKoin().get()
    labyrinthExplorer.explore()
    OSCReceiver.start()
}