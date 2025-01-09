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
    private  val roboterState: RobotStateService = getKoin().get()

    // MapCanvas liegt im selben Package (ggf. anpassen!)
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
                        mapCanvas.updateRobotDirection(roboterState.getRoboterDirection())
                    }
                    KeyEvent.VK_S -> {
                        labyrinthExplorer.driveSouth()
                        mapCanvas.updateRobotDirection(roboterState.getRoboterDirection())
                    }
                    KeyEvent.VK_A -> {
                        labyrinthExplorer.driveWest()
                        mapCanvas.updateRobotDirection(roboterState.getRoboterDirection())
                    }
                    KeyEvent.VK_D -> {
                        labyrinthExplorer.driveEast()
                        mapCanvas.updateRobotDirection(roboterState.getRoboterDirection())
                    }


                    // ENTER => Neue Zelle mit UNDISCOVERED-Borders
                    KeyEvent.VK_ENTER -> {
                        labyrinthExplorer.scanCell()
                        println(labyrinthState.getCurrentCell()!!.borders.toString())
                    }

                    // N => Grau färben, isColorField behalten
                    KeyEvent.VK_N -> {
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = Color.LIGHT_GRAY,
                            bordersMap = newBorders,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= oldCell.isColorField,
                            priority    = oldCell.priority,
                            isBlocked   = oldCell.isBlocked
                        )
                        println("Zelle bei ($x, $y) auf Grau gesetzt (isColorField bleibt).")
                    }

                    // E => Eingang
                    KeyEvent.VK_E -> {
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            bordersMap = newBorders,
                            isEntrance = true,
                            isColorField= oldCell.isColorField,
                            priority    = oldCell.priority,
                            isBlocked   = oldCell.isBlocked
                        )
                        println("Eingang gesetzt bei ($x, $y)")
                        mapCanvas.updateRobotPosition(x, y)
                    }

                    // R/G/B => Farbzellen
                    KeyEvent.VK_R -> {
                        currentColor = Color.RED
                        println("Farbplatte Rot ausgewählt")
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = Color.RED,
                            bordersMap = newBorders,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= true,
                            priority    = 1,
                            isBlocked   = oldCell.isBlocked
                        )
                    }
                    KeyEvent.VK_G -> {
                        currentColor = Color.GREEN
                        println("Farbplatte Grün ausgewählt")
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = Color.GREEN,
                            bordersMap = newBorders,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= true,
                            priority    = 2,
                            isBlocked   = oldCell.isBlocked
                        )
                    }
                    KeyEvent.VK_B -> {
                        currentColor = Color.BLUE
                        println("Farbplatte Blau ausgewählt")
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = Color.BLUE,
                            bordersMap = newBorders,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= true,
                            priority    = 3,
                            isBlocked   = oldCell.isBlocked
                        )
                    }

                    // X => Blockiert
                    KeyEvent.VK_X -> {
                        val oldCell = labyrinthState.getCell(x, y) ?: Cell()
                        val newBorders = oldCell.borders.toMutableMap()

                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            bordersMap = newBorders,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= oldCell.isColorField,
                            priority    = oldCell.priority,
                            isBlocked   = true
                        )
                        println("Blockierte Zelle gesetzt bei ($x, $y)")
                    }

                    // P => Export
                    KeyEvent.VK_P -> {
                        try {
                            exportMap(labyrinthState, "labyrinth.json")
                            println("Karte exportiert in labyrinth.json")
                        } catch (ex: IOException) {
                            println("Fehler beim Export: ${ex.message}")
                        }
                    }

                    // L => Load
                    KeyEvent.VK_L -> {
                        loadMap(labyrinthState, "labyrinth.json")
                        println("Karte aus labyrinth.json geladen")
                        val entrance = labyrinthState.getCells()
                            .filterValues { it.isEntrance }.keys.firstOrNull()
                        entrance?.let { mapCanvas.updateRobotPosition(it.first, it.second) }
                    }

                    // F => Pfad suchen
                    KeyEvent.VK_F -> {
                        calculateAndMoveToNextTarget(labyrinthState, mapController)
                    }
                }

                // Position updaten
                mapCanvas.updateRobotPosition(labyrinthState.getRobotPosition().first,labyrinthState.getRobotPosition().second)
                println("Position: ($x, $y), Farbe: $currentColor, Richtung: ${roboterState.getRoboterDirection()}")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }
    // --------------------------------------------------------
    // Export/Load
    // --------------------------------------------------------
    private fun exportMap(labyrinthState: LabyrinthStateService, filename: String) {
        val file = File(filename)
        val jsonArray = JSONArray()

        labyrinthState.getCells().forEach { (coords, cell) ->
            val cellJson = JSONObject()
            cellJson.put("x", coords.first)
            cellJson.put("y", coords.second)

            // Borders
            val bordersObj = JSONObject()
            for ((dir, border) in cell.borders) {
                bordersObj.put(dir.name, border.name)
            }
            cellJson.put("borders", bordersObj)

            cellJson.put("color", String.format("#%06X", 0xFFFFFF and cell.color.rgb))
            cellJson.put("isEntrance", cell.isEntrance)
            cellJson.put("isColorField", cell.isColorField)
            cellJson.put("priority", cell.priority)
            cellJson.put("isBlocked", cell.isBlocked)
            jsonArray.put(cellJson)
        }

        file.writeText(jsonArray.toString(4))
    }

    private fun loadMap(labyrinthState: LabyrinthStateService, filename: String) {
        val file = File(filename)
        val jsonArray = JSONArray(file.readText())

        val cellsMap = labyrinthState.getCells() as MutableMap<Pair<Int, Int>, Cell>
        cellsMap.clear()

        for (i in 0 until jsonArray.length()) {
            val cellJson = jsonArray.getJSONObject(i)
            val x = cellJson.getInt("x")
            val y = cellJson.getInt("y")

            val bordersJson = cellJson.getJSONObject("borders")
            val bordersMap = mutableMapOf<RoboterDirection, CellBoarder>()
            for (dirName in bordersJson.keySet()) {
                val borderName = bordersJson.getString(dirName)
                val borderEnum = CellBoarder.valueOf(borderName)
                val dirEnum = RoboterDirection.valueOf(dirName)
                bordersMap[dirEnum] = borderEnum
            }

            val colorStr = cellJson.getString("color")
            val colorParsed = Color.decode(colorStr)

            val isEntrance   = cellJson.getBoolean("isEntrance")
            val isColorField = cellJson.getBoolean("isColorField")
            val priority     = cellJson.getInt("priority")
            val isBlocked    = cellJson.getBoolean("isBlocked")

            val newCell = Cell(
                borders = bordersMap,
                color = colorParsed,
                isEntrance = isEntrance,
                isColorField = isColorField,
                priority = priority,
                isBlocked = isBlocked
            )
            cellsMap[Pair(x, y)] = newCell
        }

        // Aktualisiere die Anzeige
        cellsMap.forEach { (coords, cell) ->
            mapCanvas.addCell(coords.first, coords.second, cell)
        }
        mapCanvas.repaint()
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
            val cellHere = labyrinthState.getCurrentCell()
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

        mapCanvas.updateRobotPosition(nextCoord.first, nextCoord.second)
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

