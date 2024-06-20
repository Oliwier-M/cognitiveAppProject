package com.example.cognitiveassesmenttest.ui.mmse

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.MainActivity
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.db.MMSEScore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SummaryActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var infoText: TextView
    private lateinit var menuButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var diagnosis: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        resultText = findViewById(R.id.resultText)
        infoText = findViewById(R.id.infoText)
        menuButton = findViewById(R.id.menuButton)
        val finalScore = intent.getIntExtra("score", 0)

        resultText.text = "Your score: $finalScore/18 points"
        setInfoText(finalScore)
        saveScoreToFirebase(finalScore)

        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setInfoText(finalScore: Int) {
        diagnosis = when {
            finalScore >= 16 -> "no cognitive impairment"
            finalScore in 11..15 -> "mild dementia"
            finalScore in 6..10 -> "moderate dementia"
            else -> "severe dementia"
        }
        infoText.text = "You have $diagnosis"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScoreToFirebase(score: Int) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("MMSE_scores")
            val scoreId = scoresRef.push().key ?: ""
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val userScore = MMSEScore(userId, "$score/18", diagnosis, currentDateTime)

            scoresRef.child(scoreId).setValue(userScore)
                .addOnSuccessListener {
                    Log.d("Firebase", "Score successfully saved with ID: $scoreId")
                    Toast.makeText(this, "Score saved to Firebase!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Failed to save score", exception)
                    Toast.makeText(
                        this,
                        "Failed to save score: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
