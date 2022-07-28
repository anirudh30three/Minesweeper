package minesweeper

import java.util.*
import kotlin.random.Random

enum class Symbol(val symbol: String) {
    Mine("X"),
    SCell("."),
    Mark("*"),
    FCell("/")
}


class Minefield(mines: Int) {
    private val minefield = MutableList(9) { MutableList(9) { Symbol.SCell.symbol } }
    private val totalMines = mines
    private val displayedMinefield = MutableList(9) { MutableList(9) { Symbol.SCell.symbol } }

    init {

        var totalMines = mines
        while (totalMines > 0) {
            var randomIndex1 = Random.nextInt(0, minefield.size)
            if (Symbol.SCell.symbol !in minefield[randomIndex1]) {
                while (Symbol.SCell.symbol !in minefield[randomIndex1]) {
                    randomIndex1 = Random.nextInt(0, minefield.size)
                }
            }
            var randomIndex2 = Random.nextInt(0, minefield[randomIndex1].size)
            if (minefield[randomIndex1][randomIndex2] == Symbol.SCell.symbol) {
                minefield[randomIndex1][randomIndex2] = Symbol.Mine.symbol
            } else {
                while (minefield[randomIndex1][randomIndex2] != Symbol.SCell.symbol) {
                    randomIndex2 = Random.nextInt(0, minefield[randomIndex1].size)
                }
                minefield[randomIndex1][randomIndex2] = Symbol.Mine.symbol
            }
            totalMines--
        }
        assignNumbers()
    }

    fun printField() {
        println(" |123456789|")
        println("-|---------|")
        for (i in displayedMinefield.indices) {
            println("${i + 1}|${displayedMinefield[i].joinToString("")}|")
        }
        println("-|---------|")
    }

    private fun assignNumbers() {
        for (i in minefield.indices) {
            for (j in minefield[i].indices) {
                if (minefield[i][j] == Symbol.SCell.symbol) {
                    var surroundingMines = 0
                    for (k in determineRange(i, minefield.lastIndex)) {
                        for (m in determineRange(j, minefield[i].lastIndex)) {
                            if (minefield[k][m] == Symbol.Mine.symbol) surroundingMines++
                        }
                    }
                    minefield[i][j] = if (surroundingMines > 0) surroundingMines.toString() else Symbol.SCell.symbol
                }
            }
        }
    }

    private var markedMines = 0
    private var markedCells = 0
    private var markedSCells = 0

    fun playGame() {
        while (markedMines < totalMines || markedCells > markedMines) {
            if (minefield.size * minefield[0].size - totalMines == markedSCells) break
            println("Set/unset mine marks or claim a cell as free: ")
            val (num1, num2, action) = readln().split(" ")
            val x = num1.toInt()
            val y = num2.toInt()
            when (action) {
                "free" -> free(y - 1, x - 1)
                "mine" -> when (minefield[y - 1][x - 1]) {
                    Symbol.Mine.symbol -> {
                        if (displayedMinefield[y - 1][x - 1] == Symbol.Mark.symbol) {
                            displayedMinefield[y - 1][x - 1] = Symbol.SCell.symbol
                            markedMines--
                            markedCells--
                        } else {
                            displayedMinefield[y - 1][x - 1] = Symbol.Mark.symbol
                            markedCells++
                            markedMines++
                        }
                        printField()
                    }
                    Symbol.SCell.symbol -> {
                        if (displayedMinefield[y - 1][x - 1] == Symbol.Mark.symbol) {
                            displayedMinefield[y - 1][x - 1] = Symbol.SCell.symbol
                            markedCells--
                        } else {
                            displayedMinefield[y - 1][x - 1] = Symbol.Mark.symbol
                            markedCells++
                            markedSCells++
                        }
                        printField()
                    }
                    else -> {
                        if (displayedMinefield[y - 1][x - 1] == Symbol.Mark.symbol) {
                            displayedMinefield[y - 1][x - 1] = Symbol.SCell.symbol
                            markedCells--
                        } else {
                            displayedMinefield[y - 1][x - 1] = Symbol.Mark.symbol
                            markedCells++
                            markedSCells++
                        }
                        printField()
                    }
                }
            }
        }
        println("Congratulations! You found all the mines!")
    }


    private fun free(row: Int, column: Int) {
        if (minefield[row][column] == Symbol.Mine.symbol) {
            for (i in minefield.indices) {
                for (j in minefield[i].indices) {
                    if (minefield[i][j] == Symbol.Mine.symbol) displayedMinefield[i][j] = Symbol.Mine.symbol
                }
            }
            printField()
            throw Exception("You stepped on a mine and failed!")
        } else {
            explore(row, column)
            printField()
        }
    }

    private fun explore(row: Int, column: Int) {
        val length1 = minefield.size
        val length2 = minefield[0].size
        if (displayedMinefield[row][column] == "/") return

        val queue = LinkedList<List<Int>>()
        queue.add(listOf(row, column))
        while (!queue.isEmpty()) {
            val (i, j) = queue.remove()
            if (i < 0 || i >= length1 || j < 0 || j >= length2 || displayedMinefield[i][j] == Symbol.FCell.symbol) {
                continue
            } else if (checkIfMinesAround(i, j)) {
                displayedMinefield[i][j] = minefield[i][j]
                continue
            } else {
                displayedMinefield[i][j] = Symbol.FCell.symbol
                queue.add(listOf(i + 1, j))
                queue.add(listOf(i - 1, j))
                queue.add(listOf(i, j + 1))
                queue.add(listOf(i, j - 1))
                queue.add(listOf(i - 1, j - 1))
                queue.add(listOf(i - 1, j + 1))
                queue.add(listOf(i + 1, j - 1))
                queue.add(listOf(i + 1, j + 1))
            }
        }
    }


    private fun checkIfMinesAround(row: Int, column: Int): Boolean {
        var minesAround = false
        for (rowNumber in determineRange(row, minefield.lastIndex)) {
            for (columnNumber in determineRange(column, minefield[rowNumber].lastIndex)) {
                if (minefield[rowNumber][columnNumber] == Symbol.Mine.symbol) {
                    minesAround = true
                    break
                }
            }
        }
        return minesAround
    }


    private fun determineRange(num1: Int, num2: Int) =
        when (num1) {
            0 -> 0..1
            num2 -> num1 - 1..num2
            else -> num1 - 1..num1 + 1
        }
}


fun main() {
    println("How many mines do you want on the field?")
    val mines = readln().toInt()
    val minefield = Minefield(mines)
    minefield.printField()
    try {
        minefield.playGame()
    } catch (e: Exception) {
        println(e.message)
    }
}
