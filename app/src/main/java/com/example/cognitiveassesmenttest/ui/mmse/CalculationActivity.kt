package com.example.cognitiveassesmenttest.ui.mmse

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R

/**
 * Activity for the calculation screen.
 * The user is asked to subtract 7 from 100 and then subtract 7 from the result, and so on.
 * The user is scored based on the number of correct answers.
 */
class CalculationActivity : AppCompatActivity() {
    private lateinit var editFirst: EditText
    private lateinit var editSecond: EditText
    private lateinit var editThird: EditText
    private lateinit var editFourth: EditText
    private lateinit var editFifth: EditText
    private lateinit var checkButton: Button

    /**
     * Creates the view for the calculation screen.
     * @param savedInstanceState The saved instance state.
     */
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
        checkButton.isEnabled = false
        var finalScore = intent.getIntExtra("score", 0)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkButton.isEnabled = areAllFieldsFilled()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editFirst.addTextChangedListener(textWatcher)
        editSecond.addTextChangedListener(textWatcher)
        editThird.addTextChangedListener(textWatcher)
        editFourth.addTextChangedListener(textWatcher)
        editFifth.addTextChangedListener(textWatcher)

        checkButton.setOnClickListener {
            val first = editFirst.text.toString().trim()
            val second = editSecond.text.toString().trim()
            val third = editThird.text.toString().trim()
            val fourth = editFourth.text.toString().trim()
            val fifth = editFifth.text.toString().trim()

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

    /**
     * Checks if all the fields are filled.
     * @return True if all the fields are filled, false otherwise.
     */
    private fun areAllFieldsFilled(): Boolean {
        return editFirst.text.isNotBlank() &&
                editSecond.text.isNotBlank() &&
                editThird.text.isNotBlank() &&
                editFourth.text.isNotBlank() &&
                editFifth.text.isNotBlank()
    }

    /**
     * Checks the answers given by the user.
     * @param first The answer to the first question.
     * @param second The answer to the second question.
     * @param third The answer to the third question.
     * @param fourth The answer to the fourth question.
     * @param fifth The answer to the fifth question.
     * @return The score of the user based on their answer.
     */
    private fun checkAnswers(
        first: String,
        second: String,
        third: String,
        fourth: String,
        fifth: String
    ): Int {
        val correctAnswers = arrayOf("93", "86", "79", "72", "65")
        val userAnswers = arrayOf(first, second, third, fourth, fifth)
        val editTexts = arrayOf(editFirst, editSecond, editThird, editFourth, editFifth)
        var score = 0

        for (i in correctAnswers.indices) {
            if (userAnswers[i] == correctAnswers[i]) {
                editTexts[i].setBackgroundColor(resources.getColor(R.color.green))
                score++
            } else {
                editTexts[i].setBackgroundColor(resources.getColor(R.color.red))
            }
        }

        return score
    }
}
