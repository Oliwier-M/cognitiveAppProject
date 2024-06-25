package com.example.cognitiveassesmenttest.ui.gameone

import android.annotation.SuppressLint
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
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.ui.adapters.ScoreAdapter
import com.example.cognitiveassesmenttest.ui.db.CombinedScore
import com.example.cognitiveassesmenttest.ui.db.HRBScore
import com.example.cognitiveassesmenttest.ui.db.TMTScore
import com.example.cognitiveassesmenttest.ui.interfaces.Score
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Activity to display Trail Making Test (TMT) result and save/fetch scores from Firebase.
 */
class TrailResultActivity : AppCompatActivity() {

    private lateinit var resultTextA: TextView
    private lateinit var resultTextB: TextView
    private lateinit var infoText: TextView
    private lateinit var menuButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var diagnosis: String
    private lateinit var scoresList: MutableList<Score>
    private lateinit var adapter: ScoreAdapter
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trail_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        auth = FirebaseAuth.getInstance()
        resultTextA = findViewById(R.id.resultAText)
        resultTextB = findViewById(R.id.resultBText)
        infoText = findViewById(R.id.infoText)
        menuButton = findViewById(R.id.menuButton)
        recyclerView = findViewById(R.id.tmtRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scoresList = mutableListOf()
        adapter = ScoreAdapter(scoresList)
        recyclerView.adapter = adapter

        val finalScoreA = intent.getIntExtra("scoreA", 0)
        val finalScoreB = intent.getIntExtra("scoreB", 0)

        resultTextA.text = "Your score A: $finalScoreA points"
        resultTextB.text = "Your score B: $finalScoreB points"
        setInfoText(finalScoreA, finalScoreB)
        saveScoreToFirebase(finalScoreA, finalScoreB)
        fetchScoresFromFirebase()

        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Sets the information text based on the calculated scores.
     *
     * @param scoreA The score obtained in test A.
     * @param scoreB The score obtained in test B.
     */
    @SuppressLint("SetTextI18n")
    private fun setInfoText(scoreA: Int, scoreB: Int ) {
        val finalScore = scoreA + scoreB
        diagnosis = when {
            finalScore < 104 -> "you have no cognitive impairment"
            finalScore in 104.. 352 -> "you have cognitive decline"
            else -> "your cognitive ability is deficient"
        }
        infoText.text = "Based on the result, $diagnosis."
    }

    /**
     * Saves the TMT scores to Firebase.
     *
     * @param scoreA The score obtained in test A.
     * @param scoreB The score obtained in test B.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScoreToFirebase(scoreA: Int, scoreB: Int) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("TMT_scores")
            val scoreId = scoresRef.push().key ?: ""

            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
            val formattedDateTime = currentDateTime.format(formatter)

            val userScore = TMTScore(userId, "$scoreA", "$scoreB", diagnosis, formattedDateTime)

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
     * Fetches TMT scores from Firebase for the current user.
     */
    private fun fetchScoresFromFirebase() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoresRef = database.getReference("TMT_scores")

            scoresRef.orderByChild("userId").equalTo(userId).addValueEventListener(object :
                ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    scoresList.clear()
                    for (scoreSnapshot in snapshot.children) {
                        val score = scoreSnapshot.getValue(TMTScore::class.java)
                        if (score != null) {

                            val combinedScore = CombinedScore(
                                userId = score.userId,
                                score = "${score.scoreA.toInt() + score.scoreB.toInt()}",
                                diagnosis = score.diagnosis,
                                time = score.time
                            )
                            scoresList.add(combinedScore)
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