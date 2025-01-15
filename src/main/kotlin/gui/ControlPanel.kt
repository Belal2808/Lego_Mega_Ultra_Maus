package de.fhkiel.rob.legoosctester.gui

import javax.swing.*
import java.awt.*
import java.io.IOException
import de.fhkiel.rob.legoosctester.*

class ControlPanel : JPanel() {

    private val cellInfo = JTextArea(10, 30)
    private val saveButton = JButton("Speichern")
    private var currentCellPosition: Pair<Int, Int>? = null
    private var currentCell: Cell? = null
    lateinit var labyrinthExplorer: LabyrinthExplorer
    lateinit var automaticExplorer: AutomaticExplorer
    lateinit var labyrinthState: LabyrinthStateService
    lateinit var algorithmus: Algorithmus

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = Color(240, 240, 240)
        border = BorderFactory.createTitledBorder("Steuerung")

        // Zellinformationsbereich
        add(JLabel("Zellinformationen"))
        cellInfo.isEditable = true
        add(JScrollPane(cellInfo))

        // Speichern-Button für Zellinformationen
        saveButton.addActionListener {
            saveCellInfo()
        }
        add(saveButton)

        // Steuerungsbereich
        val buttonPanel = JPanel(GridLayout(5, 2, 10, 10))
        buttonPanel.background = Color(240, 240, 240)
        add(buttonPanel)

        // Buttons erstellen und Aktionen zuweisen
        buttonPanel.addButton("↑ Nach Norden") { labyrinthExplorer.driveNorth() }
        buttonPanel.addButton("↓ Nach Süden") { labyrinthExplorer.driveSouth() }
        buttonPanel.addButton("← Nach Westen") { labyrinthExplorer.driveWest() }
        buttonPanel.addButton("→ Nach Osten") { labyrinthExplorer.driveEast() }
        buttonPanel.addButton("🔍 Zelle scannen") { labyrinthExplorer.scanCell() }
        buttonPanel.addButton("🚶 Automatisch erkunden") { automaticExplorer.exploreAutomatic() }
        buttonPanel.addButton("💾 Karte exportieren") {
            try {
                MapLoader.exportMap(labyrinthState, "labyrinth.json")
                println("Karte exportiert in labyrinth.json")
            } catch (ex: IOException) {
                println("Fehler beim Export: ${ex.message}")
            }
        }
        buttonPanel.addButton("📂 Karte laden") {
            MapLoader.loadMap(labyrinthState, "labyrinth.json")
            println("Karte aus labyrinth.json geladen")
        }
        buttonPanel.addButton("🎯 Nächstes Ziel finden") {
            algorithmus.calculateAndMoveToNextTarget(labyrinthState)
        }
        buttonPanel.addButton("🔄 Zurücksetzen") {
            labyrinthExplorer.resetRoboter()
            println("Roboter und Labyrinth zurückgesetzt.")
        }
    }

    fun updateCellInfo(position: Pair<Int, Int>, cell: Cell?) {
        currentCellPosition = position
        currentCell = cell
        cellInfo.text = if (cell != null) {
            """
            Position: $position
            Farbe: ${colorToName(cell.color)}
            Blockiert: ${cell.isBlocked}
            Eingang: ${cell.isEntrance}
            Priorität: ${cell.priority}
            Wände: ${cell.borders.map { "${it.key}: ${it.value}" }.joinToString(", ")}
            """.trimIndent()
        } else {
            "Keine Zelle an dieser Position"
        }
    }

    private fun saveCellInfo() {
        if (currentCellPosition != null && currentCell != null) {
            val updatedCell = currentCell!!
            val lines = cellInfo.text.lines()
            try {
                for (line in lines) {
                    when {
                        line.startsWith("Farbe:") -> {
                            val colorName = line.split(":")[1].trim()
                            updatedCell.color = parseColor(colorName)
                        }
                        line.startsWith("Blockiert:") -> {
                            updatedCell.isBlocked = line.split(":")[1].trim().toBoolean()
                        }
                        line.startsWith("Eingang:") -> {
                            updatedCell.isEntrance = line.split(":")[1].trim().toBoolean()
                        }
                        line.startsWith("Priorität:") -> {
                            updatedCell.priority = line.split(":")[1].trim().toInt()
                        }
                        line.startsWith("Wände:") -> {
                            val borders = line.split(":")[1].trim().split(",").mapNotNull {
                                val parts = it.split(":")
                                if (parts.size == 2) {
                                    RoboterDirection.valueOf(parts[0].trim()) to CellBoarder.valueOf(parts[1].trim())
                                } else null
                            }.toMap()
                            updatedCell.borders.clear()
                            updatedCell.borders.putAll(borders)
                        }
                    }
                }
                labyrinthState.updateCell(currentCellPosition!!.first, currentCellPosition!!.second, updatedCell)
            } catch (e: Exception) {
                println("Fehler beim Aktualisieren der Zelle: ${e.message}")
            }
        }
    }

    private fun parseColor(colorName: String): Color {
        return when (colorName.uppercase()) {
            "LIGHTGRAY" -> Color.LIGHT_GRAY
            "GRAY" -> Color.GRAY
            "DARKGRAY" -> Color.DARK_GRAY
            "RED" -> Color.RED
            "GREEN" -> Color.GREEN
            "BLUE" -> Color.BLUE
            "CYAN" -> Color.CYAN
            "MAGENTA" -> Color.MAGENTA
            "YELLOW" -> Color.YELLOW
            "BLACK" -> Color.BLACK
            "WHITE" -> Color.WHITE
            else -> throw IllegalArgumentException("Unbekannte Farbe: $colorName")
        }
    }

    private fun colorToName(color: Color): String {
        return when (color) {
            Color.LIGHT_GRAY -> "LIGHTGRAY"
            Color.RED -> "RED"
            Color.GREEN -> "GREEN"
            Color.BLUE -> "BLUE"
            Color.CYAN -> "CYAN"
            else -> "Unbekannt (${color.red}, ${color.green}, ${color.blue})"
        }
    }


    private fun JPanel.addButton(label: String, action: () -> Unit) {
        val button = JButton(label).apply {
            background = Color(200, 230, 255)
            foreground = Color.BLACK
            isFocusable = false
            addActionListener { action() }
        }
        add(button)
    }
}
