package com.example.cognitiveassesmenttest.ui.interfaces

/**
 * Interface for the score data class
 * @property userId the user id
 * @property scoreA the scoreA
 * @property scoreB the scoreB
 * @property diagnosis the diagnosis
 * @property time the time
 */
interface ScoreTMT {
    val userId: String
    val scoreA: String
    val scoreB: String
    val diagnosis: String
    val time: String
}