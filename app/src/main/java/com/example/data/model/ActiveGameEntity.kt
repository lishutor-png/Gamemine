package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_game")
data class ActiveGameEntity(
    @PrimaryKey
    val id: Int = 1,
    val difficulty: String,
    val rows: Int,
    val cols: Int,
    val mines: Int,
    val boardData: String,
    val elapsedSeconds: Long,
    val gameState: String,
    val firstClickDone: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
