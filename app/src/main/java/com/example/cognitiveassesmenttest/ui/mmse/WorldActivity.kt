package com.example.cognitiveassesmenttest.ui.mmse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R

class WorldActivity : AppCompatActivity() {

    private var worldBackward: EditText? = null
    private var checkButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_world)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        worldBackward = findViewById(R.id.revWorldText)
        checkButton = findViewById(R.id.checkButton)
        var finalScore = intent.getIntExtra("score", 0)

        checkButton?.setOnClickListener {
            val backward = worldBackward?.text.toString().trim().uppercase()

            if (backward.isBlank()) {
                return@setOnClickListener
            }

            val score = checkAnswers(backward)
            finalScore += score
            val intent = Intent(this, DrawingActivity::class.java)
            intent.putExtra("score", finalScore)
            Thread.sleep(2000)
            startActivity(intent)
        }
    }

    private fun checkAnswers(backward: String): Int {
        val correctRevWorld = "DLROW"
        val score: Int

        val backwardChars = backward.toCharArray()
        val correctBackwardChars = correctRevWorld.toCharArray()

        score = if (backward == correctRevWorld) {
            5
        } else if (backwardChars[0] == correctBackwardChars[0] && backwardChars[4] == correctBackwardChars[4]) {
            4
        } else if (backwardChars[0] == correctBackwardChars[1] && backwardChars[2] == correctBackwardChars[2] && backwardChars[3] == correctBackwardChars[4]) {
            3
        } else if (backwardChars[1] == correctBackwardChars[1] && backwardChars[3] == correctBackwardChars[2]) {
            2
        } else if (backwardChars[2] == correctBackwardChars[1]) {
            1
        } else {
            0
        }

        return score
    }
}