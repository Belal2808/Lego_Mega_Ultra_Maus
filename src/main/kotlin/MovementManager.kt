package de.fhkiel.rob.legoosctester

class MovementManager(
    robotController: RobotController
) {
    private val movementQueue: MutableList<() -> Unit> = mutableListOf()
    private var isProcessingMovement = false

    init {
        robotController.addMovementCompleteListener { onMovementComplete() }
    }

    fun enqueueMovements(movements: List<() -> Unit>) {
        movementQueue.addAll(movements)
        println("Enqueued movements: $movements")
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
        print(movementQueue.size)
        isProcessingMovement = false
        processNextMovement()
    }

}
