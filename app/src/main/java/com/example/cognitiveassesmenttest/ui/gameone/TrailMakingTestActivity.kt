package com.example.cognitiveassesmenttest.ui.gameone

import Timer
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.gridlayout.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainMenuFragment
import org.w3c.dom.Text
import kotlin.random.Random

class TrailMakingTestActivity : AppCompatActivity(), Timer.TimerUpdateListener  {
    private lateinit var timerText: TextView
    private lateinit var timerUtil: Timer
    private lateinit var start: Button
    private var gameStarted: Boolean = false


    private lateinit var constraintLayout: ConstraintLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trail_making_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        constraintLayout = findViewById(R.id.constraintLayout)




        val back = findViewById<Button>(R.id.backButton)
        start = findViewById(R.id.startButton)

        timerText = findViewById(R.id.timeTextView)
        timerUtil = Timer(this)
        timerUtil.stopTimer()


        back.setOnClickListener{
            val intent = Intent(this, MainMenuFragment::class.java)
            startActivity(intent)
        }

        var game: Int = 1
        start.setOnClickListener{
            start.isEnabled = false
            toggleTimer()


            addItemsRandomly(20)

//            when(game){
//                1 -> partA()
//                2 -> partB()
//            }


        }

    }


    private fun addItemsRandomly(itemCount: Int) {
        val inflater = LayoutInflater.from(this)
        val placedItems = mutableSetOf<Pair<Int, Int>>()
        val itemSize = 60 // Width and height of the item

        for (i in 1 .. itemCount) {
            val itemView = inflater.inflate(R.layout.point_item, constraintLayout, false)

            // Set the text of the TextView
            val pointNumber = itemView.findViewById<TextView>(R.id.pointNumber)
            pointNumber.text = i.toString()

            var randomX: Int
            var randomY: Int
            var isOverlapping: Boolean

            do {
                randomX = Random.nextInt(constraintLayout.width - itemSize)
                randomY = Random.nextInt(constraintLayout.height - itemSize)
                isOverlapping = placedItems.any { (x, y) ->
                    Math.abs(x - randomX) < itemSize && Math.abs(y - randomY) < itemSize
                }
            } while (isOverlapping)

            placedItems.add(Pair(randomX, randomY))

            // Add the item to the ConstraintLayout
            itemView.id = View.generateViewId()
            constraintLayout.addView(itemView)

            // Apply constraints
            val constraintSet = ConstraintSet().apply {
                clone(constraintLayout)
                connect(itemView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, randomX)
                connect(itemView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, randomY)
                applyTo(constraintLayout)
            }
        }
    }




    @SuppressLint("InflateParams")
    private fun createPointView(text: String, context: Context): View {
        // Inflate the point item layout
        val pointView = LayoutInflater.from(context).inflate(R.layout.point_item, null)

        // Set the text on the TextView inside the point item layout
        val pointTextView: TextView = pointView.findViewById(R.id.pointNumber)
        pointTextView.text = text

        return pointView
    }


    // PART A
//    private fun partA(){
//        // amount of points = 25
//        val n = 25
//        val gridLayout: GridLayout = findViewById(R.id.grid)
//
//        // Set to keep track of occupied positions
//        val occupiedPositions = mutableSetOf<Pair<Int, Int>>()
//
//        for (i in 1..n){
//            var column: Int
//            var row: Int
//            var position: Pair<Int, Int>
//
//            do {
//                column = Random.nextInt(0, gridLayout.columnCount)
//                row = Random.nextInt(0,gridLayout.rowCount)
//                position = Pair(row, column)
//            } while (occupiedPositions.contains(position))
//
//            // Mark the position as occupied
//            occupiedPositions.add(position)
//
//            val pointView = createPointView(i.toString(), this)
//
//            val param = GridLayout.LayoutParams()
//            param.rowSpec = GridLayout.spec(row, 1)
//            param.columnSpec = GridLayout.spec(column, 1)
//            param.width = GridLayout.LayoutParams.WRAP_CONTENT
//            param.height = GridLayout.LayoutParams.WRAP_CONTENT
//
//            gridLayout.addView(pointView, param)
//
//        }
//    }

    // PART B
    // amount of points = 12 numbers and 12 letters
    private fun partB(){

    }

    private fun toggleTimer() {
        if (gameStarted) {
            timerUtil.stopTimer()
        } else {
            timerUtil.startTimer()
        }
        gameStarted = !gameStarted
    }


//    override fun onResume() {
//        super.onResume()
//        timerUtil.startTimer() // Start the timer when the activity resumes
//    }
//
//    override fun onPause() {
//        super.onPause()
//        timerUtil.stopTimer() // Stop the timer when the activity is paused
//    }

    override fun onTimeUpdate(time: String?) {
        timerText.text = time
    }
}