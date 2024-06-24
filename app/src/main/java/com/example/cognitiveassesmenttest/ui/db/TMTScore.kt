package com.example.cognitiveassesmenttest.ui.db

/**
 * Data class representing a TMT score.
 * @property userId The user's ID.
 * @property scoreA The score for part A of the test.
 * @property scoreB The score for part B of the test.
 * @property diagnosis The diagnosis based on the test results.
 * @property time The time taken to complete the test.
 */
data class TMTScore (
    val userId: String = "",
    val scoreA: String = "",
    val scoreB: String = "",
    val diagnosis: String = "",
    val time: String = ""
)