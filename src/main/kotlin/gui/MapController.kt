package de.fhkiel.rob.legoosctester.gui

import MapCanvas
import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import java.awt.Color

class MapController(
    private val mapState: MapState,
    private val mapCanvas: MapCanvas
) {

    fun addSimulatedCell(
        x: Int,
        y: Int,
        color: Color? = null,
        north: CellBoarder? = null,
        east: CellBoarder? = null,
        south: CellBoarder? = null,
        west: CellBoarder? = null,
        isEntrance: Boolean? = null,
        isColorField: Boolean? = null,  // <-- Nullable!
        priority: Int? = null,
        isBlocked: Boolean? = null
    ) {
        val existingCell = mapState.getCell(x, y)
        println("Vorherige Zelle an Position ($x, $y): $existingCell")

        // 1) Falls es eine alte Zelle gibt, nimm deren Werte als Default,
        //    ansonsten Standardwerte.
        val oldCell = existingCell ?: Cell(
            north = CellBoarder.UNDISCOVERED,
            east  = CellBoarder.UNDISCOVERED,
            south = CellBoarder.UNDISCOVERED,
            west  = CellBoarder.UNDISCOVERED,
            color = Color.LIGHT_GRAY,
            isEntrance = false,
            isColorField = false,
            priority = 0,
            isBlocked = false
        )

        // 2) "Merger"-Logik: Nur wenn der neue Parameter != null, überschreib.
        val newCell = oldCell.copy(
            north       = north       ?: oldCell.north,
            east        = east        ?: oldCell.east,
            south       = south       ?: oldCell.south,
            west        = west        ?: oldCell.west,
            color       = color       ?: oldCell.color,
            isEntrance  = isEntrance  ?: oldCell.isEntrance,
            isColorField= isColorField ?: oldCell.isColorField, // <-- WICHTIG
            priority    = priority    ?: oldCell.priority,
            isBlocked   = isBlocked   ?: oldCell.isBlocked
        )

        println("Neue/aktualisierte Zelle an Position ($x, $y): $newCell")

        // In MapState und im Canvas ablegen
        mapState.addCell(x, y, newCell)
        mapCanvas.addCell(x, y, newCell)
        mapCanvas.repaint()

        println("Zelle aktualisiert/erstellt: Position=($x, $y), Eigenschaften=$newCell")
    }

}
