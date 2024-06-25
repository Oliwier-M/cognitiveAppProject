package com.example.cognitiveassesmenttest.ui.hrb

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.cognitiveassesmenttest.R

/**
 * Activity to test the user's ability to differentiate between 3 sounds.
 * The user is presented with 3 sounds and must determine if they are the same or different.
 * The user is then presented with three sounds and must determine which of the three sounds is different.
 * The user's score is passed to the next activity.
 */
class SameSoundActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: Button
    private lateinit var btnSame: Button
    private lateinit var btnDifferent: Button
    private lateinit var ans1: Button
    private lateinit var ans2: Button
    private lateinit var ans3: Button
    private lateinit var checkButton: Button
    private lateinit var ansQuestion: TextView

    private val rhythms = arrayOf(
        R.raw.wood,
        R.raw.glass
    )
    /**
     * Sets up the activity layout and views.
     * The user is presented with a play button to listen to a sound.
     * The user is then presented with a button to determine if the sound is the same or different.
     * The user is then presented with three buttons to determine which sound is different.
     * The user's score is passed to the next activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_same_sound)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnPlay = findViewById(R.id.btnPlay)
        btnSame = findViewById(R.id.btnSame)
        btnDifferent = findViewById(R.id.btnDifferent)
        ans1 = findViewById(R.id.ans1)
        ans2 = findViewById(R.id.ans2)
        ans3 = findViewById(R.id.ans3)
        checkButton = findViewById(R.id.check)
        ansQuestion = findViewById(R.id.ansQuestion)
        var finalScore = intent.getIntExtra("score", 0)

        btnSame.isVisible = false
        btnDifferent.isVisible = false
        ans1.isVisible = false
        ans2.isVisible = false
        ans3.isVisible = false
        checkButton.isVisible = false
        ansQuestion.isVisible = false

        checkButton.isEnabled = false

        val handler = android.os.Handler()

        btnPlay.setOnClickListener {
            playCurrentRhythm(0)
            handler.postDelayed({
                btnSame.isVisible = true
            }, 2000)
        }

        btnSame.setOnClickListener {
            playCurrentRhythm(0)
            handler.postDelayed({
                btnDifferent.isVisible = true
            }, 2000)
        }

        btnDifferent.setOnClickListener {
            playCurrentRhythm(1)
            handler.postDelayed({
                ans1.isVisible = true
                ans2.isVisible = true
                ans3.isVisible = true
                checkButton.isVisible = true
                ansQuestion.isVisible = true
            }, 2000)
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
            checkAnswers()
            val score = checkAnswers()
            finalScore += score

            handler.postDelayed({
                val intent = Intent(this, SpeechSoundActivity::class.java)
                intent.putExtra("score", finalScore)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }
    /**
     * Handles the user's answer selection.
     * Updates the button's appearance based on the user's selection.
     * Updates the check button's state based on the user's selection.
     */
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
    /**
     * Updates the state of the check button based on the user's answer selection.
     */
    private fun updateCheckButtonState() {
        checkButton.isEnabled = ans1.isSelected || ans2.isSelected || ans3.isSelected
    }
    /**
     * Plays the current rhythm based on user click.
     */
    private fun playCurrentRhythm(currentRhythmIndex: Int) {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(this, rhythms[currentRhythmIndex])
        mediaPlayer.setOnCompletionListener {
            enableAnswerButtons(true)
        }
        mediaPlayer.start()

        enableAnswerButtons(false)
    }
    /**
     * Checks the user's answers and returns the user's score.
     */
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

        val correctAnswers = listOf(1, 2)
        var score = 0

        if (selectedAnswers == correctAnswers) {
            score = 1
        }

        if (score == 1) {
            ans1.backgroundTintList = resources.getColorStateList(R.color.green)
            ans2.backgroundTintList = resources.getColorStateList(R.color.green)
        } else {
            selectedAnswers.forEach {
                when (it) {
                    1 -> ans1.backgroundTintList = if (it in correctAnswers) resources.getColorStateList(R.color.green) else resources.getColorStateList(R.color.red)
                    2 -> ans2.backgroundTintList = if (it in correctAnswers) resources.getColorStateList(R.color.green) else resources.getColorStateList(R.color.red)
                    3 -> ans3.backgroundTintList = if (it in correctAnswers) resources.getColorStateList(R.color.green) else resources.getColorStateList(R.color.red)
                }
            }
        }

        return score
    }

    /**
     * Enables or disables the answer buttons based on the given boolean.
     */
    private fun enableAnswerButtons(enable: Boolean) {
        ans1.isEnabled = enable
        ans2.isEnabled = enable
        ans3.isEnabled = enable
    }
}
