package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CalculationDao) {
    val allHistory: Flow<List<CalculationEntity>> = dao.getAllHistory()

    suspend fun saveCalculation(item: CalculationEntity) {
        dao.insert(item)
    }

    suspend fun deleteCalculation(item: CalculationEntity) {
        dao.delete(item)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
