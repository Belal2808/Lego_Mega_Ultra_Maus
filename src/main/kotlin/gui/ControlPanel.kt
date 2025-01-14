package de.fhkiel.rob.legoosctester.gui

import javax.swing.*
import java.awt.*
import java.io.IOException
import de.fhkiel.rob.legoosctester.*
import org.koin.mp.KoinPlatform

class ControlPanel : JPanel() {

    // Variablen für die Abhängigkeiten
    lateinit var labyrinthExplorer: LabyrinthExplorer
    lateinit var automaticExplorer: AutomaticExplorer
    lateinit var labyrinthState: LabyrinthStateService
    lateinit var algorithmus: Algorithmus

    init {
        layout = GridLayout(5, 2, 10, 10) // 5 Reihen, 2 Spalten, Abstand 10px
        background = Color(240, 240, 240)
        border = BorderFactory.createTitledBorder("Steuerung")

        // Buttons erstellen und Aktionen zuweisen
        addButton("↑ Nach Norden") { labyrinthExplorer.driveNorth() }
        addButton("↓ Nach Süden") { labyrinthExplorer.driveSouth() }
        addButton("← Nach Westen") { labyrinthExplorer.driveWest() }
        addButton("→ Nach Osten") { labyrinthExplorer.driveEast() }
        addButton("🔍 Zelle scannen") { labyrinthExplorer.scanCell() }
        addButton("🚶 Automatisch erkunden") { automaticExplorer.exploreAutomatic() }
        addButton("💾 Karte exportieren") {
            try {
                MapLoader.exportMap(labyrinthState, "labyrinth.json")
                println("Karte exportiert in labyrinth.json")
            } catch (ex: IOException) {
                println("Fehler beim Export: ${ex.message}")
            }
        }
        addButton("📂 Karte laden") {
            MapLoader.loadMap(labyrinthState, "labyrinth.json")
            println("Karte aus labyrinth.json geladen")
        }
        addButton("🎯 Nächstes Ziel finden") {
            algorithmus.calculateAndMoveToNextTarget(labyrinthState)
        }
        addButton("🔄 Zurücksetzen") {
            labyrinthExplorer.resetRoboter()
            println("Roboter und Labyrinth zurückgesetzt.")
        }
    }

    private fun addButton(label: String, action: () -> Unit) {
        val button = JButton(label).apply {
            background = Color(200, 230, 255)
            foreground = Color.BLACK
            isFocusable = false
            addActionListener { action() }
        }
        add(button)
    }
}
