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
 * RepeatingActivity is an activity that displays the Repeating Activity of the MMSE test.
 * The user is asked to recall three words: "MONKEY", "APPLE", and "SHELF".
 * The user is then asked to write them down.
 * The user is scored based on the number of words they repeat correctly.
 *
 */

class RepeatingActivity : AppCompatActivity() {

    private lateinit var firstWord: EditText
    private lateinit var secondWord: EditText
    private lateinit var thirdWord: EditText
    private lateinit var checkButton: Button

    /**
     * onCreate is called when the activity is starting. This is where most initialization should go.
     * @param savedInstanceState is a reference to a Bundle object that is passed into the onCreate method of every Android Activity.
     */
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
        checkButton.isEnabled = false

        var finalScore = intent.getIntExtra("score", 0)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val first = firstWord.text.toString().trim()
                val second = secondWord.text.toString().trim()
                val third = thirdWord.text.toString().trim()
                checkButton.isEnabled = first.isNotEmpty() && second.isNotEmpty() && third.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        firstWord.addTextChangedListener(textWatcher)
        secondWord.addTextChangedListener(textWatcher)
        thirdWord.addTextChangedListener(textWatcher)


        checkButton.setOnClickListener {
            val first = firstWord.text.toString().trim().uppercase()
            val second = secondWord.text.toString().trim().uppercase()
            val third = thirdWord.text.toString().trim().uppercase()

            if (first.isBlank() || second.isBlank() || third.isBlank()) {
                return@setOnClickListener
            }

            val score = checkAnswers(first, second, third)
            finalScore += score
            val intent = Intent(this, SummaryActivity::class.java)
            intent.putExtra("score", finalScore)
            Thread.sleep(2000)
            startActivity(intent)
            finish()
        }
    }

    /**
     * checkAnswers checks the user's answers against the correct answers and returns the score.
     * @param first is the user's answer to the first word.
     * @param second is the user's answer to the second word.
     * @param third is the user's answer to the third word.
     * @return the score based on the number of correct answers.
     */
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