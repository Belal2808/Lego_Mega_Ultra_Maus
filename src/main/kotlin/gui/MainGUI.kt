package de.fhkiel.rob.legoosctester.gui

import MapCanvas
import de.fhkiel.rob.labyrinth.gui.MapState
import de.fhkiel.rob.legoosctester.Cell
import de.fhkiel.rob.legoosctester.CellBoarder
import de.fhkiel.rob.legoosctester.Direction
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame

class MainGUI : JFrame() {
    private var x = 0
    private var y = 0
    private var currentColor = Color.LIGHT_GRAY
    private var currentDirection = Direction.NORTH

    init {
        title = "Labyrinth Map"
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE

        val mapState = MapState()
        val mapCanvas = MapCanvas()
        val mapController = MapController(mapState, mapCanvas)

        add(mapCanvas)

        addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_W -> y-- // Bewegung nach oben
                    KeyEvent.VK_S -> y++ // Bewegung nach unten
                    KeyEvent.VK_A -> x-- // Bewegung nach links
                    KeyEvent.VK_D -> x++ // Bewegung nach rechts

                    KeyEvent.VK_ENTER -> {
                        // Prüfe, ob die Zelle existiert, und erstelle sie, falls nicht vorhanden
                        if (!mapState.getCells().containsKey(Pair(x, y))) {
                            // Erstelle eine neue Zelle mit allen Wänden auf UNDISCOVERED und der aktuellen Farbe
                            val newCell = Cell(
                                north = CellBoarder.UNDISCOVERED,
                                east = CellBoarder.UNDISCOVERED,
                                south = CellBoarder.UNDISCOVERED,
                                west = CellBoarder.UNDISCOVERED,
                                color = currentColor
                            )
                            mapController.addSimulatedCell(x, y, newCell.color, newCell.north, newCell.east, newCell.south, newCell.west)
                        }
                    }

                    KeyEvent.VK_SPACE -> {
                        // Hole die bestehende Zelle oder erstelle eine neue mit Standardwerten
                        val cell = mapState.getCells()[Pair(x, y)] ?: Cell(
                            north = CellBoarder.UNDISCOVERED,
                            east = CellBoarder.UNDISCOVERED,
                            south = CellBoarder.UNDISCOVERED,
                            west = CellBoarder.UNDISCOVERED,
                            color = currentColor // Verwende die aktuelle Farbe
                        )

                        // Aktualisiere nur die Wand basierend auf der aktuellen Richtung
                        val updatedCell = when (currentDirection) {
                            Direction.NORTH -> cell.copy(north = CellBoarder.WALL)
                            Direction.SOUTH -> cell.copy(south = CellBoarder.WALL)
                            Direction.WEST -> cell.copy(west = CellBoarder.WALL)
                            Direction.EAST -> cell.copy(east = CellBoarder.WALL)
                        }

                        // Füge die aktualisierte Zelle hinzu
                        mapController.addSimulatedCell(
                            x, y, updatedCell.color, updatedCell.north, updatedCell.east, updatedCell.south, updatedCell.west
                        )
                    }

                    KeyEvent.VK_R -> {
                        currentColor = Color.RED // Rot
                        println("Farbe geändert: Rot")
                    }
                    KeyEvent.VK_G -> {
                        currentColor = Color.GREEN // Grün
                        println("Farbe geändert: Grün")
                    }
                    KeyEvent.VK_B -> {
                        currentColor = Color.BLUE // Blau
                        println("Farbe geändert: Blau")
                    }
                    KeyEvent.VK_N -> {
                        currentColor = Color.LIGHT_GRAY // Grau
                        println("Farbe geändert: Grau")
                    }

                    KeyEvent.VK_1 -> currentDirection = Direction.NORTH // Richtung Norden
                    KeyEvent.VK_2 -> currentDirection = Direction.EAST // Richtung Osten
                    KeyEvent.VK_3 -> currentDirection = Direction.SOUTH // Richtung Süden
                    KeyEvent.VK_4 -> currentDirection = Direction.WEST // Richtung Westen
                }

                println("Position: ($x, $y), Farbe: $currentColor, Richtung: $currentDirection")
            }

            override fun keyReleased(e: KeyEvent) {}
            override fun keyTyped(e: KeyEvent) {}
        })

        isVisible = true
    }
}
