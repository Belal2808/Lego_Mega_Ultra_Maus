package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.legoosctester.*
import java.awt.Color

class MapController(
    private val labyrinthState: LabyrinthStateService,
    private val mapCanvas: MapCanvas
) {

    /**
     * Fügt eine Zelle an (x,y) hinzu oder aktualisiert sie.
     * @param x,y Koordinaten
     * @param color Farbe der Zelle
     * @parm bordersMap Neue or zu ergänzende Border-Map (NORTH->WALL, etc.)
     * @param isEntrance Ob die Zelle ein Eingang ist
     * @param isColorField Ob die Zelle Farbfeld sein soll
     * @param priority Priorität der Zelle
     * @param isBlocked Ob die Zelle blockiert ist
     */
    fun addSimulatedCell(
        x: Int,
        y: Int,
        color: Color? = null,
        bordersMap: MutableMap<RoboterDirection, CellBoarder>? = null,
        isEntrance: Boolean? = null,
        isColorField: Boolean? = null,
        priority: Int? = null,
        isBlocked: Boolean? = null
    ) {
        val existingCell = labyrinthState.getCell(x, y)
        println("Vorherige Zelle an Position ($x, $y): $existingCell")

        // Wenn keine alte Zelle existiert, Standardwerte anlegen
        val oldCell = existingCell ?: Cell(
            borders = mutableMapOf(
                RoboterDirection.NORTH to CellBoarder.UNDISCOVERED,
                RoboterDirection.EAST  to CellBoarder.UNDISCOVERED,
                RoboterDirection.SOUTH to CellBoarder.UNDISCOVERED,
                RoboterDirection.WEST  to CellBoarder.UNDISCOVERED
            ),
            color = Color.LIGHT_GRAY,
            isEntrance = false,
            isColorField = false,
            priority = 0,
            isBlocked = false
        )

        // Aktuelle borders als Kopie
        val newBorders = oldCell.borders.toMutableMap()

        // Wenn bordersMap != null, dann übernimm (bzw. überschreibe) daraus die Werte
        if (bordersMap != null) {
            for ((dir, border) in bordersMap) {
                newBorders[dir] = border
            }
        }

        // Baue ein neues Cell-Objekt
        val newCell = oldCell.copy(
            borders = newBorders,
            color = color ?: oldCell.color,
            isEntrance = isEntrance ?: oldCell.isEntrance,
            isColorField = isColorField ?: oldCell.isColorField,
            priority = priority ?: oldCell.priority,
            isBlocked = isBlocked ?: oldCell.isBlocked
        )

        println("Neue/aktualisierte Zelle an Position ($x, $y): $newCell")

        println("Zelle aktualisiert/erstellt: Position=($x, $y), Eigenschaften=$newCell")
    }
}
