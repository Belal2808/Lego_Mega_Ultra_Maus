import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class MapCanvas : JPanel() {
    private val mapState = MapState() // Zustand der Karte

    fun addCell(x: Int, y: Int, cell: Cell) {
        mapState.addCell(x, y, cell)
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val cellSize = 50
        val wallThickness = 5

        for ((position, cell) in mapState.getCells()) {
            val (x, y) = position
            val px = x * cellSize
            val py = y * cellSize

            // Zeichne die Zellenfarbe
            g.color = cell.color
            g.fillRect(px, py, cellSize, cellSize)

            // Zeichne die Wände in Schwarz
            g.color = Color.BLACK
            if (cell.north == CellBoarder.WALL) g.fillRect(px, py, cellSize, wallThickness)
            if (cell.south == CellBoarder.WALL) g.fillRect(px, py + cellSize - wallThickness, cellSize, wallThickness)
            if (cell.west == CellBoarder.WALL) g.fillRect(px, py, wallThickness, cellSize)
            if (cell.east == CellBoarder.WALL) g.fillRect(px + cellSize - wallThickness, py, wallThickness, cellSize)
        }
    }

}