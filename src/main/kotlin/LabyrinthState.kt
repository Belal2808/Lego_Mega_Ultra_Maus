package de.fhkiel.rob.legoosctester

import jdk.internal.net.http.common.Pair.pair
import java.awt.Color
import java.util.PriorityQueue

class LabyrinthState(rows: Int, columns: Int) : LabyrinthStateService {
    private var currentX : Int = rows/2
    private var currentY : Int = columns/2
    private val labyrinth = mutableMapOf<Pair<Int, Int>, Cell>() // Map von Koordinaten zu Zellen

    /**
     * Fügt eine Zelle hinzu oder aktualisiert sie.
     */
    override fun addCell(x: Int, y: Int, cell: Cell) {
        labyrinth[Pair(x, y)] = cell
    }

    /**
     * Ruft alle Zellen ab.
     */
    override fun getCells(): Map<Pair<Int, Int>, Cell> {
        return labyrinth
    }

    /**
     * Ruft eine Zelle ab, wenn sie existiert, sonst null.
     */
    override fun getCell(x: Int, y: Int): Cell? {
        return labyrinth[Pair(x, y)]
    }


    override fun updateCell(x: Int, y: Int, cell: Cell) {
        labyrinth[Pair(x, y)] = cell
    }

    override fun getRobotPosition(): Pair<Int, Int> {
        return Pair(currentX, currentY)
    }

    override fun setRobotPosition(x: Int, y: Int) {
        currentX = x
        currentY = y
    }

    fun getNeighbors(x: Int, y: Int): List<Pair<Int, Int>> {
        val neighbors = mutableListOf<Pair<Int, Int>>()

        val currentCell = getCell(x, y)
        if (currentCell == null) {
            println("Warnung: Zelle ($x, $y) existiert nicht.")
            return emptyList()
        }

        // Nachbarn prüfen und bidirektionale Verbindungen sicherstellen
        // NORTH
        if (currentCell.getBorder(RoboterDirection.NORTH) != CellBoarder.WALL) {
            val northCell = getCell(x, y - 1)
            if (northCell != null &&
                northCell.getBorder(RoboterDirection.SOUTH) != CellBoarder.WALL &&
                !northCell.isBlocked
            ) {
                neighbors.add(Pair(x, y - 1))
            }
        }

        // EAST
        if (currentCell.getBorder(RoboterDirection.EAST) != CellBoarder.WALL) {
            val eastCell = getCell(x + 1, y)
            if (eastCell != null &&
                eastCell.getBorder(RoboterDirection.WEST) != CellBoarder.WALL &&
                !eastCell.isBlocked
            ) {
                neighbors.add(Pair(x + 1, y))
            }
        }

        // SOUTH
        if (currentCell.getBorder(RoboterDirection.SOUTH) != CellBoarder.WALL) {
            val southCell = getCell(x, y + 1)
            if (southCell != null &&
                southCell.getBorder(RoboterDirection.NORTH) != CellBoarder.WALL &&
                !southCell.isBlocked
            ) {
                neighbors.add(Pair(x, y + 1))
            }
        }

        // WEST
        if (currentCell.getBorder(RoboterDirection.WEST) != CellBoarder.WALL) {
            val westCell = getCell(x - 1, y)
            if (westCell != null &&
                westCell.getBorder(RoboterDirection.EAST) != CellBoarder.WALL &&
                !westCell.isBlocked
            ) {
                neighbors.add(Pair(x - 1, y))
            }
        }

        println("Ermittelte Nachbarn für Zelle ($x, $y): $neighbors")
        return neighbors
    }

    /**
     * Findet einen Pfad von Start zu Ziel unter Verwendung des Dijkstra-Algorithmus.
     */
    override fun findPathDijkstra(start: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>> {
        // Überprüfen, ob Start und Ziel existieren
        if (getCell(start.first,start.second) == null) {
            println("Startpunkt $start existiert nicht.")
            return emptyList()
        }
        if (getCell(goal.first,goal.second) == null) {
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
 override fun processColorSensorData(args: List<Any>) {
        val colorString = args[0] as String


        labyrinth[Pair(currentX,currentY)]!!.color = when (colorString.lowercase()) {
            "black" -> Color.BLACK
            "blue" -> Color.BLUE
            "green" -> Color.GREEN
            "yellow" -> Color.YELLOW
            "red" -> Color.RED
            "white" -> Color.WHITE
            "brown" -> Color(139, 69, 19)
            else -> Color.LIGHT_GRAY
        }

    }


}