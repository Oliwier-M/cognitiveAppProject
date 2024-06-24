package com.example.cognitiveassesmenttest.ui.db

import com.example.cognitiveassesmenttest.ui.interfaces.Score
/**
 * Data class representing a combined score.
 * @property userId The user's ID.
 * @property score The score for the combined test.
 * @property diagnosis The diagnosis based on the test results.
 * @property time The time taken to complete the test.
 */
data class CombinedScore(
    override val userId: String,
    override val score: String,
    override val diagnosis: String,
    override val time: String
) : Score