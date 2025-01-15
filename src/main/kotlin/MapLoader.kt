package de.fhkiel.rob.legoosctester

import de.fhkiel.rob.legoosctester.gui.MapCanvas
import org.json.JSONArray
import org.json.JSONObject
import org.koin.mp.KoinPlatform
import java.awt.Color
import java.io.File

class MapLoader {
    companion object {
        fun exportMap(labyrinthState: LabyrinthStateService, filename: String) {
            val file = File(filename)
            val jsonArray = JSONArray()

            labyrinthState.getCells().forEach { (coords, cell) ->
                val cellJson = JSONObject()
                cellJson.put("x", coords.first)
                cellJson.put("y", coords.second)

                // Borders
                val bordersObj = JSONObject()
                for ((dir, border) in cell.borders) {
                    bordersObj.put(dir.name, border.name)
                }
                cellJson.put("borders", bordersObj)

                cellJson.put("color", String.format("#%06X", 0xFFFFFF and cell.color.rgb))
                cellJson.put("isEntrance", cell.isEntrance)
                cellJson.put("isColorField", cell.isColorField)
                cellJson.put("priority", cell.priority)
                cellJson.put("isBlocked", cell.isBlocked)
                jsonArray.put(cellJson)
            }

            file.writeText(jsonArray.toString(4))
        }

        fun loadMap(labyrinthState: LabyrinthStateService, filename: String) {
            val file = File(filename)
            val jsonArray = JSONArray(file.readText())

            val cellsMap = labyrinthState.getCells() as MutableMap<Pair<Int, Int>, Cell>
            cellsMap.clear()

            for (i in 0 until jsonArray.length()) {
                val cellJson = jsonArray.getJSONObject(i)
                val x = cellJson.getInt("x")
                val y = cellJson.getInt("y")

                val bordersJson = cellJson.getJSONObject("borders")
                val bordersMap = mutableMapOf<RoboterDirection, CellBoarder>()
                for (dirName in bordersJson.keySet()) {
                    val borderName = bordersJson.getString(dirName)
                    val borderEnum = CellBoarder.valueOf(borderName)
                    val dirEnum = RoboterDirection.valueOf(dirName)
                    bordersMap[dirEnum] = borderEnum
                }

                val colorStr = cellJson.getString("color")
                val colorParsed = Color.decode(colorStr)

                val isEntrance = cellJson.getBoolean("isEntrance")
                val isColorField = cellJson.getBoolean("isColorField")
                val priority = cellJson.getInt("priority")
                val isBlocked = cellJson.getBoolean("isBlocked")

                val newCell = Cell(
                    borders = bordersMap,
                    color = colorParsed,
                    isEntrance = isEntrance,
                    isColorField = isColorField,
                    priority = priority,
                    isBlocked = isBlocked
                )
               labyrinthState.addCell(x, y, newCell)
            }

        }
    }
}
