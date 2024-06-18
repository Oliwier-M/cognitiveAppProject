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

class CalculationActivity : AppCompatActivity() {
    private var editFirst: EditText? = null
    private var editSecond: EditText? = null
    private var editThird: EditText? = null
    private var editFourth: EditText? = null
    private var editFifth: EditText? = null
    private var checkButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editFirst = findViewById(R.id.editFirst)
        editSecond = findViewById(R.id.editSecond)
        editThird = findViewById(R.id.editThird)
        editFourth = findViewById(R.id.editFourth)
        editFifth = findViewById(R.id.editFifth)
        checkButton = findViewById(R.id.checkButton)
        var finalScore = intent.getIntExtra("score", 0)

        checkButton?.setOnClickListener {
            val first = editFirst?.text.toString().trim()
            val second = editSecond?.text.toString().trim()
            val third = editThird?.text.toString().trim()
            val fourth = editFourth?.text.toString().trim()
            val fifth = editFifth?.text.toString().trim()

            if (first.isBlank() || second.isBlank() || third.isBlank() || fourth.isBlank() || fifth.isBlank()) {
                return@setOnClickListener
            }
            val score = checkAnswers(first, second, third, fourth, fifth)
            finalScore += score
            val handler = android.os.Handler()
            handler.postDelayed({
                val intent = Intent(this, WorldActivity::class.java)
                intent.putExtra("score", finalScore)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private fun checkAnswers(
        first: String,
        second: String,
        third: String,
        fourth: String,
        fifth: String
    ) : Int {
        val correctAnswers = arrayOf(93, 86, 79, 72, 65)
        var score = 0

        if (first == correctAnswers[0].toString()) {
            editFirst?.setBackgroundColor(resources.getColor(R.color.green))
            score++
        } else {
            editFirst?.setBackgroundColor(resources.getColor(R.color.red))
        }

        if (second == correctAnswers[1].toString()) {
            editSecond?.setBackgroundColor(resources.getColor(R.color.green))
            score++
        } else {
            editSecond?.setBackgroundColor(resources.getColor(R.color.red))
        }

        if (third == correctAnswers[2].toString()) {
            editThird?.setBackgroundColor(resources.getColor(R.color.green))
            score++
        } else {
            editThird?.setBackgroundColor(resources.getColor(R.color.red))
        }

        if (fourth == correctAnswers[3].toString()) {
            editFourth?.setBackgroundColor(resources.getColor(R.color.green))
            score++
        } else {
            editFourth?.setBackgroundColor(resources.getColor(R.color.red))
        }
        if (fifth == correctAnswers[4].toString()) {
            editFifth?.setBackgroundColor(resources.getColor(R.color.green))
            score++
        } else {
            editFifth?.setBackgroundColor(resources.getColor(R.color.red))
        }
        return score
    }
}