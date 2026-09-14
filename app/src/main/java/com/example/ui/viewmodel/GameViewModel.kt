package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ActiveGameEntity
import com.example.data.model.GameRecordEntity
import com.example.data.repository.GameRepository
import com.example.domain.engine.MinesweeperEngine
import com.example.domain.engine.MoveResult
import com.example.domain.model.Cell
import com.example.domain.model.ControlMode
import com.example.domain.model.CustomConfig
import com.example.domain.model.DifficultyLevel
import com.example.domain.model.GameStatus
import com.example.domain.model.GlobalStats
import com.example.domain.model.OperatorMood
import com.example.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val board: List<List<Cell>> = emptyList(),
    val rows: Int = 9,
    val cols: Int = 9,
    val totalMines: Int = 10,
    val flagsPlaced: Int = 0,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,
    val customConfig: CustomConfig = CustomConfig(12, 12, 20),
    val status: GameStatus = GameStatus.IDLE,
    val controlMode: ControlMode = ControlMode.DIG,
    val elapsedSeconds: Long = 0L,
    val operatorMood: OperatorMood = OperatorMood.CALM,
    val isAutosaved: Boolean = false,
    val isDarkMode: Boolean = true,
    val zoomLevel: Float = 1.0f,
    val showLeaderboardDialog: Boolean = false,
    val showStatsDialog: Boolean = false,
    val showCustomGridDialog: Boolean = false,
    val showVictoryDialog: Boolean = false,
    val showDefeatDialog: Boolean = false,
    val lastCompletedTime: Long = 0L
)

