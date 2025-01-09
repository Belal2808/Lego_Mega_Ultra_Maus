package de.fhkiel.rob.legoosctester

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

    override fun getNeighbors(x: Int, y: Int): List<Pair<Int, Int>> {
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