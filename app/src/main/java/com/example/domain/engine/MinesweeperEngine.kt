package com.example.domain.engine

import com.example.domain.model.Cell
import java.util.ArrayDeque
import kotlin.random.Random

sealed class MoveResult {
    data class Normal(val cellsRevealed: Int, val isWin: Boolean) : MoveResult()
    data class Detonated(val hitRow: Int, val hitCol: Int) : MoveResult()
    data object NoOp : MoveResult()
}

object MinesweeperEngine {

    fun createEmptyBoard(rows: Int, cols: Int): List<List<Cell>> {
        return List(rows) { r ->
            List(cols) { c ->
                Cell(row = r, col = c)
            }
        }
    }

    /**
     * Places mines ensuring the first clicked cell (and preferably its neighbors) are mine-free.
     */
    fun populateMines(
        board: List<List<Cell>>,
        rows: Int,
        cols: Int,
        minesCount: Int,
        safeRow: Int,
        safeCol: Int
    ) {
        val totalCells = rows * cols
        val safeSet = mutableSetOf<Pair<Int, Int>>()
        safeSet.add(safeRow to safeCol)

        // If mines count allows, also spare 3x3 surrounding zone for a guaranteed opening
        if (totalCells - 9 >= minesCount) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = safeRow + dr
                    val nc = safeCol + dc
                    if (nr in 0 until rows && nc in 0 until cols) {
                        safeSet.add(nr to nc)
                    }
                }
            }
        }

        val availablePositions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!safeSet.contains(r to c)) {
                    availablePositions.add(r to c)
                }
            }
        }

        // Shuffle and take minesCount
        availablePositions.shuffle(Random(System.currentTimeMillis()))
        val targetMines = minesCount.coerceAtMost(availablePositions.size)
        for (i in 0 until targetMines) {
            val (r, c) = availablePositions[i]
            board[r][c].isMine = true
        }

        // Calculate adjacent mine counts
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (!board[r][c].isMine) {
                    var count = 0
                    for (dr in -1..1) {
                        for (dc in -1..1) {
                            if (dr == 0 && dc == 0) continue
                            val nr = r + dr
                            val nc = c + dc
                            if (nr in 0 until rows && nc in 0 until cols && board[nr][nc].isMine) {
                                count++
                            }
                        }
                    }
                    board[r][c].adjacentMines = count
                }
            }
        }
    }

    /**
     * Reveals a cell. If mine, triggers game over. If 0 adjacent mines, cascades.
     */
    fun reveal(
        board: List<List<Cell>>,
        rows: Int,
        cols: Int,
        totalMines: Int,
        row: Int,
        col: Int
    ): MoveResult {
        val cell = board[row][col]
        if (cell.isRevealed || cell.isFlagged) {
            return MoveResult.NoOp
        }

        if (cell.isMine) {
            cell.isExploded = true
            cell.isRevealed = true
            revealAllMines(board, rows, cols)
            return MoveResult.Detonated(row, col)
        }

        // Cascade reveal via BFS
        var count = 0
        val queue = ArrayDeque<Pair<Int, Int>>()
        cell.isRevealed = true
        count++
        if (cell.adjacentMines == 0) {
            queue.add(row to col)
        }

        while (queue.isNotEmpty()) {
            val (currR, currC) = queue.removeFirst()
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = currR + dr
                    val nc = currC + dc
                    if (nr in 0 until rows && nc in 0 until cols) {
                        val neighbor = board[nr][nc]
                        if (!neighbor.isRevealed && !neighbor.isFlagged && !neighbor.isMine) {
                            neighbor.isRevealed = true
                            count++
                            if (neighbor.adjacentMines == 0) {
                                queue.add(nr to nc)
                            }
                        }
                    }
                }
            }
        }

        val isWin = checkWin(board, rows, cols, totalMines)
        if (isWin) {
            flagRemainingMines(board, rows, cols)
        }

        return MoveResult.Normal(count, isWin)
    }

    /**
     * Toggle flag on an unopened cell.
     */
    fun toggleFlag(board: List<List<Cell>>, row: Int, col: Int): Boolean {
        val cell = board[row][col]
        if (cell.isRevealed) return false
        cell.isFlagged = !cell.isFlagged
        return true
    }

    /**
     * Chording pro move: when tapping a revealed number cell with matching adjacent flags.
     */
    fun chord(
        board: List<List<Cell>>,
        rows: Int,
        cols: Int,
        totalMines: Int,
        row: Int,
        col: Int
    ): MoveResult {
        val cell = board[row][col]
        if (!cell.isRevealed || cell.adjacentMines <= 0) return MoveResult.NoOp

        // Count flagged neighbors
        var flagCount = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until rows && nc in 0 until cols && board[nr][nc].isFlagged) {
                    flagCount++
                }
            }
        }

        if (flagCount != cell.adjacentMines) return MoveResult.NoOp

        // Reveal all non-flagged unopened neighbors
        var detonated = false
        var hitR = -1
        var hitC = -1
        var revealedAny = 0

        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until rows && nc in 0 until cols) {
                    val neighbor = board[nr][nc]
                    if (!neighbor.isRevealed && !neighbor.isFlagged) {
                        if (neighbor.isMine) {
                            neighbor.isExploded = true
                            neighbor.isRevealed = true
                            detonated = true
                            hitR = nr
                            hitC = nc
                        } else {
                            val res = reveal(board, rows, cols, totalMines, nr, nc)
                            if (res is MoveResult.Normal) {
                                revealedAny += res.cellsRevealed
                            }
                        }
                    }
                }
            }
        }

        if (detonated) {
            revealAllMines(board, rows, cols)
            return MoveResult.Detonated(hitR, hitC)
        }

        val isWin = checkWin(board, rows, cols, totalMines)
        if (isWin) {
            flagRemainingMines(board, rows, cols)
        }

        return MoveResult.Normal(revealedAny, isWin)
    }

    private fun checkWin(board: List<List<Cell>>, rows: Int, cols: Int, totalMines: Int): Boolean {
        var revealedSafeCells = 0
        val targetSafeCells = (rows * cols) - totalMines
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = board[r][c]
                if (cell.isRevealed && !cell.isMine) {
                    revealedSafeCells++
                }
            }
        }
        return revealedSafeCells >= targetSafeCells
    }

    private fun revealAllMines(board: List<List<Cell>>, rows: Int, cols: Int) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = board[r][c]
                if (cell.isMine) {
                    cell.isRevealed = true
                } else if (cell.isFlagged) {
                    cell.isFalseFlag = true
                }
            }
        }
    }

    private fun flagRemainingMines(board: List<List<Cell>>, rows: Int, cols: Int) {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cell = board[r][c]
                if (cell.isMine) {
                    cell.isFlagged = true
                }
            }
        }
    }

    fun countFlags(board: List<List<Cell>>): Int {
        var count = 0
        for (row in board) {
            for (cell in row) {
                if (cell.isFlagged) count++
            }
        }
        return count
    }

    /**
     * Compact string serialization:
     * Format per cell: isMine(1/0) + isRevealed(1/0) + isFlagged(1/0) + adjacentMines(0-8) + isExploded(1/0)
     * Cells separated by comma.
     */
    fun serialize(board: List<List<Cell>>): String {
        val sb = StringBuilder()
        for (r in board.indices) {
            for (c in board[r].indices) {
                val cell = board[r][c]
                sb.append(if (cell.isMine) '1' else '0')
                sb.append(if (cell.isRevealed) '1' else '0')
                sb.append(if (cell.isFlagged) '1' else '0')
                sb.append(cell.adjacentMines.toString())
                sb.append(if (cell.isExploded) '1' else '0')
                sb.append(',')
            }
        }
        return sb.toString()
    }

    fun deserialize(data: String, rows: Int, cols: Int): List<List<Cell>>? {
        try {
            val tokens = data.split(',').filter { it.length >= 5 }
            if (tokens.size != rows * cols) return null

            val board = List(rows) { r ->
                List(cols) { c ->
                    val idx = r * cols + c
                    val token = tokens[idx]
                    Cell(
                        row = r,
                        col = c,
                        isMine = token[0] == '1',
                        isRevealed = token[1] == '1',
                        isFlagged = token[2] == '1',
                        adjacentMines = token[3].digitToIntOrNull() ?: 0,
                        isExploded = token[4] == '1'
                    )
                }
            }
            return board
        } catch (e: Exception) {
            return null
        }
    }
}