class GameViewModel(
    application: Application,
    private val repository: GameRepository
) : AndroidViewModel(application) {

    private val haptics = HapticHelper(application)
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var isFirstClickDone = false

    // Leaderboards reactive flow
    val allRecords: StateFlow<List<GameRecordEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val globalStats: StateFlow<GlobalStats> = allRecords.map { records ->
        calculateGlobalStats(records)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GlobalStats())

    init {
        restoreOrStartDefault()
    }

    private fun restoreOrStartDefault() {
        viewModelScope.launch {
            val saved = repository.getActiveGameOnce()
            if (saved != null && (saved.gameState == GameStatus.RUNNING.name || saved.gameState == GameStatus.PAUSED.name)) {
                val restoredBoard = MinesweeperEngine.deserialize(saved.boardData, saved.rows, saved.cols)
                if (restoredBoard != null) {
                    val diff = DifficultyLevel.fromString(saved.difficulty)
                    isFirstClickDone = saved.firstClickDone
                    val flags = MinesweeperEngine.countFlags(restoredBoard)
                    _uiState.update {
                        it.copy(
                            board = restoredBoard,
                            rows = saved.rows,
                            cols = saved.cols,
                            totalMines = saved.mines,
                            flagsPlaced = flags,
                            difficulty = diff,
                            elapsedSeconds = saved.elapsedSeconds,
                            status = GameStatus.RUNNING,
                            isAutosaved = true
                        )
                    }
                    startTimer()
                    return@launch
                }
            }
            startNewGame(DifficultyLevel.EASY)
        }
    }

    fun startNewGame(
        difficulty: DifficultyLevel,
        customConfig: CustomConfig = _uiState.value.customConfig
    ) {
        stopTimer()
        isFirstClickDone = false

        val rows = if (difficulty == DifficultyLevel.CUSTOM) customConfig.rows else difficulty.rows
        val cols = if (difficulty == DifficultyLevel.CUSTOM) customConfig.cols else difficulty.cols
        val mines = if (difficulty == DifficultyLevel.CUSTOM) {
            customConfig.mines.coerceIn(1, (rows * cols - 9).coerceAtLeast(1))
        } else {
            difficulty.defaultMines
        }

        val emptyBoard = MinesweeperEngine.createEmptyBoard(rows, cols)

        _uiState.update {
            it.copy(
                board = emptyBoard,
                rows = rows,
                cols = cols,
                totalMines = mines,
                flagsPlaced = 0,
                difficulty = difficulty,
                customConfig = customConfig,
                status = GameStatus.IDLE,
                elapsedSeconds = 0L,
                operatorMood = OperatorMood.CALM,
                isAutosaved = false,
                showVictoryDialog = false,
                showDefeatDialog = false
            )
        }

        viewModelScope.launch {
            repository.deleteActiveGame()
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val current = _uiState.value
        if (current.status == GameStatus.WON || current.status == GameStatus.LOST) return

        // Prepare mutable copies of board
        val boardCopy = current.board.map { r -> r.map { it.copyState() } }
        val targetCell = boardCopy[row][col]

        if (!isFirstClickDone) {
            // First click safety
            MinesweeperEngine.populateMines(
                board = boardCopy,
                rows = current.rows,
                cols = current.cols,
                minesCount = current.totalMines,
                safeRow = row,
                safeCol = col
            )
            isFirstClickDone = true
            _uiState.update { it.copy(status = GameStatus.RUNNING) }
            startTimer()
        }

        if (current.controlMode == ControlMode.FLAG) {
            // Flagging mode
            if (!targetCell.isRevealed) {
                MinesweeperEngine.toggleFlag(boardCopy, row, col)
                haptics.flag()
                updateBoardState(boardCopy, current.rows, current.cols, current.totalMines)
            }
            return
        }

        // Dig mode
        if (targetCell.isRevealed) {
            // Check for chord action if tapping a revealed number
            if (targetCell.adjacentMines > 0) {
                val result = MinesweeperEngine.chord(
                    board = boardCopy,
                    rows = current.rows,
                    cols = current.cols,
                    totalMines = current.totalMines,
                    row = row,
                    col = col
                )
                handleMoveResult(result, boardCopy, current.rows, current.cols, current.totalMines)
            }
            return
        }

        if (targetCell.isFlagged) {
            // In dig mode, tapping a flagged cell does not trigger bomb (safety)
            return
        }

        val result = MinesweeperEngine.reveal(
            board = boardCopy,
            rows = current.rows,
            cols = current.cols,
            totalMines = current.totalMines,
            row = row,
            col = col
        )
        handleMoveResult(result, boardCopy, current.rows, current.cols, current.totalMines)
    }

    fun onCellLongClick(row: Int, col: Int) {
        val current = _uiState.value
        if (current.status == GameStatus.WON || current.status == GameStatus.LOST) return

        val boardCopy = current.board.map { r -> r.map { it.copyState() } }
        val targetCell = boardCopy[row][col]
        if (targetCell.isRevealed) return

        // Invert mode: flag or unflag
        MinesweeperEngine.toggleFlag(boardCopy, row, col)
        haptics.flag()
        updateBoardState(boardCopy, current.rows, current.cols, current.totalMines)
    }

    private fun handleMoveResult(
        result: MoveResult,
        board: List<List<Cell>>,
        rows: Int,
        cols: Int,
        totalMines: Int
    ) {
        when (result) {
            is MoveResult.Normal -> {
                haptics.tick()
                val flags = MinesweeperEngine.countFlags(board)
                if (result.isWin) {
                    onGameWon(board, flags)
                } else {
                    _uiState.update {
                        it.copy(
                            board = board,
                            flagsPlaced = flags,
                            operatorMood = OperatorMood.CALM
                        )
                    }
                    triggerAutosave(board)
                }
            }
            is MoveResult.Detonated -> {
                onGameLost(board)
            }
            MoveResult.NoOp -> {}
        }
    }

    private fun updateBoardState(board: List<List<Cell>>, rows: Int, cols: Int, totalMines: Int) {
        val flags = MinesweeperEngine.countFlags(board)
        _uiState.update {
            it.copy(
                board = board,
                flagsPlaced = flags
            )
        }
        triggerAutosave(board)
    }

    private fun onGameWon(board: List<List<Cell>>, flags: Int) {
        stopTimer()
        haptics.victory()
        val finalTime = _uiState.value.elapsedSeconds
        _uiState.update {
            it.copy(
                board = board,
                flagsPlaced = it.totalMines,
                status = GameStatus.WON,
                operatorMood = OperatorMood.VICTORY,
                showVictoryDialog = true,
                lastCompletedTime = finalTime,
                isAutosaved = false
            )
        }

        viewModelScope.launch {
            val record = GameRecordEntity(
                difficulty = _uiState.value.difficulty.name,
                rows = _uiState.value.rows,
                cols = _uiState.value.cols,
                mines = _uiState.value.totalMines,
                timeSeconds = finalTime,
                isWin = true,
                playerName = "Operator #${(1000..9999).random()}"
            )
            repository.saveRecord(record)
            repository.deleteActiveGame()
        }
    }

    private fun onGameLost(board: List<List<Cell>>) {
        stopTimer()
        haptics.boom()
        val finalTime = _uiState.value.elapsedSeconds
        _uiState.update {
            it.copy(
                board = board,
                status = GameStatus.LOST,
                operatorMood = OperatorMood.DETONATED,
                showDefeatDialog = true,
                lastCompletedTime = finalTime,
                isAutosaved = false
            )
        }

        viewModelScope.launch {
            val record = GameRecordEntity(
                difficulty = _uiState.value.difficulty.name,
                rows = _uiState.value.rows,
                cols = _uiState.value.cols,
                mines = _uiState.value.totalMines,
                timeSeconds = finalTime,
                isWin = false,
                playerName = "Operator"
            )
            repository.saveRecord(record)
            repository.deleteActiveGame()
        }
    }

    private fun triggerAutosave(board: List<List<Cell>>) {
        val state = _uiState.value
        if (state.status != GameStatus.RUNNING) return

        _uiState.update { it.copy(isAutosaved = true) }
        viewModelScope.launch {
            val serialized = MinesweeperEngine.serialize(board)
            val active = ActiveGameEntity(
                id = 1,
                difficulty = state.difficulty.name,
                rows = state.rows,
                cols = state.cols,
                mines = state.totalMines,
                boardData = serialized,
                elapsedSeconds = state.elapsedSeconds,
                gameState = state.status.name,
                firstClickDone = isFirstClickDone
            )
            repository.saveActiveGame(active)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun toggleControlMode() {
        _uiState.update {
            val newMode = if (it.controlMode == ControlMode.DIG) ControlMode.FLAG else ControlMode.DIG
            it.copy(controlMode = newMode)
        }
        haptics.tick()
    }

    fun setControlMode(mode: ControlMode) {
        _uiState.update { it.copy(controlMode = mode) }
        haptics.tick()
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun zoomIn() {
        _uiState.update { it.copy(zoomLevel = (it.zoomLevel + 0.2f).coerceAtMost(2.5f)) }
    }

    fun zoomOut() {
        _uiState.update { it.copy(zoomLevel = (it.zoomLevel - 0.2f).coerceAtLeast(0.7f)) }
    }

    fun resetZoom() {
        _uiState.update { it.copy(zoomLevel = 1.0f) }
    }

    fun setShowLeaderboard(show: Boolean) {
        _uiState.update { it.copy(showLeaderboardDialog = show) }
    }

    fun setShowStats(show: Boolean) {
        _uiState.update { it.copy(showStatsDialog = show) }
    }

    fun setShowCustomGrid(show: Boolean) {
        _uiState.update { it.copy(showCustomGridDialog = show) }
    }

    fun dismissVictoryDialog() {
        _uiState.update { it.copy(showVictoryDialog = false) }
    }

    fun dismissDefeatDialog() {
        _uiState.update { it.copy(showDefeatDialog = false) }
    }

    fun updateCustomConfig(rows: Int, cols: Int, mines: Int) {
        val safeMines = mines.coerceIn(1, (rows * cols - 9).coerceAtLeast(1))
        val config = CustomConfig(rows, cols, safeMines)
        _uiState.update { it.copy(customConfig = config) }
        startNewGame(DifficultyLevel.CUSTOM, config)
    }

    fun saveLeaderboardPlayerName(recordId: Long, name: String) {
        // Can be expanded if player customizes name
    }

    fun clearStatsAndHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun calculateGlobalStats(records: List<GameRecordEntity>): GlobalStats {
        if (records.isEmpty()) return GlobalStats()

        val total = records.size
        val wins = records.count { it.isWin }
        val losses = total - wins
        val winRate = if (total > 0) (wins.toFloat() / total) * 100f else 0f

        // Win streak calculation (records are ordered by timestamp DESC)
        var currentStreak = 0
        var streakBroken = false
        var bestStreak = 0
        var tempStreak = 0

        val chronological = records.sortedBy { it.timestamp }
        for (r in chronological) {
            if (r.isWin) {
                tempStreak++
                if (tempStreak > bestStreak) bestStreak = tempStreak
            } else {
                tempStreak = 0
            }
        }

        for (r in records) {
            if (!streakBroken) {
                if (r.isWin) currentStreak++ else streakBroken = true
            }
        }

        val winningRecords = records.filter { it.isWin }
        val fastest = winningRecords.minOfOrNull { it.timeSeconds }
        val avgTime = if (winningRecords.isNotEmpty()) winningRecords.map { it.timeSeconds }.average().toLong() else null
        val totalBombs = winningRecords.sumOf { it.mines }

        val rank = when {
            wins >= 50 -> "Elite Legend Operator"
            wins >= 25 -> "Senior Defusal Master"
            wins >= 10 -> "Bomb Disposal Specialist"
            wins >= 3 -> "Junior Field Tech"
            else -> "Trainee Operator"
        }

        return GlobalStats(
            totalGames = total,
            totalWins = wins,
            totalLosses = losses,
            winRatePercent = winRate,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalBombsDefused = totalBombs,
            fastestClearTime = fastest,
            averageClearTime = avgTime,
            operatorRank = rank
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

class GameViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val repository = GameRepository(database.gameDao())
        return GameViewModel(application, repository) as T
    }
}
