package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ActiveGameEntity
import com.example.data.model.GameRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE isWin = 1 AND difficulty = :diff ORDER BY timeSeconds ASC LIMIT 50")
    fun getLeaderboard(diff: String): Flow<List<GameRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GameRecordEntity): Long

    @Query("SELECT * FROM active_game WHERE id = 1 LIMIT 1")
    fun getActiveGame(): Flow<ActiveGameEntity?>

    @Query("SELECT * FROM active_game WHERE id = 1 LIMIT 1")
    suspend fun getActiveGameOnce(): ActiveGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveGame(game: ActiveGameEntity)

    @Query("DELETE FROM active_game WHERE id = 1")
    suspend fun deleteActiveGame()

    @Query("DELETE FROM game_records")
    suspend fun clearAllRecords()
}
