package com.example.cognitiveassesmenttest.ui.mmse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R

class RepeatingActivity : AppCompatActivity() {

    private var firstWord: EditText? = null
    private var secondWord: EditText? = null
    private var thirdWord: EditText? = null
    private var checkButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repeating)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firstWord = findViewById(R.id.firstWord)
        secondWord = findViewById(R.id.secondWord)
        thirdWord = findViewById(R.id.thirdWord)
        checkButton = findViewById(R.id.confirmButton)

        var finalScore = intent.getIntExtra("score", 0)

        checkButton?.setOnClickListener {
            val first = firstWord?.text.toString().trim().uppercase()
            val second = secondWord?.text.toString().trim().uppercase()
            val third = thirdWord?.text.toString().trim().uppercase()

            if (first.isBlank() || second.isBlank() || third.isBlank()) {
                return@setOnClickListener
            }

            val score = checkAnswers(first, second, third)
            finalScore += score
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra("score", finalScore)
            Thread.sleep(2000)
            startActivity(intent)
        }
    }

    private fun checkAnswers(first: String, second: String, third: String): Int {
        val correctFirstWord = "MONKEY"
        val correctSecondWord = "APPLE"
        val correctThirdWord = "SHELF"
        var score = 0

        if (first == correctFirstWord) {
            score += 1
        }
        if (second == correctSecondWord) {
            score += 1
        }
        if (third == correctThirdWord) {
            score += 1
        }
        return score
    }
}