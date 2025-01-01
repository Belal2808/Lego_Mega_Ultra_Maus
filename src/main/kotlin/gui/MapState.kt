package de.fhkiel.rob.labyrinth.gui

import de.fhkiel.rob.legoosctester.Cell

class MapState {
    private val cells = mutableMapOf<Pair<Int, Int>, Cell>() // Map von Koordinaten zu Zellen

    fun addCell(x: Int, y: Int, cell: Cell) {
        cells[Pair(x, y)] = cell
    }

    fun getCells(): Map<Pair<Int, Int>, Cell> {
        return cells
    }
}
