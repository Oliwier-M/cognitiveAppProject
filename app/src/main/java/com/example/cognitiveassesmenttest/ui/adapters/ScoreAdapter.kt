package com.example.cognitiveassesmenttest.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.interfaces.Score

class ScoreAdapter(private val scores: List<Score>) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val diagnosisTextView: TextView = itemView.findViewById(R.id.diagnosisTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.scoreTextView.text = "Score: ${score.score}"
        holder.diagnosisTextView.text = "Diagnosis: ${score.diagnosis}"
        holder.dateTextView.text = "Date: ${score.time}"
    }

    override fun getItemCount(): Int = scores.size
}
