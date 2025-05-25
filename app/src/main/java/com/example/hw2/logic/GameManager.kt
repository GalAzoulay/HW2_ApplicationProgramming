package com.example.hw2.logic
import com.example.hw2.utilities.Constants


class GameManager {

    private val numRows = Constants.GameLogic.numRows
    private val numCols = Constants.GameLogic.numCols
    private val Empty = Constants.Entity.EMPTY
    private val Seal = Constants.Entity.SEAL
    private val Fish = Constants.Entity.FISH
    private var spawnCounter = 0

    val roadMatrix = Array(numRows) { IntArray(numCols) { 0 } }
    var carPosition = numCols/2
    var lives = Constants.GameLogic.fullLives
    var distanceTraveled = 0


    fun moveCar(direction: Int) {
        carPosition += direction
        if (carPosition < 0) carPosition = 0
        if (carPosition > numCols - 1) carPosition = numCols - 1
    }


    fun updateObstacles() {
        for (i in numRows - 1 downTo 1) {
            for (j in 0 until numCols) {
                roadMatrix[i][j] = roadMatrix[i - 1][j]
            }
        }

        for (j in 0 until numCols) {
            roadMatrix[0][j] = Empty
        }

        spawnCounter++
        if (spawnCounter >= 2) {
            val lane = (0 until numCols).random()
            val newItemType = if ((0..1).random() == 0) Seal else Fish
            roadMatrix[0][lane] = newItemType
            spawnCounter = 0
        }

        distanceTraveled++
    }


    fun checkCollision(): Int {
        return roadMatrix[numRows - 1][carPosition]
    }


    fun handleCollision(type: Int): Boolean {
        when(type){
            Seal -> {
                lives--
            }
        }
        roadMatrix[numRows - 1][carPosition] = Empty
        return lives <= 0
    }
}