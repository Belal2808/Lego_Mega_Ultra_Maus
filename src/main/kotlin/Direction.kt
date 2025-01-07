package de.fhkiel.rob.legoosctester

enum class RoboterDirection(val value: Int) {
    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);
    companion object {

        fun fromValue(value: Int): RoboterDirection {
            return RoboterDirection.entries.find { it.value == value }!!
        }
    }
}

enum class EyesDirection(val value: Int){
    FRONT(0),RIGHT(1),BACK(2),LEFT(3);

    companion object {

        fun fromValue(value: Int): EyesDirection? {
            return entries.find { it.value == value }
        }
    }
}