package com.example.cognitiveassesmenttest.ui.gameone

import Timer
import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.ui.MainMenuFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
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
    private var size = 0

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


        constraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                CoroutineScope(Dispatchers.IO).launch {
                    val width = constraintLayout.width
                    val height = constraintLayout.height
                    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    mCanvas = Canvas(mBitmap)
                    mCanvas.drawColor(Color.TRANSPARENT)

                    withContext(Dispatchers.Main) {
                        mImageView.setImageBitmap(mBitmap)
                    }
                }
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//            supportFragmentManager.beginTransaction().apply {  }
//                .replace(R.id.nav_host_fragment_content_main, MainMenuFragment())
//                .addToBackStack(null)
//                .commit()
        }

        start.setOnClickListener{
            start.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                val gameOne: List<Int> = (1..20).toList()
                val numbers: List<Int> = (1..10).toList()
                val letters: List<Char> = ('A'..'J').toList()
                val gameTwo: List<Any> = numbers.zip(letters).flatMap { (num, char) ->
                    listOf(num, char)
                }

                val items = if (game % 2 == 1) gameOne else gameTwo
                addItemsRandomly(items)

                withContext(Dispatchers.Main) {
                    start.isEnabled = true
                }
            }
        }
    }

    private fun calculateScore(timeScore: CharSequence): Int{
        val timeParts = timeScore.split(":")
        val minutes = timeParts[0].toInt()
        val seconds = timeParts[1].toInt()
        val score = minutes * 60 + seconds

        // TODO: save into firebase straight from here
        return score
    }

    private fun addItemsRandomly(itemList: List<Any>) {
        val inflater = LayoutInflater.from(this)

        for (i in itemList) {
            val itemView: View = if (i == 1){
                inflater.inflate(R.layout.start_point_item, constraintLayout, false)
            } else {
                inflater.inflate(R.layout.point_item, constraintLayout, false)
            }

            val item = if(i==1) {
                itemView.findViewById(R.id.startPointButton)
            }else{
                itemView.findViewById<ImageButton>(R.id.pointButton)
            }

            item.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    item.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        size = item.width
                        Log.d("ITEMSIZE", "point size: $size")
                    }
                }
            })

            // Set the text of the TextView
            val pointNumber = itemView.findViewById<TextView>(R.id.pointNumber)
            pointNumber.text = i.toString()

            var attempts = 0
            var m = 2.2

            var randomX: Int
            var randomY: Int
            var tooFar: Boolean
            var tooClose: Boolean
            var onLine: Boolean

            do {
                attempts++
                if(attempts > 100){
                    m += 0.1
                    attempts = 0
                }

                val d = size * sqrt(2.0)

                if(i== 1){
                    randomX = Random.nextInt(constraintLayout.width/2)
                    randomY = Random.nextInt(constraintLayout.height/2)
                }else {
                    randomX = Random.nextInt(constraintLayout.width - (1.5 * size).toInt())
                    randomY = Random.nextInt(constraintLayout.height - (1.5 * size).toInt())
                }

                if(i != 1) {
                    val (_, x, y) = placedItems.last()

                    tooClose = placedItems.any { (_, x, y) ->
                        sqrt((randomX - x).toDouble().pow(2.0) + (randomY - y).toDouble().pow(2.0)) <= d
                    }
                    tooFar = sqrt((randomX - x).toDouble().pow(2.0) + (randomY - y).toDouble().pow(2.0)) >= (m * d)
                }else{
                    tooFar = false
                    tooClose = false
                }

            } while (tooFar || tooClose)

            placedItems.add(Triple(i, randomX, randomY))
            m = 2.2

            runOnUiThread {
                itemView.id = View.generateViewId()
                constraintLayout.addView(itemView)

                // Apply constraints on the UI thread
                ConstraintSet().apply {
                    clone(constraintLayout)
                    connect(itemView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, randomX)
                    connect(itemView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, randomY)
                    applyTo(constraintLayout)
                }
            }

            item.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("CLICKEDBUTTON", "clidked: $i")
                    onPointClicked(i, randomX.toFloat(), randomY.toFloat(), itemList, size)
                }
            }
        }
    }

    private suspend  fun onPointClicked(pointNumber: Any, x: Float, y: Float, items: List<Any>, size: Int) {
        val d = size/2
        if (pointNumber == 1 || isPointValid(pointNumber, lastNumber, items)) {
            Log.d("POINTCOORDINATES", "before assigned line start: x: $mStartX, y: $mStartY || line end: x: $mStopX, y: $mStopY")
            when (pointNumber) {
                1 -> {
                    toggleTimer()
                    Log.d("GAMESTATUS", "Game started")
                    mStartX = (x + d)
                    mStartY = (y + d)
                }
                in items.subList(1, items.size - 1) ->{
                    mStopX = (x + d)
                    mStopY = (y + d)

                    drawLineOnCanvas(mStartX, mStartY, mStopX, mStopY)

                    mStartX = mStopX
                    mStartY = mStopY
                }
                items.last() -> {
                    mStopX = (x + d)
                    mStopY = (y + d)

                    drawLineOnCanvas(mStartX, mStartY, mStopX, mStopY)

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

    private suspend fun drawLineOnCanvas(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        withContext(Dispatchers.IO) {
            mCanvas.drawLine(startX, startY, stopX, stopY, mPaint)
        }
        withContext(Dispatchers.Main) {
            mImageView.setImageBitmap(mBitmap)
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
        runOnUiThread {
            timerText.text = time
        }
    }

}

