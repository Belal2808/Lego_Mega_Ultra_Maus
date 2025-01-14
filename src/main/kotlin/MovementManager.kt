package de.fhkiel.rob.legoosctester

class MovementManager(
    robotController: RobotController
) {
    private val movementQueue: MutableList<() -> Unit> = mutableListOf()
    private var isProcessingMovement = false
    private var movementQueueListener: MovementQueueListener? = null

    init {
        robotController.addMovementCompleteListener { onMovementComplete() }
    }

    fun setMovementQueueListener(listener: MovementQueueListener) {
        this.movementQueueListener = listener
    }

    fun enqueueMovements(movements: List<() -> Unit>) {
        movementQueue.addAll(movements)
        if (!isProcessingMovement) {
            processNextMovement()
        }
    }

    private fun processNextMovement() {
        println("movent queu ist aber jetzt empty ${movementQueue}")
        if (movementQueue.isNotEmpty()) {
            isProcessingMovement = true
            val nextMovement = movementQueue.removeAt(0)
            nextMovement()
            println("next move gestertet")
        }else{
            isProcessingMovement = false
            movementQueueListener?.onQueueEmpty()
        }
    }

    private fun onMovementComplete() {
        isProcessingMovement = false
        processNextMovement()
    }
}
