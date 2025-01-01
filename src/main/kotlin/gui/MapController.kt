package de.fhkiel.rob.legoosctester.gui

import MapCanvas
import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import java.awt.Color

class MapController(private val mapState: MapState, private val mapCanvas: MapCanvas) {

    fun addSimulatedCell(
        x: Int,
        y: Int,
        color: Color,
        north: CellBoarder = CellBoarder.DISCOVERED,
        east: CellBoarder = CellBoarder.DISCOVERED,
        south: CellBoarder = CellBoarder.DISCOVERED,
        west: CellBoarder = CellBoarder.DISCOVERED
    ) {
        val cell = Cell(north, east, south, west, color)
        mapState.addCell(x, y, cell)
        mapCanvas.addCell(x, y, cell)
    }
}