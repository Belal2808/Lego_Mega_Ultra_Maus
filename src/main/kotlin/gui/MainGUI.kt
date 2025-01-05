package de.fhkiel.rob.legoosctester.gui

import MapCanvas
import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import de.fhkiel.rob.legoosctester.Direction
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.IOException
import javax.swing.JFrame
import javax.swing.Timer
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class MainGUI : JFrame() {
    private var x = 0
    private var y = 0
    private var currentColor = Color.LIGHT_GRAY
    private var currentDirection = Direction.NORTH

    val mapCanvas = MapCanvas()

    // 1) Timer + aktueller Pfad
    private var movementTimer: Timer? = null
    private var currentPath: MutableList<Pair<Int, Int>> = mutableListOf()

    init {
        title = "Labyrinth Map"
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE

        val mapState = MapState()
        val mapController = MapController(mapState, mapCanvas)

        add(mapCanvas)

        addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_M -> {
                        resetColorFields(mapState, mapController)
                    }

                    KeyEvent.VK_W -> {
                        y-- // Bewegung nach oben
                        mapCanvas.updateRobotDirection(Direction.NORTH)
                    }
                    KeyEvent.VK_S -> {
                        y++ // Bewegung nach unten
                        mapCanvas.updateRobotDirection(Direction.SOUTH)
                    }
                    KeyEvent.VK_A -> {
                        x-- // Bewegung nach links
                        mapCanvas.updateRobotDirection(Direction.WEST)
                    }
                    KeyEvent.VK_D -> {
                        x++ // Bewegung nach rechts
                        mapCanvas.updateRobotDirection(Direction.EAST)
                    }

                    KeyEvent.VK_ENTER -> {
                        // Erstelle eine neue Zelle oder aktualisiere die bestehende
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            north = CellBoarder.UNDISCOVERED,
                            east = CellBoarder.UNDISCOVERED,
                            south = CellBoarder.UNDISCOVERED,
                            west = CellBoarder.UNDISCOVERED
                        )
                        println("Zelle erstellt bei ($x, $y)")
                    }

                    KeyEvent.VK_SPACE -> {
                        // Aktualisiere die Wand basierend auf der aktuellen Richtung
                        val cell = mapState.getCells()[Pair(x, y)] ?: Cell(
                            north = CellBoarder.UNDISCOVERED,
                            east = CellBoarder.UNDISCOVERED,
                            south = CellBoarder.UNDISCOVERED,
                            west = CellBoarder.UNDISCOVERED,
                            color = currentColor
                        )

                        val updatedCell = when (currentDirection) {
                            Direction.NORTH -> cell.copy(north = CellBoarder.WALL)
                            Direction.SOUTH -> cell.copy(south = CellBoarder.WALL)
                            Direction.WEST -> cell.copy(west = CellBoarder.WALL)
                            Direction.EAST -> cell.copy(east = CellBoarder.WALL)
                        }

                        mapController.addSimulatedCell(
                            x, y,
                            updatedCell.color,
                            updatedCell.north,
                            updatedCell.east,
                            updatedCell.south,
                            updatedCell.west
                        )
                        println("Wand hinzugefügt bei ($x, $y) in Richtung $currentDirection")
                    }

                    KeyEvent.VK_N -> {
                        // 1) Hole alte Zelle oder lege eine Default-Zelle an,
                        //    damit wir die alten Werte (bes. isColorField) beibehalten können
                        val oldCell = mapState.getCell(x, y) ?: Cell(
                            north = CellBoarder.UNDISCOVERED,
                            east  = CellBoarder.UNDISCOVERED,
                            south = CellBoarder.UNDISCOVERED,
                            west  = CellBoarder.UNDISCOVERED,
                            color = Color.LIGHT_GRAY,
                            isEntrance   = false,
                            isColorField = false,
                            priority     = 0,
                            isBlocked    = false
                        )

                        // 2) Färbe nur die Zelle grau, behalte aber isColorField usw.
                        mapController.addSimulatedCell(
                            x, y,
                            color       = Color.LIGHT_GRAY,
                            north       = oldCell.north,
                            east        = oldCell.east,
                            south       = oldCell.south,
                            west        = oldCell.west,
                            isEntrance  = oldCell.isEntrance,
                            isColorField= oldCell.isColorField, // <-- beibehalten!
                            priority    = oldCell.priority,
                            isBlocked   = oldCell.isBlocked
                        )
                        println("Feld als NONE (grau) markiert, ohne isColorField zu verlieren, bei ($x, $y)")
                    }




                    KeyEvent.VK_E -> {
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            isEntrance = true
                        )
                        println("Eingang gesetzt bei ($x, $y)")
                        mapCanvas.updateRobotPosition(x, y) // Setzt die Roboterposition
                    }

                    KeyEvent.VK_R -> {
                        currentColor = Color.RED
                        println("Farbplatte Rot ausgewählt")
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            isColorField = true,
                            priority = 1
                        )
                    }

                    KeyEvent.VK_G -> {
                        currentColor = Color.GREEN
                        println("Farbplatte Grün ausgewählt")
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            isColorField = true,
                            priority = 2
                        )
                    }

                    KeyEvent.VK_B -> {
                        currentColor = Color.BLUE
                        println("Farbplatte Blau ausgewählt")
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            isColorField = true,
                            priority = 3
                        )
                    }

                    KeyEvent.VK_X -> {
                        // Markiere die aktuelle Zelle als blockiert
                        mapController.addSimulatedCell(
                            x, y,
                            color = currentColor,
                            isBlocked = true
                        )
                        println("Blockierte Zelle gesetzt bei ($x, $y)")
                    }

                    KeyEvent.VK_P -> {
                        // Exportiere die Karte in eine Datei
                        try {
                            exportMap(mapState, "labyrinth.json")
                            println("Karte exportiert in labyrinth.json")
                        } catch (e: IOException) {
                            println("Fehler beim Export: ${e.message}")
                        }
                    }

                    KeyEvent.VK_L -> {
                        loadMap(mapState, "labyrinth.json")
                        println("Karte aus labyrinth.json geladen")
                        val entrance = mapState.getCells().filterValues { it.isEntrance }.keys.firstOrNull()
                        entrance?.let { mapCanvas.updateRobotPosition(it.first, it.second) } // Setzt die Roboterposition
                    }

                    KeyEvent.VK_1 -> {
                        currentDirection = Direction.NORTH
                        println("Richtung geändert: Norden")
                    }
                    KeyEvent.VK_2 -> {
                        currentDirection = Direction.EAST
                        println("Richtung geändert: Osten")
                    }
                    KeyEvent.VK_3 -> {
                        currentDirection = Direction.SOUTH
                        println("Richtung geändert: Süden")
                    }
                    KeyEvent.VK_4 -> {
                        currentDirection = Direction.WEST
                        println("Richtung geändert: Westen")
                    }
                    KeyEvent.VK_F -> {
                        // Taste F: Pfad suchen + automatisch zum Ziel fahren
                        calculateAndMoveToNextTarget(mapState, mapController)
                    }
                }

                // Aktualisiere die Roboterposition (manuell durch WASD)
                mapCanvas.updateRobotPosition(x, y)
                println("Position: ($x, $y), Farbe: $currentColor, Richtung: $currentDirection")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }

    // --------------------------------------------------------
    // Export/Load
    // --------------------------------------------------------
    private fun exportMap(mapState: MapState, filename: String) {
        val file = File(filename)
        val jsonArray = JSONArray()

        mapState.getCells().forEach { (coords, cell) ->
            val cellJson = JSONObject()
            cellJson.put("x", coords.first)
            cellJson.put("y", coords.second)
            cellJson.put("north", cell.north.name)
            cellJson.put("east", cell.east.name)
            cellJson.put("south", cell.south.name)
            cellJson.put("west", cell.west.name)
            cellJson.put("color", String.format("#%06X", 0xFFFFFF and cell.color.rgb))
            cellJson.put("isEntrance", cell.isEntrance)
            cellJson.put("isColorField", cell.isColorField)
            cellJson.put("priority", cell.priority)
            cellJson.put("isBlocked", cell.isBlocked)
            jsonArray.put(cellJson)
        }

        file.writeText(jsonArray.toString(4)) // Formatierte Ausgabe
    }

    private fun loadMap(mapState: MapState, filename: String) {
        val file = File(filename)
        val jsonArray = JSONArray(file.readText())

        val cellsMap = mapState.getCells() as MutableMap<Pair<Int, Int>, Cell>
        cellsMap.clear()

        for (i in 0 until jsonArray.length()) {
            val cellJson = jsonArray.getJSONObject(i)
            val x = cellJson.getInt("x")
            val y = cellJson.getInt("y")
            val cell = Cell(
                north = CellBoarder.valueOf(cellJson.getString("north")),
                east = CellBoarder.valueOf(cellJson.getString("east")),
                south = CellBoarder.valueOf(cellJson.getString("south")),
                west = CellBoarder.valueOf(cellJson.getString("west")),
                color = Color.decode(cellJson.getString("color")),
                isEntrance = cellJson.getBoolean("isEntrance"),
                isColorField = cellJson.getBoolean("isColorField"),
                priority = cellJson.getInt("priority"),
                isBlocked = cellJson.getBoolean("isBlocked")
            )
            cellsMap[Pair(x, y)] = cell
        }

        // Aktualisiere die Darstellung der geladenen Zellen
        cellsMap.forEach { (coords, cell) ->
            mapCanvas.addCell(coords.first, coords.second, cell)
        }

        mapCanvas.repaint() // Stelle sicher, dass die GUI neu gezeichnet wird
    }

    // --------------------------------------------------------
    // Pfadberechnung + Automatische Bewegung
    // --------------------------------------------------------

    /**
     * Sucht den nächsten Pfad und markiert ihn.
     * Startet außerdem einen Timer, der den Roboter automatisch Schritt für Schritt bewegt.
     */
    private fun calculateAndMoveToNextTarget(mapState: MapState, mapController: MapController) {
        val currentPosition = mapCanvas.robotPosition

        if (currentPosition == null) {
            println("Fehler: Roboterposition ist nicht definiert!")
            return
        }

        println("Aktueller Startpunkt: $currentPosition")

        // (A) Liste aller Farbzellen:
        val targets = mapState.getCells()
            .filterValues { it.isColorField }
            .keys
        println("Aktuelle Farbziele: $targets")

        if (targets.isEmpty()) {
            println("Keine Farbzellen verfügbar.")
            removeCyanPath(mapState, mapController)
            return
        }

        // (B) Wähle nächstes Ziel
        val nextTarget = targets.minByOrNull { target ->
            val path = mapState.findPathDijkstra(currentPosition, target)
            if (path.isNotEmpty()) path.size else Int.MAX_VALUE
        }

        if (nextTarget == null) {
            println("Kein gültiger Pfad zu einer Farbzelle gefunden.")
            removeCyanPath(mapState, mapController)
            return
        }

        // *** NEU: Wenn das Ziel == currentPosition => isColorField deaktivieren und Abbruch ***
        if (nextTarget == currentPosition) {
            println("Nächstes Ziel ist gleich aktueller Standort: $nextTarget.")
            val cellHere = mapState.getCell(currentPosition.first, currentPosition.second)
            if (cellHere != null && cellHere.isColorField) {
                println("Deaktiviere isColorField bei $currentPosition, um Endlosschleifen zu vermeiden.")
                mapController.addSimulatedCell(
                    currentPosition.first, currentPosition.second,
                    color       = cellHere.color,
                    north       = cellHere.north,
                    east        = cellHere.east,
                    south       = cellHere.south,
                    west        = cellHere.west,
                    isEntrance  = cellHere.isEntrance,
                    isColorField= false, // --> Ziel nicht mehr Farb-Zelle
                    priority    = cellHere.priority,
                    isBlocked   = cellHere.isBlocked
                )
            }
            return
        }

        // (C) Dijkstra zum gewählten Ziel
        val path = mapState.findPathDijkstra(currentPosition, nextTarget)
        if (path.isEmpty()) {
            println("Kein gültiger Pfad zum Ziel gefunden (Blockaden, Wände oder fehlender Boden).")
            removeCyanPath(mapState, mapController)
            return
        }

        println("Berechneter Pfad zur nächsten Farbzelle: $path")

        // Alte Pfadmarkierungen entfernen
        removeCyanPath(mapState, mapController)

        // Neuen Pfad auf CYAN setzen
        path.forEach { (px, py) ->
            val cell = mapState.getCell(px, py)
            if (cell != null && !cell.isBlocked && cell.color == Color.LIGHT_GRAY) {
                mapController.addSimulatedCell(
                    px, py,
                    color = Color.CYAN,
                    north = cell.north,
                    east  = cell.east,
                    south = cell.south,
                    west  = cell.west,
                    isEntrance  = cell.isEntrance,
                    isColorField= cell.isColorField,
                    priority    = cell.priority,
                    isBlocked   = cell.isBlocked
                )
            }
        }
        mapCanvas.repaint()
        println("Pfad erfolgreich markiert.")

        // Timer anhalten (falls noch einer läuft)
        movementTimer?.stop()

        // Pfad übernehmen
        currentPath = path.toMutableList()

        // Neuen Timer starten
        movementTimer = Timer(500) {
            moveRobotOneStep(mapState, mapController)
        }
        movementTimer?.start()
    }

    /**
     * Löscht sämtliche CYAN-Zellen (alte Pfadmarkierungen), indem sie wieder auf LIGHT_GRAY gesetzt werden.
     */
    private fun removeCyanPath(mapState: MapState, mapController: MapController) {
        mapState.getCells().forEach { (coords, cell) ->
            if (cell.color == Color.CYAN) {
                mapController.addSimulatedCell(
                    coords.first, coords.second,
                    color = Color.LIGHT_GRAY,
                    north = cell.north,
                    east = cell.east,
                    south = cell.south,
                    west = cell.west,
                    isEntrance = cell.isEntrance,
                    isColorField = cell.isColorField,
                    priority = cell.priority,
                    isBlocked = cell.isBlocked
                )
            }
        }
    }

    /**
     * Bewegt den Roboter einen Schritt entlang des aktuellen Pfads (currentPath).
     * Wenn der Pfad abgefahren ist, beendet den Timer und prüft, ob wir auf einer Farbzelle stehen.
     * Falls ja, erneut calculateAndMoveToNextTarget (z.B. für das nächste Farbziel).
     */
    private fun moveRobotOneStep(mapState: MapState, mapController: MapController) {
        // 1) Keine Schritte mehr übrig => Wir haben das aktuelle Ziel erreicht
        if (currentPath.isEmpty()) {
            println("Pfad vollständig abgefahren. Stoppe Bewegung.")
            movementTimer?.stop()

            val pos = mapCanvas.robotPosition
            if (pos != null) {
                // Hole die Zell-Daten
                val cell = mapState.getCell(pos.first, pos.second)
                // Prüfen: Ist es (noch) Farbzelle?
                if (cell != null && cell.isColorField) {
                    println("Farbziel erreicht bei $pos.")

                    // Mit der NEUEN addSimulatedCell(...) => isColorField = false
                    mapController.addSimulatedCell(
                        x = pos.first,
                        y = pos.second,
                        color       = cell.color,
                        north       = cell.north,
                        east        = cell.east,
                        south       = cell.south,
                        west        = cell.west,
                        isEntrance  = cell.isEntrance,
                        isColorField= false,  // <-- Deaktivieren
                        priority    = cell.priority,
                        isBlocked   = cell.isBlocked
                    )

                    // => Starte sofort das nächste Ziel
                    calculateAndMoveToNextTarget(mapState, mapController)
                }
            }
            return
        }

        // 2) Wir haben noch Schritte übrig => bewege den Roboter ein Feld weiter
        val nextCoord = currentPath.removeAt(0)
        println("Bewege Roboter zu: $nextCoord")

        // Richtung automatisch setzen
        val oldPos = mapCanvas.robotPosition
        if (oldPos != null) {
            val dx = nextCoord.first - oldPos.first
            val dy = nextCoord.second - oldPos.second
            val newDirection = when {
                dx > 0 -> Direction.EAST
                dx < 0 -> Direction.WEST
                dy > 0 -> Direction.SOUTH
                else   -> Direction.NORTH
            }
            mapCanvas.updateRobotDirection(newDirection)
        }

        // Roboter verschieben
        mapCanvas.updateRobotPosition(nextCoord.first, nextCoord.second)
    }

    private fun resetColorFields(mapState: MapState, mapController: MapController) {
        // Durchlaufe alle Zellen
        for ((coords, cell) in mapState.getCells()) {
            // Prüfe, ob die Farbe (rot, grün oder blau) ist
            // und ob isColorField = false ist (abgefahren)
            val c = cell.color
            val isRGB = (c == Color.RED || c == Color.GREEN || c == Color.BLUE)

            if (isRGB && !cell.isColorField) {
                // Reaktiviere diese Zelle
                mapController.addSimulatedCell(
                    coords.first, coords.second,
                    // Farbe bleibt die gleiche
                    color       = cell.color,
                    north       = cell.north,
                    east        = cell.east,
                    south       = cell.south,
                    west        = cell.west,
                    isEntrance  = cell.isEntrance,
                    isColorField= true,    // <-- wieder aktivieren
                    priority    = cell.priority,
                    isBlocked   = cell.isBlocked
                )
                println("Zelle $coords mit Farbe $c wieder als isColorField=true aktiviert.")
            }
        }
        println("Alle R/G/B-Zellen wurden reaktiviert, du kannst F erneut verwenden.")
    }

}
