package com.example.cognitiveassesmenttest.ui.db

import com.example.cognitiveassesmenttest.ui.interfaces.Score

data class MMSEScore(
    override val userId: String = "",
    override val score: String = "",
    override val diagnosis: String = "",
    override val time: String = ""
) : Score