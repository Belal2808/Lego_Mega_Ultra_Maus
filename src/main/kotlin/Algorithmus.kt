package de.fhkiel.rob.legoosctester

import de.fhkiel.rob.legoosctester.gui.MapCanvas
import java.awt.Color // Für Farbinformationen
import java.util.PriorityQueue // Für den Dijkstra-Algorithmus
import javax.swing.Timer // Für Zeitsteuerung


class Algorithmus(
    private val labyrinthState: LabyrinthStateService,
    private val mapCanvas: MapCanvas
) {
    private var movementTimer: Timer? = null
    private var currentPath: MutableList<Pair<Int, Int>> = mutableListOf()
    fun findPathDijkstra(start: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>> {
        // Überprüfen, ob Start und Ziel existieren
        if (labyrinthState.getCell(start.first,start.second) == null) {
            println("Startpunkt $start existiert nicht.")
            return emptyList()
        }
        if (labyrinthState.getCell(goal.first,goal.second) == null) {
            println("Zielpunkt $goal existiert nicht.")
            return emptyList()
        }

        // Überprüfen, ob Startpunkt Nachbarn hat
        if (labyrinthState.getNeighbors(start.first, start.second).isEmpty()) {
            println("Startpunkt $start hat keine erreichbaren Nachbarn.")
            return emptyList()
        }

        // Initialisierung der Datenstrukturen
        val distances = mutableMapOf<Pair<Int, Int>, Int>().withDefault { Int.MAX_VALUE }
        val previous = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { distances.getValue(it) })

        distances[start] = 0
        priorityQueue.add(start)

        println("Dijkstra-Algorithmus gestartet: Start=$start, Ziel=$goal")

        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()

            // Ziel erreicht
            if (current == goal) {
                println("Ziel $goal erreicht.")
                break
            }

            // Nachbarn verarbeiten
            for (neighbor in labyrinthState.getNeighbors(current.first, current.second)) {
                val tentativeDistance = distances.getValue(current) + 1
                if (tentativeDistance < distances.getValue(neighbor)) {
                    distances[neighbor] = tentativeDistance
                    previous[neighbor] = current
                    priorityQueue.add(neighbor)
                    println("Aktualisiert: $neighbor mit Distanz $tentativeDistance (Vorher: $current)")
                }
            }
        }

        // Pfad zurückverfolgen
        val path = mutableListOf<Pair<Int, Int>>()
        var step: Pair<Int, Int>? = goal
        while (step != null && step != start) {
            path.add(0, step)
            step = previous[step]
        }

        if (step == start) {
            path.add(0, start)
            println("Pfad erfolgreich berechnet: $path")
        } else {
            println("Ziel $goal ist von $start aus nicht erreichbar.")
            return emptyList()
        }

        return path
    }
    fun calculateAndMoveToNextTarget(labyrinthState: LabyrinthStateService) {
        val currentPosition = labyrinthState.getRobotPosition()
        println("Aktueller Startpunkt: $currentPosition")

        val targets = labyrinthState.getCells().filterValues { it.isColorField }.keys
        println("Aktuelle Farbziele: $targets")

        if (targets.isEmpty()) {
            println("Keine Farbzellen verfügbar. Zurück zum Eingang.")
            val entrancePosition = labyrinthState.getCells().entries.find { it.value.isEntrance }?.key

            if (entrancePosition != null) {
                val pathToEntrance = findPathDijkstra(currentPosition, entrancePosition)
                if (pathToEntrance.isNotEmpty()) {
                    println("Pfad zum Eingang: $pathToEntrance")
                    currentPath.clear()
                    currentPath.addAll(pathToEntrance)

                    movementTimer?.stop()
                    movementTimer = Timer(500) {
                        moveRobotOneStep(labyrinthState)
                    }
                    movementTimer?.start()
                } else {
                    println("Kein gültiger Pfad zum Eingang gefunden.")
                }
            } else {
                println("Eingang nicht definiert.")
            }
            return
        }

        val nextTarget = targets.minByOrNull { target ->
            val path = findPathDijkstra(currentPosition, target)
            if (path.isNotEmpty()) path.size else Int.MAX_VALUE
        }
        if (nextTarget == null) {
            println("Kein gültiger Pfad zu einer Farbzelle gefunden.")
            removeCyanPath(labyrinthState)
            return
        }

        if (nextTarget == currentPosition) {
            println("Nächstes Ziel == aktueller Standort: $currentPosition. Deaktiviere isColorField.")
            val cellHere = labyrinthState.getCell(currentPosition.first, currentPosition.second)
            if (cellHere != null && cellHere.isColorField) {
                cellHere.isColorField = false
            }
            return
        }

        val path = findPathDijkstra(currentPosition, nextTarget)
        if (path.isEmpty()) {
            println("Kein gültiger Pfad zum Ziel gefunden.")
            removeCyanPath(labyrinthState)
            return
        }

        println("Berechneter Pfad: $path")
        removeCyanPath(labyrinthState)

        // Markiere Pfad als CYAN
        for ((px, py) in path) {
            val oldCell = labyrinthState.getCell(px, py) ?: Cell()
            if (!oldCell.isBlocked && oldCell.color == Color.LIGHT_GRAY) {
                oldCell.color = Color.CYAN
            }
        }
        mapCanvas.repaint()
        println("Pfad erfolgreich markiert.")

        movementTimer?.stop()
        currentPath = path.toMutableList()

        movementTimer = Timer(500) {
            moveRobotOneStep(labyrinthState)
        }
        movementTimer?.start()
    }
    private fun moveRobotOneStep(labyrinthState: LabyrinthStateService) {
        if (currentPath.isEmpty()) {
            println("Pfad vollständig abgefahren. Stoppe Bewegung.")
            movementTimer?.stop()

            val pos = labyrinthState.getRobotPosition()
            val cell = labyrinthState.getCell(pos.first, pos.second)
            if (cell != null && cell.isColorField) {
                println("Farbziel erreicht bei $pos.")
                cell.color = Color.LIGHT_GRAY
                cell.isColorField = false

                // Überprüfen, ob weitere Ziele verfügbar sind
                val targets = labyrinthState.getCells().filterValues { it.isColorField }.keys
                if (targets.isEmpty()) {
                    println("Alle Farbziele abgefahren. Rückkehr zum Eingang.")
                    val entrance = labyrinthState.getCells().entries.find { it.value.isEntrance }?.key
                    if (entrance != null) {
                        val pathToEntrance = findPathDijkstra(pos, entrance)
                        if (pathToEntrance.isNotEmpty()) {
                            println("Pfad zum Eingang berechnet: $pathToEntrance")
                            currentPath.addAll(pathToEntrance)
                            movementTimer?.start()
                        } else {
                            println("Kein gültiger Pfad zum Eingang gefunden.")
                        }
                    } else {
                        println("Kein Eingang definiert.")
                    }
                } else {
                    // Nächstes Ziel ansteuern
                    calculateAndMoveToNextTarget(labyrinthState)
                }
            }
            return
        }

        // Bewegung zum nächsten Punkt
        val nextCoord = currentPath.removeAt(0)
        println("Bewege Roboter zu: $nextCoord")
        labyrinthState.setRobotPosition(nextCoord.first, nextCoord.second)
    }

    private fun removeCyanPath(labyrinthState: LabyrinthStateService) {
        for ((coords, cell) in labyrinthState.getCells()) {
            if (cell.color == Color.CYAN) {
                cell.color = Color.LIGHT_GRAY
            }
        }
    }
    }

