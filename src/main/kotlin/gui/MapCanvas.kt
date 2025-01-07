package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import de.fhkiel.rob.legoosctester.RoboterDirection
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class MapCanvas : JPanel() {

    private val mapState = MapState() // Zustand der Karte

    // Roboter-Richtung: RoboterDirection statt Direction
    var robotDirection: RoboterDirection = RoboterDirection.NORTH

    // Aktuelle Position des Roboters
    var robotPosition: Pair<Int, Int>? = null

    /**
     * Fügt eine Zelle hinzu und aktualisiert die Anzeige.
     */
    fun addCell(x: Int, y: Int, cell: Cell) {
        mapState.addCell(x, y, cell)
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val cellSize = 50
        val wallThickness = 5

        // Zeichne jede Zelle
        for ((position, cell) in mapState.getCells()) {
            val (cx, cy) = position
            val px = cx * cellSize
            val py = cy * cellSize

            // 1) Zellenfarbe
            g.color = cell.color
            g.fillRect(px, py, cellSize, cellSize)

            // 2) Walls anhand der borders-Map:
            //    cell.getBorder(RoboterDirection.NORTH) / .EAST / .SOUTH / .WEST
            g.color = Color.BLACK

            // NORTH
            if (cell.getBorder(RoboterDirection.NORTH) == CellBoarder.WALL) {
                g.fillRect(px, py, cellSize, wallThickness)
            }
            // SOUTH
            if (cell.getBorder(RoboterDirection.SOUTH) == CellBoarder.WALL) {
                g.fillRect(px, py + cellSize - wallThickness, cellSize, wallThickness)
            }
            // WEST
            if (cell.getBorder(RoboterDirection.WEST) == CellBoarder.WALL) {
                g.fillRect(px, py, wallThickness, cellSize)
            }
            // EAST
            if (cell.getBorder(RoboterDirection.EAST) == CellBoarder.WALL) {
                g.fillRect(px + cellSize - wallThickness, py, wallThickness, cellSize)
            }

            // 3) Eingang? (gelber Rahmen)
            if (cell.isEntrance) {
                g.color = Color.YELLOW
                g.drawRect(px + 5, py + 5, cellSize - 10, cellSize - 10)
            }

            // 4) Blockiert? (rotes X)
            if (cell.isBlocked) {
                g.color = Color.RED
                // Diagonalen
                g.drawLine(px, py, px + cellSize, py + cellSize)
                g.drawLine(px, py + cellSize, px + cellSize, py)
                g.drawString("X", px + cellSize / 4, py + (3 * cellSize / 4))
            }
        }

        // Zeichne den Roboter
        robotPosition?.let { (rx, ry) ->
            val px = rx * cellSize + cellSize / 2
            val py = ry * cellSize + cellSize / 2
            val size = cellSize / 3

            g.color = Color.MAGENTA
            val (xPoints, yPoints) = getRobotTrianglePoints(px, py, size, robotDirection)
            g.fillPolygon(xPoints, yPoints, 3)
        }
    }

    /**
     * Bestimmt die Eckpunkte des Roboter-Dreiecks in Abhängigkeit von RoboterDirection.
     */
    private fun getRobotTrianglePoints(
        centerX: Int,
        centerY: Int,
        size: Int,
        direction: RoboterDirection
    ): Pair<IntArray, IntArray> {
        return when (direction) {
            RoboterDirection.NORTH -> {
                val xPoints = intArrayOf(centerX, centerX - size, centerX + size)
                val yPoints = intArrayOf(centerY - size, centerY + size, centerY + size)
                Pair(xPoints, yPoints)
            }
            RoboterDirection.SOUTH -> {
                val xPoints = intArrayOf(centerX, centerX - size, centerX + size)
                val yPoints = intArrayOf(centerY + size, centerY - size, centerY - size)
                Pair(xPoints, yPoints)
            }
            RoboterDirection.EAST -> {
                val xPoints = intArrayOf(centerX + size, centerX - size, centerX - size)
                val yPoints = intArrayOf(centerY, centerY - size, centerY + size)
                Pair(xPoints, yPoints)
            }
            RoboterDirection.WEST -> {
                val xPoints = intArrayOf(centerX - size, centerX + size, centerX + size)
                val yPoints = intArrayOf(centerY, centerY - size, centerY + size)
                Pair(xPoints, yPoints)
            }
        }
    }

    // Aktualisiert die Richtung
    fun updateRobotDirection(direction: RoboterDirection) {
        robotDirection = direction
        println("Aktuelle Richtung des Roboters: $robotDirection")
        repaint()
    }

    // Aktualisiert die Roboterposition
    fun updateRobotPosition(x: Int, y: Int) {
        robotPosition = Pair(x, y)
        println("Aktuelle Position des Roboters: $robotPosition")
        repaint()
    }
}
