package com.example.cognitiveassesmenttest.ui.mmse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.cognitiveassesmenttest.R
import java.util.*

class RepetitionActivity : AppCompatActivity(), RecognitionListener {
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var readButton: Button? = null
    private var speakButton: Button? = null
    private var resultView: TextView? = null
    private var repetitions = 5
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repetition)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        readButton = findViewById(R.id.readButton)
        speakButton = findViewById(R.id.speakButton)
        resultView = findViewById(R.id.resultView)
        speakButton?.isVisible = false

        speakButton?.let {
            if (it.isPressed) {
                it.text = "Listening..."
            } else {
                it.text = "Hold to speak"
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            setupSpeechRecognition()
        }

        textToSpeech = TextToSpeech(
            this
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        readButton?.setOnClickListener {
            val words = arrayOf("Monkey", "Apple", "Shelf")
            for (word in words) {
                textToSpeech?.speak(word, TextToSpeech.QUEUE_ADD, null, null)

                Thread.sleep(1000)
                speakButton?.isVisible = true
            }
        }

    }

    private fun setupSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(this)

        speakButton?.setOnLongClickListener {
            startListening()
            true
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onEndOfSpeech() {
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server sends error status"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var score = 0
        resultView?.text = "Recognised words: " + matches?.joinToString(", ")
        matches?.let {
            var recognizedCorrectly = false
            val originalWords = arrayOf("Monkey", "Apple", "Shelf")
            for (recognizedText in it) {
                val recognizedWords = recognizedText.split(" ")
                if (recognizedWords.size == originalWords.size) {
                    if (recognizedWords.zip(originalWords)
                        .all { (recognizedWord, expectedWord) ->
                            recognizedWord.equals(expectedWord, ignoreCase = true)
                        }) {
                        recognizedCorrectly = true
                        break
                    }

                    for (i in originalWords.indices) {
                        if (originalWords[i].equals(recognizedWords[i], ignoreCase = true)) {
                            score++
                        }
                    }
                }
            }

            if (recognizedCorrectly) {
                score = 3
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
                val handler = android.os.Handler()
                handler.postDelayed({
                    val intent = Intent(this, CalculationActivity::class.java)
                    if (repetitions == 5) {
                        intent.putExtra("score", score)
                    }
                    startActivity(intent)
                    finish()
                }, 2000)
            } else {
                repetitions--
                if (repetitions <= 0) {
                    Toast.makeText(this, "Out of retries", Toast.LENGTH_SHORT).show()
                    val handler = android.os.Handler()
                    handler.postDelayed({
                        val intent = Intent(this, CalculationActivity::class.java)
                        score = 0
                        intent.putExtra("score", score)
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else {
                    Toast.makeText(this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}
