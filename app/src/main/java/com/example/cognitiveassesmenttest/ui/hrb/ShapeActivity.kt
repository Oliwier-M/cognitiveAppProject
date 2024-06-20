package com.example.cognitiveassesmenttest.ui.hrb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R

class ShapeActivity : AppCompatActivity() {

    private lateinit var ans1: Button
    private lateinit var ans2: Button
    private lateinit var ans3: Button
    private lateinit var ans4: Button
    private lateinit var checkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shape)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ans1 = findViewById(R.id.ans1)
        ans2 = findViewById(R.id.ans2)
        ans3 = findViewById(R.id.ans3)
        ans4 = findViewById(R.id.ans4)
        checkButton = findViewById(R.id.check)
        checkButton.isEnabled = false

        ans1.setOnClickListener {
            handleAnswerClick(ans1)
        }

        ans2.setOnClickListener {
            handleAnswerClick(ans2)
        }

        ans3.setOnClickListener {
            handleAnswerClick(ans3)
        }

        ans4.setOnClickListener {
            handleAnswerClick(ans4)
        }

        checkButton.setOnClickListener {
            checkButton.isEnabled = false
            val score = checkAnswers()
            val handler = android.os.Handler()
            handler.postDelayed({
                val intent = Intent(this, SameSoundActivity::class.java)
                intent.putExtra("score", score)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private fun handleAnswerClick(button: Button?) {
        button?.let {
            it.isSelected = !it.isSelected
            it.backgroundTintList = if (it.isSelected) {
                resources.getColorStateList(R.color.button_selected)
            } else {
                resources.getColorStateList(R.color.button_default)
            }
        }
        updateCheckButtonState()
    }

    private fun updateCheckButtonState() {
        checkButton.isEnabled = ans1.isSelected || ans2.isSelected || ans3.isSelected || ans4.isSelected
    }

    private fun checkAnswers(): Int {
        val selectedAnswers = mutableListOf<Int>()

        if (ans1.isSelected) {
            selectedAnswers.add(1)
        }
        if (ans2.isSelected) {
            selectedAnswers.add(2)
        }
        if (ans3.isSelected) {
            selectedAnswers.add(3)
        }
        if (ans4.isSelected) {
            selectedAnswers.add(4)
        }

        val correctAnswers = listOf(1, 3)
        var score = 0

        correctAnswers.forEach { correctAnswer ->
            if (selectedAnswers.contains(correctAnswer)) {
                score++
            }
        }

        if (score == correctAnswers.size) {
            ans1.backgroundTintList = resources.getColorStateList(R.color.green)
            ans3.backgroundTintList = resources.getColorStateList(R.color.green)
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            selectedAnswers.forEach {
                if (!correctAnswers.contains(it)) {
                    when (it) {
                        2 -> ans2.backgroundTintList = resources.getColorStateList(R.color.red)
                        4 -> ans4.backgroundTintList = resources.getColorStateList(R.color.red)
                    }
                }
            }
            ans1.backgroundTintList = resources.getColorStateList(R.color.green)
            ans3.backgroundTintList = resources.getColorStateList(R.color.green)
        }
        return score
    }
}
