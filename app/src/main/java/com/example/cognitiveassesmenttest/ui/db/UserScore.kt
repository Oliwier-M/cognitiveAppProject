package com.example.cognitiveassesmenttest.ui.db

data class UserScore(
    val userId: String,
    val score: Int,
    val assessmentType: String,
    val timestamp: Long = System.currentTimeMillis()
)