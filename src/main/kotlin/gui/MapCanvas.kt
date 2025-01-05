import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import de.fhkiel.rob.legoosctester.Direction
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class MapCanvas : JPanel() {
    private val mapState = MapState() // Zustand der Karte
    var robotDirection: Direction = Direction.NORTH // Aktuelle Richtung des Roboters
    var robotPosition: Pair<Int, Int>? = null       // Aktuelle Position des Roboters

    fun addCell(x: Int, y: Int, cell: Cell) {
        mapState.addCell(x, y, cell)
        repaint() // Zeichne die Karte neu
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val cellSize = 50
        val wallThickness = 5

        // --- Zeichne jede Zelle ---
        for ((position, cell) in mapState.getCells()) {
            val (x, y) = position
            val px = x * cellSize
            val py = y * cellSize

            // 1) Zellenfarbe
            g.color = cell.color
            g.fillRect(px, py, cellSize, cellSize)

            // 2) Wände
            g.color = Color.BLACK
            if (cell.north == CellBoarder.WALL) {
                g.fillRect(px, py, cellSize, wallThickness)
            }
            if (cell.south == CellBoarder.WALL) {
                g.fillRect(px, py + cellSize - wallThickness, cellSize, wallThickness)
            }
            if (cell.west == CellBoarder.WALL) {
                g.fillRect(px, py, wallThickness, cellSize)
            }
            if (cell.east == CellBoarder.WALL) {
                g.fillRect(px + cellSize - wallThickness, py, wallThickness, cellSize)
            }

            // 3) Eingang (E)
            if (cell.isEntrance) {
                g.color = Color.YELLOW
                g.drawRect(px + 5, py + 5, cellSize - 10, cellSize - 10)
            }

            // 4) Blockierte Zellen (X)
            if (cell.isBlocked) {
                g.color = Color.RED
                g.drawLine(px, py, px + cellSize, py + cellSize)  // Diagonale 1
                g.drawLine(px, py + cellSize, px + cellSize, py)  // Diagonale 2
                g.drawString("X", px + cellSize / 4, py + 3 * cellSize / 4)
            }
        }

        // --- Zeichne den Roboter ---
        robotPosition?.let { (rx, ry) ->
            val px = rx * cellSize + cellSize / 2
            val py = ry * cellSize + cellSize / 2
            val size = cellSize / 3

            g.color = Color.MAGENTA

            // NEU: Errechne je nach robotDirection die Eckpunkte des Dreiecks
            val (xPoints, yPoints) = getRobotTrianglePoints(px, py, size, robotDirection)

            g.fillPolygon(xPoints, yPoints, 3)
        }
    }

    /**
     * Hilfsfunktion zum Ermitteln der Eckpunkte des Roboter-Dreiecks
     * abhängig von der aktuellen Richtung.
     */
    private fun getRobotTrianglePoints(
        centerX: Int,
        centerY: Int,
        size: Int,
        direction: Direction
    ): Pair<IntArray, IntArray> {
        return when (direction) {
            Direction.NORTH -> {
                // Spitze nach oben
                val xPoints = intArrayOf(centerX, centerX - size, centerX + size)
                val yPoints = intArrayOf(centerY - size, centerY + size, centerY + size)
                Pair(xPoints, yPoints)
            }
            Direction.SOUTH -> {
                // Spitze nach unten
                val xPoints = intArrayOf(centerX, centerX - size, centerX + size)
                val yPoints = intArrayOf(centerY + size, centerY - size, centerY - size)
                Pair(xPoints, yPoints)
            }
            Direction.EAST -> {
                // Spitze nach rechts
                val xPoints = intArrayOf(centerX + size, centerX - size, centerX - size)
                val yPoints = intArrayOf(centerY, centerY - size, centerY + size)
                Pair(xPoints, yPoints)
            }
            Direction.WEST -> {
                // Spitze nach links
                val xPoints = intArrayOf(centerX - size, centerX + size, centerX + size)
                val yPoints = intArrayOf(centerY, centerY - size, centerY + size)
                Pair(xPoints, yPoints)
            }
        }
    }

    // Aktualisiert nur die Richtung, repaint() ruft paintComponent() neu auf
    fun updateRobotDirection(direction: Direction) {
        robotDirection = direction
        println("Aktuelle Richtung des Roboters: $robotDirection")
        repaint()
    }

    // Aktualisiert die Position des Roboters und zeichnet neu
    fun updateRobotPosition(x: Int, y: Int) {
        robotPosition = Pair(x, y)
        println("Aktuelle Position des Roboters: $robotPosition")
        repaint()
    }
}
