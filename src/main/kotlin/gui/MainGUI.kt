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

    private var movementTimer: Timer? = null
    private var currentPath: MutableList<Pair<Int, Int>> = mutableListOf()

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
                        calculateAndMoveToNextTarget(labyrinthState, mapController)
                    }
                }
                println("Position: ($x, $y), Farbe: $currentColor, Richtung: $currentDirection")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }
    // --------------------------------------------------------
    // Pfadberechnung + Automatische Bewegung
    // --------------------------------------------------------
    private fun calculateAndMoveToNextTarget(labyrinthState: LabyrinthStateService, mapController: MapController) {
        val currentPosition = labyrinthState.getRobotPosition()
        println("Aktueller Startpunkt: $currentPosition")

        val targets = labyrinthState.getCells().filterValues { it.isColorField }.keys
        println("Aktuelle Farbziele: $targets")

        if (targets.isEmpty()) {
            println("Keine Farbzellen verfügbar.")
            removeCyanPath(labyrinthState, mapController)
            return
        }

        val nextTarget = targets.minByOrNull { target ->
            val path = labyrinthState.findPathDijkstra(currentPosition, target)
            if (path.isNotEmpty()) path.size else Int.MAX_VALUE
        }
        if (nextTarget == null) {
            println("Kein gültiger Pfad zu einer Farbzelle gefunden.")
            removeCyanPath(labyrinthState, mapController)
            return
        }

        if (nextTarget == currentPosition) {
            println("Nächstes Ziel == aktueller Standort: $currentPosition. Deaktiviere isColorField.")
            val cellHere = labyrinthState.getCell(currentPosition.first, currentPosition.second)
            if (cellHere != null && cellHere.isColorField) {
                // isColorField deaktivieren
                val updated = cellHere.copy(isColorField = false)

                // Borders-Map unverändert übernehmen
                val newBorders = updated.borders.toMutableMap()

                mapController.addSimulatedCell(
                    x = currentPosition.first,
                    y = currentPosition.second,
                    color      = updated.color,
                    bordersMap = newBorders,
                    isEntrance = updated.isEntrance,
                    isColorField = false,
                    priority   = updated.priority,
                    isBlocked  = updated.isBlocked
                )
            }
            return
        }

        val path = labyrinthState.findPathDijkstra(currentPosition, nextTarget)
        if (path.isEmpty()) {
            println("Kein gültiger Pfad zum Ziel gefunden.")
            removeCyanPath(labyrinthState, mapController)
            return
        }

        println("Berechneter Pfad: $path")
        removeCyanPath(labyrinthState, mapController)

        // Markiere Pfad als CYAN
        for ((px, py) in path) {
            val oldCell = labyrinthState.getCell(px, py) ?: Cell()
            if (!oldCell.isBlocked && oldCell.color == Color.LIGHT_GRAY) {
                val newBorders = oldCell.borders.toMutableMap()

                mapController.addSimulatedCell(
                    x = px,
                    y = py,
                    color = Color.CYAN,
                    bordersMap = newBorders,
                    isEntrance  = oldCell.isEntrance,
                    isColorField= oldCell.isColorField,
                    priority    = oldCell.priority,
                    isBlocked   = oldCell.isBlocked
                )
            }
        }
        mapCanvas.repaint()
        println("Pfad erfolgreich markiert.")

        movementTimer?.stop()
        currentPath = path.toMutableList()

        movementTimer = Timer(500) {
            moveRobotOneStep(labyrinthState, mapController)
        }
        movementTimer?.start()
    }

    private fun removeCyanPath(labyrinthState: LabyrinthStateService, mapController: MapController) {
        for ((coords, cell) in labyrinthState.getCells()) {
            if (cell.color == Color.CYAN) {
                val newBorders = cell.borders.toMutableMap()  // unverändert
                mapController.addSimulatedCell(
                    coords.first, coords.second,
                    color = Color.LIGHT_GRAY,
                    bordersMap = newBorders,
                    isEntrance  = cell.isEntrance,
                    isColorField= cell.isColorField,
                    priority    = cell.priority,
                    isBlocked   = cell.isBlocked
                )
            }
        }
    }


    private fun moveRobotOneStep(labyrinthState: LabyrinthStateService, mapController: MapController) {
        if (currentPath.isEmpty()) {
            println("Pfad vollständig abgefahren. Stoppe Bewegung.")
            movementTimer?.stop()

            val pos = labyrinthState.getRobotPosition()
            if (pos != null) {
                val cell = labyrinthState.getCell(pos.first, pos.second)
                if (cell != null && cell.isColorField) {
                    println("Farbziel erreicht bei $pos.")
                    // isColorField = false
                    val updatedCell = cell.copy(isColorField = false)
                    val newBorders  = updatedCell.borders.toMutableMap()

                    mapController.addSimulatedCell(
                        x = pos.first,
                        y = pos.second,
                        color      = updatedCell.color,
                        bordersMap = newBorders,
                        isEntrance = updatedCell.isEntrance,
                        isColorField = false,
                        priority   = updatedCell.priority,
                        isBlocked  = updatedCell.isBlocked
                    )

                    // Nächstes Ziel
                    calculateAndMoveToNextTarget(labyrinthState, mapController)
                }
            }
            return
        }

        val nextCoord = currentPath.removeAt(0)
        println("Bewege Roboter zu: $nextCoord")

        val oldPos = labyrinthState.getRobotPosition()
        if (oldPos != null) {
            val dx = nextCoord.first - oldPos.first
            val dy = nextCoord.second - oldPos.second
            val newDirection = when {
                dx > 0 -> RoboterDirection.EAST
                dx < 0 -> RoboterDirection.WEST
                dy > 0 -> RoboterDirection.SOUTH
                else   -> RoboterDirection.NORTH
            }
            mapCanvas.updateRobotDirection(newDirection)
        }

        labyrinthState.setRobotPosition(nextCoord.first, nextCoord.second)
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

