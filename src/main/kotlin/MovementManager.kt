package de.fhkiel.rob.legoosctester

class MovementManager(
    private val robotActionHandler: RobotController
) {
    private val movementQueue: MutableList<() -> Unit> = mutableListOf()
    private var isProcessingMovement = false

    fun enqueueMovement(movement: () -> Unit) {
        movementQueue.add(movement)
        if (!isProcessingMovement) {
            processNextMovement()
        }

    }

    private fun processNextMovement() {
        if (movementQueue.isNotEmpty()) {
            isProcessingMovement = true
            val nextMovement = movementQueue.removeAt(0)
            nextMovement()
        }
    }

    private fun onMovementComplete() {
        isProcessingMovement = false
        processNextMovement()
    }
}
