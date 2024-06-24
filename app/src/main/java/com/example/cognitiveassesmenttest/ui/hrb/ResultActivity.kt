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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.adapters.ScoreAdapter
import com.example.cognitiveassesmenttest.ui.db.HRBScore
import com.example.cognitiveassesmenttest.ui.interfaces.Score
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Activity to display the result of the Halstead Reitan Battery test.
 * The user's score is displayed along with a diagnosis based on the score.
 * The user's score is saved to Firebase and the user's previous scores are fetched from Firebase.
 * The user can return to the main menu by clicking the menu button.
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var infoText: TextView
    private lateinit var menuButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var diagnosis: String
    private lateinit var scoresList: MutableList<Score>
    private lateinit var adapter: ScoreAdapter

    /**
     * Sets up the activity layout and views.
     * Displays the user's score and diagnosis.
     * Saves the user's score to Firebase.
     * Fetches the user's previous scores from Firebase.
     */
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
        recyclerView = findViewById(R.id.hrbRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scoresList = mutableListOf()
        adapter = ScoreAdapter(scoresList)
        recyclerView.adapter = adapter

        val finalScore = intent.getIntExtra("score", 0)

        resultText.text = "Your score: $finalScore/14 points"
        setInfoText(finalScore)
        saveScoreToFirebase(finalScore)
        fetchScoresFromFirebase()

        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    /**
     * Sets the diagnosis based on the user's score.
     */
    private fun setInfoText(finalScore: Int) {
        diagnosis = when {
            finalScore >= 12 -> "no cognitive impairment"
            finalScore in 9..11 -> "mild dementia"
            finalScore in 6..8 -> "moderate dementia"
            else -> "severe dementia"
        }
        infoText.text = "Based on the result, you might have $diagnosis."
    }
    /**
     * Saves the user's score to Firebase.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScoreToFirebase(score: Int) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("HRB_scores")
            val scoreId = scoresRef.push().key ?: ""

            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
            val formattedDateTime = currentDateTime.format(formatter)

            val userScore = HRBScore(userId, "$score/14", diagnosis, formattedDateTime)

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
    /**
     * Fetches the user's previous scores from Firebase.
     */
    private fun fetchScoresFromFirebase() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("HRB_scores")

            scoresRef.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scoresList.clear()
                    for (scoreSnapshot in snapshot.children) {
                        val score = scoreSnapshot.getValue(HRBScore::class.java)
                        if (score != null) {
                            scoresList.add(score)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to fetch scores", error.toException())
                }
            })
        }
    }
}
