package com.example.cognitiveassesmenttest.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.interfaces.Score

/**
 * Adapter for the RecyclerView displaying the scores.
 * @property scores The list of scores to display.
 */
class ScoreAdapter(private val scores: List<Score>) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    /**
     * ViewHolder for the scores.
     * @property scoreTextView The TextView displaying the score.
     * @property diagnosisTextView The TextView displaying the diagnosis.
     * @property dateTextView The TextView displaying the date.
     */
    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val diagnosisTextView: TextView = itemView.findViewById(R.id.diagnosisTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }
    /**
     * Creates a new ViewHolder for the scores.
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return The new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }
    /**
     * Binds the ViewHolder to the data.
     * @param holder The ViewHolder to bind.
     * @param position The position of the data.
     */
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.scoreTextView.text = "Score: ${score.score}"
        holder.diagnosisTextView.text = "Diagnosis: ${score.diagnosis}"
        holder.dateTextView.text = "Date: ${score.time}"
    }
    /**
     * @return The number of scores.
     */
    override fun getItemCount(): Int = scores.size
}
