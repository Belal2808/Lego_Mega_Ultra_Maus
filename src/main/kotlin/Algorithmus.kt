package de.fhkiel.rob.legoosctester

import java.util.*
import de.fhkiel.rob.legoosctester.gui.MapController // Für GUI-Interaktion
import de.fhkiel.rob.legoosctester.LabyrinthStateService // Für Zugriff auf Zellen
import de.fhkiel.rob.legoosctester.Cell // Für Zellenobjekte
import de.fhkiel.rob.legoosctester.RoboterDirection // Für Richtungen
import de.fhkiel.rob.legoosctester.CellBoarder // Für Zellenbegrenzungen
import de.fhkiel.rob.legoosctester.gui.MapCanvas
import java.awt.Color // Für Farbinformationen
import java.util.PriorityQueue // Für den Dijkstra-Algorithmus
import javax.swing.Timer // Für Zeitsteuerung


class Algorithmus(
    private val labyrinthState: LabyrinthStateService,
    private val mapCanvas: MapCanvas
) {
    private var movementTimer: Timer? = null
    private var currentPath: MutableList<Pair<Int, Int>> = mutableListOf()
    fun findPathDijkstra(start: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>> {
        // Überprüfen, ob Start und Ziel existieren
        if (labyrinthState.getCell(start.first,start.second) == null) {
            println("Startpunkt $start existiert nicht.")
            return emptyList()
        }
        if (labyrinthState.getCell(goal.first,goal.second) == null) {
            println("Zielpunkt $goal existiert nicht.")
            return emptyList()
        }

        // Überprüfen, ob Startpunkt Nachbarn hat
        if (labyrinthState.getNeighbors(start.first, start.second).isEmpty()) {
            println("Startpunkt $start hat keine erreichbaren Nachbarn.")
            return emptyList()
        }

        // Initialisierung der Datenstrukturen
        val distances = mutableMapOf<Pair<Int, Int>, Int>().withDefault { Int.MAX_VALUE }
        val previous = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { distances.getValue(it) })

        distances[start] = 0
        priorityQueue.add(start)

        println("Dijkstra-Algorithmus gestartet: Start=$start, Ziel=$goal")

        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()

            // Ziel erreicht
            if (current == goal) {
                println("Ziel $goal erreicht.")
                break
            }

            // Nachbarn verarbeiten
            for (neighbor in labyrinthState.getNeighbors(current.first, current.second)) {
                val tentativeDistance = distances.getValue(current) + 1
                if (tentativeDistance < distances.getValue(neighbor)) {
                    distances[neighbor] = tentativeDistance
                    previous[neighbor] = current
                    priorityQueue.add(neighbor)
                    println("Aktualisiert: $neighbor mit Distanz $tentativeDistance (Vorher: $current)")
                }
            }
        }

        // Pfad zurückverfolgen
        val path = mutableListOf<Pair<Int, Int>>()
        var step: Pair<Int, Int>? = goal
        while (step != null && step != start) {
            path.add(0, step)
            step = previous[step]
        }

        if (step == start) {
            path.add(0, start)
            println("Pfad erfolgreich berechnet: $path")
        } else {
            println("Ziel $goal ist von $start aus nicht erreichbar.")
            return emptyList()
        }

        return path
    }
     fun calculateAndMoveToNextTarget(labyrinthState: LabyrinthStateService, mapController: MapController) {
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
            val path = findPathDijkstra(currentPosition, target)
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

        val path = findPathDijkstra(currentPosition, nextTarget)
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
    private fun removeCyanPath(labyrinthState: LabyrinthStateService, mapController: MapController) {
        for ((coords, cell) in labyrinthState.getCells()) {
            if (cell.color == Color.CYAN) {
                val newBorders = cell.borders.toMutableMap()
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

}