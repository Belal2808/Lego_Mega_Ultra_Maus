package de.fhkiel.rob.legoosctester.gui
import de.fhkiel.rob.legoosctester.*
import org.koin.mp.KoinPlatform.getKoin
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class MapCanvas : JPanel(), LabyrinthStateListener,RoboterStateListener {

    private val labyrinthState: LabyrinthStateService = getKoin().get()
    private val roboterState: RobotStateService = getKoin().get()
    private val cellSize = 30
    private val maxCells = 20
    var selectedCellListener: ((Pair<Int, Int>, Cell?) -> Unit)? = null
    init {
        roboterState.addListener(this)
        labyrinthState.addListener(this)

        preferredSize = Dimension(cellSize * (maxCells+1), cellSize * (maxCells+1)) // 400x400
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x / cellSize
                val y = e.y / cellSize
                val cell = labyrinthState.getCell(x, y)
                selectedCellListener?.invoke(Pair(x, y), cell) // Callback auslösen
            }
        })
    }


    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val wallThickness = 5
        val cells = labyrinthState.getCells()
        val knownCellPositions = cells.keys

        // Hintergrund und graue Raster zeichnen
        val maxCells = 20
        g.color = Color.LIGHT_GRAY // Hellgrau für unbekannte Zellumrandungen

        for (x in 0 until maxCells) {
            for (y in 0 until maxCells) {
                val px = x * cellSize
                val py = y * cellSize

                // Zeichne die grauen Rahmen für alle Zellen
                g.drawRect(px, py, cellSize, cellSize)
            }
        }

        // Zeichne jede bekannte Zelle
        for ((position, cell) in cells) {
            val (cx, cy) = position
            val px = cx * cellSize
            val py = cy * cellSize

            // 1) Zellenfarbe
            g.color = cell.color
            g.fillRect(px, py, cellSize, cellSize)

            // 2) Wände zeichnen
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
                g.drawLine(px, py, px + cellSize, py + cellSize)
                g.drawLine(px, py + cellSize, px + cellSize, py)
                g.drawString("X", px + cellSize / 4, py + (3 * cellSize / 4))
            }
        }

        // Zeichne den Roboter
        roboterState.getRobotPosition().let { (rx, ry) ->
            val px = rx * cellSize + cellSize / 2
            val py = ry * cellSize + cellSize / 2
            val size = cellSize / 3

            g.color = Color.MAGENTA
            val (xPoints, yPoints) = getRobotTrianglePoints(px, py, size, RoboterDirection.NORTH)
            g.fillPolygon(xPoints, yPoints, 3)
        }
    }



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

    override fun onStateChanged() {
        repaint()
    }
}
