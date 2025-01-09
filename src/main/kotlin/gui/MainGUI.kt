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

        add(mapCanvas)

        addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    // WASD => Bewegung
                    KeyEvent.VK_W -> {
                        labyrinthExplorer.driveNorth()
                    }
                    KeyEvent.VK_S -> {
                        labyrinthExplorer.driveSouth()
                    }
                    KeyEvent.VK_A -> {
                        labyrinthExplorer.driveWest()
                    }
                    KeyEvent.VK_D -> {
                        labyrinthExplorer.driveEast()
                    }
                    KeyEvent.VK_SPACE -> {
                        labyrinthExplorer.scanCell()
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
                        algorithmus.calculateAndMoveToNextTarget(labyrinthState)
                    }
                }
                println("Position: ($x, $y), Farbe: $currentColor, Richtung: $currentDirection")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }
}

