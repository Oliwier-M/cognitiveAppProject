package com.example.cognitiveassesmenttest.ui.interfaces

/**
 * Interface for the score data class
 * @property userId the user id
 * @property score the score
 * @property diagnosis the diagnosis
 * @property time the time
 */
interface Score {
    val userId: String
    val score: String
    val diagnosis: String
    val time: String
}