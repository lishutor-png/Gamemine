package com.example.domain.model

data class Cell(
    val row: Int,
    val col: Int,
    var isMine: Boolean = false,
    var isRevealed: Boolean = false,
    var isFlagged: Boolean = false,
    var adjacentMines: Int = 0,
    var isExploded: Boolean = false,
    var isFalseFlag: Boolean = false
) {
    fun copyState(): Cell = copy()
}

enum class DifficultyLevel(
    val displayName: String,
    val rows: Int,
    val cols: Int,
    val defaultMines: Int
) {
    EASY("Rookie", 9, 9, 10),
    MEDIUM("Specialist", 14, 14, 30),
    HARD("Master", 18, 14, 48),
    CUSTOM("Custom Pro", 10, 10, 15);

    companion object {
        fun fromString(name: String): DifficultyLevel {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: EASY
        }
    }
}

enum class GameStatus {
    IDLE,
    RUNNING,
    WON,
    LOST,
    PAUSED
}

enum class ControlMode {
    DIG,   // Tap: Reveal, Long Press: Flag
    FLAG   // Tap: Flag, Long Press: Reveal
}

enum class OperatorMood {
    CALM,     // Normal tactical operation
    TENSE,    // Holding / inspecting tile
    VICTORY,  // Mission cleared, defused successfully
    DETONATED // Bomb exploded
}

data class GlobalStats(
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val winRatePercent: Float = 0f,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalBombsDefused: Int = 0,
    val fastestClearTime: Long? = null,
    val averageClearTime: Long? = null,
    val operatorRank: String = "Trainee"
)

data class CustomConfig(
    val rows: Int = 12,
    val cols: Int = 12,
    val mines: Int = 20
)
