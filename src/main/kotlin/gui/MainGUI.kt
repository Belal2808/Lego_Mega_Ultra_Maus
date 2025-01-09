package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.legoosctester.*
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.IOException
import javax.swing.JFrame
import javax.swing.Timer
import org.json.*
import org.koin.mp.KoinPlatform.getKoin

class MainGUI : JFrame() {
    private var currentColor = Color.LIGHT_GRAY
    private var currentDirection = RoboterDirection.NORTH  // Verwende RoboterDirection


    val mapCanvas = MapCanvas()

    private val labyrinthState: LabyrinthStateService = getKoin().get()
    val algorithmus = Algorithmus(labyrinthState, mapCanvas)


    init {
        val labyrinthExplorer: LabyrinthExplorer = getKoin().get()
        title = "Labyrinth Map"
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE

        val labyrinthState: LabyrinthStateService = getKoin().get()
        val mapController = MapController(labyrinthState, mapCanvas)

        add(mapCanvas)

        addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    // M => R/G/B-Farben wiederherstellen
                    KeyEvent.VK_M -> {
                        resetColorFields(labyrinthState, mapController)
                    }
                    // WASD => Bewegung
                    KeyEvent.VK_W -> {
                        labyrinthExplorer.driveNorth()
                        labyrinthState.moveRoboterNorth()
                    }
                    KeyEvent.VK_S -> {
                        labyrinthExplorer.driveSouth()
                        labyrinthState.moveRoboterSouth()
                    }
                    KeyEvent.VK_A -> {
                        labyrinthExplorer.driveWest()
                        labyrinthState.moveRoboterWest()
                    }
                    KeyEvent.VK_D -> {
                        labyrinthExplorer.driveEast()
                        labyrinthState.moveRoboterEast()
                    }
                    KeyEvent.VK_SPACE -> {
                        labyrinthExplorer.scanCell()
                        val position = labyrinthState.getRobotPosition()
                        val currentCell = labyrinthState.getCell(position.first, position.second)

                        if (currentCell != null) {

                            val updatedBorders = currentCell.borders.toMutableMap()


                            mapController.addSimulatedCell(
                                position.first,
                                position.second,
                                color = currentCell.color,
                                bordersMap = updatedBorders,
                                isEntrance = currentCell.isEntrance,
                                isColorField = currentCell.isColorField,
                                priority = currentCell.priority,
                                isBlocked = currentCell.isBlocked
                            )

                            println("Zelle gescannt und aktualisiert bei (${position.first}, ${position.second}).")
                        } else {
                            println("Fehler: Keine Zelle an der Position (${position.first}, ${position.second}) gefunden.")
                        }
                    }
                    // P => Export
                    KeyEvent.VK_P -> {
                        try {
                            MapLoader.exportMap(labyrinthState, "labyrinth.json")
                            println("Karte exportiert in labyrinth.json")
                        } catch (ex: IOException) {
                            println("Fehler beim Export: ${ex.message}")
                        }
                    }
                    // L => Load
                    KeyEvent.VK_L -> {
                        MapLoader.loadMap(labyrinthState, "labyrinth.json")
                        println("Karte aus labyrinth.json geladen")
                    }
                    // F => Pfad suchen
                    KeyEvent.VK_F -> {
                        algorithmus.calculateAndMoveToNextTarget(labyrinthState, mapController)
                    }
                }
                println("Position: ($x, $y), Farbe: $currentColor, Richtung: $currentDirection")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }


    private fun resetColorFields(labyrinthState: LabyrinthStateService, mapController: MapController) {
        for ((coords, cell) in labyrinthState.getCells()) {
            val c = cell.color
            val isRGB = (c == Color.RED || c == Color.GREEN || c == Color.BLUE)
            if (isRGB && !cell.isColorField) {
                val newBorders = cell.borders.toMutableMap()

                mapController.addSimulatedCell(
                    coords.first, coords.second,
                    color = cell.color,
                    bordersMap = newBorders,
                    isEntrance  = cell.isEntrance,
                    isColorField= true,
                    priority    = cell.priority,
                    isBlocked   = cell.isBlocked
                )
                println("Zelle $coords mit Farbe $c wieder als isColorField=true aktiviert.")
            }
        }
        println("Alle R/G/B-Zellen reaktiviert, du kannst F erneut verwenden.")
    }
}

