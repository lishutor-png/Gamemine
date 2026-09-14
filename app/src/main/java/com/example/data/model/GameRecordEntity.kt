package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val difficulty: String,
    val rows: Int,
    val cols: Int,
    val mines: Int,
    val timeSeconds: Long,
    val isWin: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val playerName: String = "Operator"
)
