package com.example.cognitiveassesmenttest.ui.gameone

import Timer
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainMenuFragment
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class TrailMakingTestActivity : AppCompatActivity(), Timer.TimerUpdateListener  {
    private lateinit var timerText: TextView
    private lateinit var timerUtil: Timer
    private lateinit var start: Button
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var mImageView: ImageView
    private lateinit var timeScore : CharSequence

    private lateinit var mCanvas: Canvas
    private lateinit var mBitmap: Bitmap
    private val mPaint = Paint()

    private var gameStarted = false
    private var game = 1
    private var lastNumber: Any = 0

    private var mStartX = 0F
    private var mStartY = 0F
    private var mStopX = 0F
    private var mStopY = 0F

    private val placedItems = mutableListOf<Triple<Any, Int, Int>>()

    private var scoreA = 0
    private var scoreB = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trail_making_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.trail_test)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        constraintLayout = findViewById(R.id.constraintLayout)
        mImageView = findViewById(R.id.bitmapView)
        val back = findViewById<Button>(R.id.backButton)
        start = findViewById(R.id.startButton)

        var width : Int
        var height : Int

        constraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                width = constraintLayout.width
                height = constraintLayout.height
                Log.d("DIMENSIONSLAYOUT", "w: $width, h: $height")

                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mCanvas = Canvas(mBitmap)
                mCanvas.drawColor(Color.TRANSPARENT)
                mImageView.setImageBitmap(mBitmap)
            }
        })

        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 20F
        mPaint.isAntiAlias = true

        timerText = findViewById(R.id.timeTextView)
        timerUtil = Timer(this)
        timerUtil.stopTimer()

        back.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, MainMenuFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        start.setOnClickListener{
            val gameOne: List<Int> = (1..20).toList()
            val numbers: List<Int> = (1..10).toList()
            val letters: List<Char> = ('A'..'J').toList()
            val gameTwo: List<Any> = numbers.zip(letters).flatMap { (num, char) ->
                listOf(num, char)
            }

            start.isEnabled = false

            if(game%2 == 1){
                addItemsRandomly(gameOne)
            }else{
                addItemsRandomly(gameTwo)
            }
        }
    }

    private fun calculateScore(timeScore: CharSequence): Int{
        val timeParts = timeScore.split(":")
        val minutes = timeParts[0].toInt()
        val seconds = timeParts[1].toInt()
        val score = minutes * 60 + seconds

        return score
    }

    private fun addItemsRandomly(itemList: List<Any>) {
        val inflater = LayoutInflater.from(this)
        val itemSize = 100 // diameter
        val margin = 40
        val radius = (2 * itemSize) + margin

        for (i in itemList) {
            val itemView: View = if (i == 1){
                inflater.inflate(R.layout.start_point_item, constraintLayout, false)
            } else {
                inflater.inflate(R.layout.point_item, constraintLayout, false)
            }

            // Set the text of the TextView
            val pointNumber = itemView.findViewById<TextView>(R.id.pointNumber)
            pointNumber.text = i.toString()

            var randomX: Int
            var randomY: Int
            var isOverlapping: Boolean
            var tooFar: Boolean
            var tooClose: Boolean

            do {
                randomX = Random.nextInt(constraintLayout.width - radius)
                randomY = Random.nextInt(constraintLayout.height - radius)

                if(i != 1) {
                    val (_, x, y) = placedItems.last()

                    isOverlapping = placedItems.any { (_, x, y) ->
                        sqrt((randomX - x).toDouble().pow(2.0) + (randomY - y).toDouble().pow(2.0)) <= radius
                    }
                    tooFar = sqrt((randomX - x).toDouble().pow(2.0) + (randomY - y).toDouble().pow(2.0)) >= (10 * radius)
                    tooClose = sqrt((randomX - x).toDouble().pow(2.0) + (randomY - y).toDouble().pow(2.0)) >= (2 * radius)
                }else{
                    isOverlapping = false
                    tooFar = false
                    tooClose = false
                }

            } while (isOverlapping || tooFar || tooClose)

            placedItems.add(Triple(i, randomX, randomY))

            // Add the item to the ConstraintLayout
            itemView.id = View.generateViewId()
            constraintLayout.addView(itemView)

            // Apply constraints
            ConstraintSet().apply {
                clone(constraintLayout)
                connect(itemView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, randomX)
                connect(itemView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, randomY)
                applyTo(constraintLayout)
            }

            val item = if(i==1) {
                itemView.findViewById(R.id.startPointButton)
            }else{
                itemView.findViewById<ImageButton>(R.id.pointButton)
            }

            item.setOnClickListener {
                Log.d("CLICKEDBUTTON", "clidked: $i")
                onPointClicked(i, randomX.toFloat(), randomY.toFloat(), itemList)
            }
        }
    }

    private fun onPointClicked(pointNumber: Any, x: Float, y: Float, items: List<Any>) {
        if (pointNumber == 1 || isPointValid(pointNumber, lastNumber, items)) {
            Log.d("POINTCOORDINATES", "before assigned line start: x: $mStartX, y: $mStartY || line end: x: $mStopX, y: $mStopY")
            when (pointNumber) {
                1 -> {
                    toggleTimer()
                    Log.d("GAMESTATUS", "Game started")
                    mStartX = (x + 103.5).toFloat()
                    mStartY = (y + 103.5).toFloat()
                }
                in items.subList(1, items.size - 1) ->{
                    mStopX = (x + 103.5).toFloat()
                    mStopY = (y + 103.5).toFloat()

                    mCanvas.drawLine(mStartX, mStartY, mStopX, mStopY, mPaint)
                    Log.d("POINTCOORDINATES", "line start: x: $mStartX, y: $mStartY || line end: x: $mStopX, y: $mStopY")
                    mImageView.setImageBitmap(mBitmap)

                    mStartX = mStopX
                    mStartY = mStopY
                }
                items.last() -> {
                    mStopX = (x + 103.5).toFloat()
                    mStopY = (y + 103.5).toFloat()

                    mCanvas.drawLine(mStartX, mStartY, mStopX, mStopY, mPaint)
                    mImageView.setImageBitmap(mBitmap)

                    start.isEnabled = true
                    toggleTimer()
                    game++
                    timeScore = timerText.text.toString()

                    if (game%2 == 1){
                        scoreA = calculateScore(timeScore)
                    }else{
                        scoreB = calculateScore(timeScore)
                    }
                }
            }
            lastNumber = pointNumber
        }
    }

    private fun isPointValid(pointNumber: Any, lastNumber: Any, items: List<Any>): Boolean{
        val index = items.indexOf(pointNumber)
        return items[index-1] == lastNumber
    }

    private fun toggleTimer() {
        if (gameStarted) {
            timerUtil.stopTimer()
        } else {
            timerUtil.startTimer()
        }
        gameStarted = !gameStarted
    }

    override fun onTimeUpdate(time: String?) {
        timerText.text = time
    }

}

