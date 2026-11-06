package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val indexValue: Double,
    val radicandValue: Double,
    val resultText: String,
    val isComplex: Boolean,
    val simplifiedForm: String?,
    val timestamp: Long = System.currentTimeMillis()
)
