package de.fhkiel.rob.legoosctester

import org.koin.core.context.startKoin
import org.koin.dsl.module
import de.fhkiel.rob.legoosctester.gui.MainGUI
import de.fhkiel.rob.legoosctester.gui.MapCanvas
import de.fhkiel.rob.legoosctester.osc.OSCReceiver

val appModule = module {
    single<LabyrinthStateService> { LabyrinthState(20, 20) }
    single<RobotStateService> { RoboterState() }
    single { RobotController() }
    single { MovementManager(get()) }
    single { MovementPlanner(get(), get(), get())}
    single { LabyrinthExplorer(get(), get()) }
    single { MapCanvas() }
    single { Algorithmus(get(), get(),get()) }
    single { AutomaticExplorer(get(), get(),get()) }
}

fun main() {
    startKoin { modules(appModule) }
    MainGUI()
    OSCReceiver.start()
}