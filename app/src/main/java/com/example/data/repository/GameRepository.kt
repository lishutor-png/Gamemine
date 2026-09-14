package com.example.data.repository

import com.example.data.dao.GameDao
import com.example.data.model.ActiveGameEntity
import com.example.data.model.GameRecordEntity
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allRecords: Flow<List<GameRecordEntity>> = gameDao.getAllRecords()
    val activeGameFlow: Flow<ActiveGameEntity?> = gameDao.getActiveGame()

    fun getLeaderboard(difficulty: String): Flow<List<GameRecordEntity>> {
        return gameDao.getLeaderboard(difficulty)
    }

    suspend fun getActiveGameOnce(): ActiveGameEntity? {
        return gameDao.getActiveGameOnce()
    }

    suspend fun saveRecord(record: GameRecordEntity): Long {
        return gameDao.insertRecord(record)
    }

    suspend fun saveActiveGame(activeGame: ActiveGameEntity) {
        gameDao.saveActiveGame(activeGame)
    }

    suspend fun deleteActiveGame() {
        gameDao.deleteActiveGame()
    }

    suspend fun clearHistory() {
        gameDao.clearAllRecords()
    }
}
