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
import com.example.cognitiveassesmenttest.ui.mmse.CalculationActivity

class ShapeActivity : AppCompatActivity() {

    private var ans1: Button? = null
    private var ans2: Button? = null
    private var ans3: Button? = null
    private var ans4: Button? = null
    private var checkButton: Button? = null

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

        ans1?.setOnClickListener {
            handleAnswerClick(ans1)
        }

        ans2?.setOnClickListener {
            handleAnswerClick(ans2)
        }

        ans3?.setOnClickListener {
            handleAnswerClick(ans3)
        }

        ans4?.setOnClickListener {
            handleAnswerClick(ans4)
        }

        checkButton?.setOnClickListener {
            checkButton?.isEnabled = false
            val score = checkAnswers()
            val handler = android.os.Handler()
            handler.postDelayed({
                val intent = Intent(this, SeashoreActivity::class.java)
                intent.putExtra("score", score)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }

    private fun handleAnswerClick(button: Button?) {
        button?.let {
            if (it.isSelected) {
                it.isSelected = false
                it.backgroundTintList = resources.getColorStateList(R.color.button_default)
            } else {
                it.isSelected = true
                it.backgroundTintList = resources.getColorStateList(R.color.button_selected)
            }
        }
    }

    private fun checkAnswers(): Int {
        val selectedAnswers = mutableListOf<Int>()

        if (ans1?.isSelected == true) {
            selectedAnswers.add(1)
        }
        if (ans2?.isSelected == true) {
            selectedAnswers.add(2)
        }
        if (ans3?.isSelected == true) {
            selectedAnswers.add(3)
        }
        if (ans4?.isSelected == true) {
            selectedAnswers.add(4)
        }

        val correctAnswers = listOf(1, 3)
        var score = 0
        if (selectedAnswers == correctAnswers) {
            ans1?.backgroundTintList = resources.getColorStateList(R.color.green)
            ans3?.backgroundTintList = resources.getColorStateList(R.color.green)

            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            score = 1
        } else {
            ans1?.backgroundTintList = resources.getColorStateList(R.color.green)
            ans3?.backgroundTintList = resources.getColorStateList(R.color.green)
            ans2?.backgroundTintList = resources.getColorStateList(R.color.red)
            ans4?.backgroundTintList = resources.getColorStateList(R.color.red)
            Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show()
        }
        return score
    }
}


