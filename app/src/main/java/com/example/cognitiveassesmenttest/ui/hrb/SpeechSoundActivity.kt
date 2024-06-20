package com.example.cognitiveassesmenttest.ui.hrb

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.cognitiveassesmenttest.R
import java.util.Locale

class SpeechSoundActivity : AppCompatActivity() {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var readButton: Button
    private lateinit var ans1: Button
    private lateinit var ans2: Button
    private lateinit var ans3: Button
    private lateinit var checkButton: Button
    private lateinit var ansText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_speech_sound)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        readButton = findViewById(R.id.readButton)
        ans1 = findViewById(R.id.ans1)
        ans2 = findViewById(R.id.ans2)
        ans3 = findViewById(R.id.ans3)
        checkButton = findViewById(R.id.check)
        ansText = findViewById(R.id.answerText)
        var finalScore = intent.getIntExtra("score", 0)

        ans1.isVisible = false
        ans2.isVisible = false
        ans3.isVisible = false
        checkButton.isVisible = false
        ansText.isVisible = false

        readButton.setOnClickListener {
            val words = arrayOf("ba", "da", "ta", "ka", "ra")
            for (word in words) {
                textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null, null)
                Thread.sleep(1000)
            }
            readButton.isVisible = false
            ans1.isVisible = true
            ans2.isVisible = true
            ans3.isVisible = true
            checkButton.isVisible = true
            ansText.isVisible = true
            checkButton .isEnabled = false
        }

        ans1.setOnClickListener {
            handleAnswerClick(ans1)
        }

        ans2.setOnClickListener {
            handleAnswerClick(ans2)
        }

        ans3.setOnClickListener {
            handleAnswerClick(ans3)
        }

        checkButton.setOnClickListener {
            checkButton.isEnabled = false
            ans1.isEnabled = false
            ans2.isEnabled = false
            ans3.isEnabled = false

            val score = checkAnswers()
            finalScore += score
            val handler = android.os.Handler()
            handler.postDelayed({
                val intent = Intent(this, NamingActivity::class.java)
                intent.putExtra("score", finalScore)
                startActivity(intent)
                finish()
            }, 1000)
        }


        textToSpeech = TextToSpeech(
            this
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleAnswerClick(button: Button?) {
        checkButton.isEnabled = true
        button?.let {
            deselectAllButtons()
            it.isSelected = true
            it.backgroundTintList = resources.getColorStateList(R.color.button_selected)
        }
    }

    private fun deselectAllButtons() {
        ans1.isSelected = false
        ans2.isSelected = false
        ans3.isSelected = false
        ans1.backgroundTintList = resources.getColorStateList(R.color.button_default)
        ans2.backgroundTintList = resources.getColorStateList(R.color.button_default)
        ans3.backgroundTintList = resources.getColorStateList(R.color.button_default)
    }

    private fun checkAnswers(): Int {
        var score = 0
        ans3.backgroundTintList = resources.getColorStateList(R.color.green)

        if (ans3.isSelected) {
            score = 2

        } else {
            if(ans1.isSelected) {
                ans1.backgroundTintList = resources.getColorStateList(R.color.red)
            }
            else if(ans2.isSelected) {
                ans2.backgroundTintList = resources.getColorStateList(R.color.red)
            }
        }
        return score
    }
}
