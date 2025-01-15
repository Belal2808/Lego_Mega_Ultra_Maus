package de.fhkiel.rob.legoosctester

import java.util.Timer
import java.util.TimerTask

class MovementManager(
    robotController: RobotController
) {
    private val movementQueue: MutableList<() -> Unit> = mutableListOf()
    private var isProcessingMovement = false
    private var movementQueueListener: MovementQueueListener? = null
    private var lastMovement: (() -> Unit)? = null
    private var retryTimer: Timer? = null

    init {
        robotController.addMovementCompleteListener { onMovementComplete() }
    }

    fun setMovementQueueListener(listener: MovementQueueListener) {
        this.movementQueueListener = listener
    }

    fun removeMovementQueueListener(listener: MovementQueueListener){
        movementQueueListener = null
    }

    fun enqueueMovements(movements: List<() -> Unit>) {
        movementQueue.addAll(movements)
        if (!isProcessingMovement) {
            processNextMovement()
        }
    }

    private fun processNextMovement() {
        if (movementQueue.isNotEmpty()) {
            isProcessingMovement = true
            val nextMovement = movementQueue.removeAt(0)
            lastMovement = nextMovement
            println("schicke den näcsten befehl ab")
            nextMovement()
            startRetryTimer(nextMovement)
        }else{
            isProcessingMovement = false
            println("führe jetzt den nächsten schritt automatisch aus")
            movementQueueListener?.onQueueEmpty()
        }
    }

    private fun startRetryTimer(movement: () -> Unit) {
        retryTimer?.cancel() // Vorherigen Timer abbrechen
        retryTimer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    println("Keine Antwort erhalten. Sende letzten Befehl erneut...")
                    movement() // Wiederhole den letzten Befehl
                    startRetryTimer(movement) // Timer erneut starten
                }
            }, 5000) // Timeout von 5 Sekunden
        }
    }

    private fun stopRetryTimer() {
        retryTimer?.cancel()
        retryTimer = null
    }

    private fun onMovementComplete() {
        stopRetryTimer()
        isProcessingMovement = false
        processNextMovement()
    }
}
