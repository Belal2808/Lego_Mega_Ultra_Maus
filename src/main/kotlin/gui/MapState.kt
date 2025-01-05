package de.fhkiel.rob.labyrinth.gui

import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import de.fhkiel.rob.legoosctester.gui.MapController
import java.awt.Color
import java.util.*

class MapState {
    private val cells = mutableMapOf<Pair<Int, Int>, Cell>() // Map von Koordinaten zu Zellen

    // Fügt eine Zelle hinzu oder aktualisiert sie
    fun addCell(x: Int, y: Int, cell: Cell) {
        cells[Pair(x, y)] = cell
    }

    // Ruft alle Zellen ab
    fun getCells(): Map<Pair<Int, Int>, Cell> {
        return cells
    }
    fun getNeighbors(x: Int, y: Int): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>()

        val currentCell = getCell(x, y)
        if (currentCell == null) {
            println("Warnung: Zelle ($x, $y) existiert nicht.")
            return emptyList()
        }

        // Nachbarn prüfen und bidirektionale Verbindungen sicherstellen
        if (currentCell.north != CellBoarder.WALL) {
            val northCell = getCell(x, y - 1)
            if (northCell != null && northCell.south != CellBoarder.WALL && !northCell.isBlocked) {
                neighbors.add(Pair(x, y - 1))
            }
        }
        if (currentCell.east != CellBoarder.WALL) {
            val eastCell = getCell(x + 1, y)
            if (eastCell != null && eastCell.west != CellBoarder.WALL && !eastCell.isBlocked) {
                neighbors.add(Pair(x + 1, y))
            }
        }
        if (currentCell.south != CellBoarder.WALL) {
            val southCell = getCell(x, y + 1)
            if (southCell != null && southCell.north != CellBoarder.WALL && !southCell.isBlocked) {
                neighbors.add(Pair(x, y + 1))
            }
        }
        if (currentCell.west != CellBoarder.WALL) {
            val westCell = getCell(x - 1, y)
            if (westCell != null && westCell.east != CellBoarder.WALL && !westCell.isBlocked) {
                neighbors.add(Pair(x - 1, y))
            }
        }

        println("Ermittelte Nachbarn für Zelle ($x, $y): $neighbors")
        return neighbors
    }




    // Überprüft, ob eine Zelle an einer bestimmten Position existiert
    fun hasCell(x: Int, y: Int): Boolean {
        return cells.containsKey(Pair(x, y))
    }

    // Ruft eine Zelle ab, wenn sie existiert, sonst null
    fun getCell(x: Int, y: Int): Cell? {
        return cells[Pair(x, y)]
    }
    fun findPathDijkstra(start: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>> {
        // Überprüfen, ob Start und Ziel existieren
        if (getCell(start.first, start.second) == null) {
            println("Startpunkt $start existiert nicht.")
            return emptyList()
        }
        if (getCell(goal.first, goal.second) == null) {
            println("Zielpunkt $goal existiert nicht.")
            return emptyList()
        }

        // Überprüfen, ob Startpunkt Nachbarn hat
        if (getNeighbors(start.first, start.second).isEmpty()) {
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
            for (neighbor in getNeighbors(current.first, current.second)) {
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