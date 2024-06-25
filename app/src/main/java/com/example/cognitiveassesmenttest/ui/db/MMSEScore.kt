package com.example.cognitiveassesmenttest.ui.db

import com.example.cognitiveassesmenttest.ui.interfaces.Score

/**
 * Data class representing a MMSE score.
 * @property userId The user's ID.
 * @property score The score for the MMSE test.
 * @property diagnosis The diagnosis based on the test results.
 * @property time The time taken to complete the test.
 */
data class MMSEScore(
    override val userId: String = "",
    override val score: String = "",
    override val diagnosis: String = "",
    override val time: String = ""
) : Score