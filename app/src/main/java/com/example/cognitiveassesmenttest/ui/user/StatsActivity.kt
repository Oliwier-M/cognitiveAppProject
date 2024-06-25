package com.example.cognitiveassesmenttest.ui.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.ui.db.CombinedScore
import com.example.cognitiveassesmenttest.ui.db.HRBScore
import com.example.cognitiveassesmenttest.ui.db.MMSEScore
import com.example.cognitiveassesmenttest.ui.db.TMTScore
import com.example.cognitiveassesmenttest.ui.interfaces.Score
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.coroutines.suspendCoroutine

class StatsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var tmt: MutableList<CombinedScore> = mutableListOf()
    private var hrb: MutableList<CombinedScore> = mutableListOf()
    private var mmse: MutableList<CombinedScore> = mutableListOf()

    private lateinit var tmtItem: ConstraintLayout
    private lateinit var hrbItem: ConstraintLayout
    private lateinit var mmseItem: ConstraintLayout

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stats)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tmtItem = findViewById(R.id.TMTstats)
        hrbItem = findViewById(R.id.HRBTstats)
        mmseItem = findViewById(R.id.MMSEstats)
        val menu = findViewById<Button>(R.id.menuButton)

        auth = FirebaseAuth.getInstance()

        fetchScoresFromFirebase()

        menu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI() {
        // Update UI components with fetched scores
        val latestTMT = findLatestScore(tmt)
        val bestTMT = findLowestScore(tmt)

        val latestHRB = findLatestScore(hrb)
        val bestHRB = findHighestScore(hrb)

        val latestMMSE = findLatestScore(mmse)
        val bestMMSE = findHighestScore(mmse)

        updateStatsItem(tmtItem, "Trail Making Test", latestTMT.toString(), bestTMT.toString())
        updateStatsItem(hrbItem, "Halstead Reitan Battery Test", latestHRB.toString(), bestHRB.toString())
        updateStatsItem(mmseItem, "Mini Mental State Examination", latestMMSE.toString(), bestMMSE.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findLatestScore(scores: List<CombinedScore>): Int {
        if (scores.isEmpty()) return 0

        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

        val latestScore = scores.maxByOrNull { score ->
            try {
                LocalDateTime.parse(score.time, dateTimeFormatter)
            } catch (e: DateTimeParseException) {
                LocalDateTime.MIN
            }
        }

        return latestScore?.score?.substringBefore("/")?.toIntOrNull() ?: 0
    }

    private fun findHighestScore(scores: List<CombinedScore>): Int {
        if (scores.isEmpty()) return 0
        val highestScore = scores.maxByOrNull {
            val scoreString = it.score.substringBefore("/")
            scoreString.toIntOrNull() ?: Int.MIN_VALUE
        }
        return highestScore?.score?.substringBefore("/")?.toIntOrNull() ?: 0
    }

    private fun findLowestScore(scores: List<CombinedScore>): Int {
        if (scores.isEmpty()) return 0
        val lowestScore = scores.minByOrNull {
            val scoreString = it.score.substringBefore("/")
            scoreString.toIntOrNull() ?: Int.MAX_VALUE
        }
        return lowestScore?.score?.substringBefore("/")?.toIntOrNull() ?: 0
    }

    private fun fetchScoresFromFirebase() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val scoreTypes = listOf("TMT_scores", "HRB_scores", "MMSE_scores")

            var count = 0  // Counter to track fetched score types

            for (type in scoreTypes) {
                val scoresRef = database.getReference(type)
                scoresRef.orderByChild("userId").equalTo(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (scoreSnapshot in snapshot.children) {
                                when (type) {
                                    "TMT_scores" -> {
                                        val score = scoreSnapshot.getValue(TMTScore::class.java)
                                        if (score != null) {
                                            val combinedScore = CombinedScore(
                                                userId = score.userId,
                                                score = "${score.scoreA.toInt() + score.scoreB.toInt()}",
                                                diagnosis = score.diagnosis,
                                                time = score.time
                                            )
                                            tmt.add(combinedScore)
                                        }
                                    }
                                    "HRB_scores" -> {
                                        val score = scoreSnapshot.getValue(HRBScore::class.java)
                                        if (score != null) {
                                            val combinedScore = CombinedScore(
                                                userId = score.userId,
                                                score = score.score,
                                                diagnosis = score.diagnosis,
                                                time = score.time
                                            )
                                            hrb.add(combinedScore)
                                        }
                                    }
                                    "MMSE_scores" -> {
                                        val score = scoreSnapshot.getValue(MMSEScore::class.java)
                                        if (score != null) {
                                            val combinedScore = CombinedScore(
                                                userId = score.userId,
                                                score = score.score,
                                                diagnosis = score.diagnosis,
                                                time = score.time
                                            )
                                            mmse.add(combinedScore)
                                        }
                                    }
                                }
                            }
                            count++
                            if (count == scoreTypes.size) {
                                // All score types fetched, update UI
                                updateUI()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Failed to fetch scores", error.toException())
                        }
                    })
            }
        }
    }

    private fun updateStatsItem(layout: ConstraintLayout, title: String, latestScore: String, bestScore: String) {
        val titleTextView = layout.findViewById<TextView>(R.id.titleText)
        val latestTextView = layout.findViewById<TextView>(R.id.latestText)
        val bestTextView = layout.findViewById<TextView>(R.id.bestText)

        titleTextView.text = title
        latestTextView.text = "Latest score: $latestScore"
        bestTextView.text = "Best score: $bestScore"
    }
}
