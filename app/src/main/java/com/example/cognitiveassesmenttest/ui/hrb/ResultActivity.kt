package com.example.cognitiveassesmenttest.ui.hrb

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
import com.example.cognitiveassesmenttest.ui.db.HRBScore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResultActivity : AppCompatActivity() {
    private lateinit var resultText: TextView
    private lateinit var infoText: TextView
    private lateinit var menuButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var diagnosis: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        auth = FirebaseAuth.getInstance()
        resultText = findViewById(R.id.resultText)
        infoText = findViewById(R.id.infoText)
        menuButton = findViewById(R.id.menuButton)
        val finalScore = intent.getIntExtra("score", 0)

        resultText.text = "Your score: $finalScore points"
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
            finalScore >= 12 -> "no cognitive impairment"
            finalScore in 9..11 -> "mild dementia"
            finalScore in 6..8 -> "moderate dementia"
            else -> "severe dementia"
        }
        infoText.text = "You have $diagnosis."
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScoreToFirebase(score: Int) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("HRB_scores")
            val scoreId = scoresRef.push().key ?: ""
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val userScore = HRBScore(userId, "$score/14", diagnosis, currentDateTime)

            scoresRef.child(scoreId).setValue(userScore)
                .addOnSuccessListener {
                    Log.d("Firebase", "Score successfully saved with ID: $scoreId")
                    Toast.makeText(this, "Score saved successfully", Toast.LENGTH_SHORT).show()
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