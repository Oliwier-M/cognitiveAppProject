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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.ui.hrb.SentenceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * TrailMakingTestActivity is an activity that handles the Trail Making Test, which includes
 * drawing lines between sequentially numbered points as fast as possible.
 */
class TrailMakingTestActivity : AppCompatActivity(), Timer.TimerUpdateListener  {
    private lateinit var timerText: TextView
    private lateinit var timerUtil: Timer
    private lateinit var start: Button
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var mImageView: ImageView
    private lateinit var timeScore : CharSequence
    private lateinit var prompt: TextView

    private lateinit var mCanvas: Canvas
    private lateinit var mBitmap: Bitmap
    private val mPaint = Paint()

    private var gameStarted = false
    private var game = 0
    private var lastNumber: Any = 0
    private var size = 0

    private var mStartX = 0F
    private var mStartY = 0F
    private var mStopX = 0F
    private var mStopY = 0F

    private val placedItems = mutableListOf<Triple<Any, Int, Int>>()

    private var scoreA = 0
    private var scoreB = 0

    private var popupDialog: AlertDialog? = null
    private lateinit var popupTextView: TextView

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
        prompt = findViewById(R.id.prompt)

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

        showPopup("On this page are some numbers.\nBegin at 1 and draw a line from 1 to 2, 2 to 3, 3 to 4 and so on, in order, until you reach the end.\n" +
                "Draw your line as fast as you can.\nThis is a tutorial sample, ready?")

        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity((intent))
        }

        start.setOnClickListener{
            prompt.text = ""
            game++
            Log.d("GAMENUMBER", "$game")
            start.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                val tutorialOne: List<Int> = (1..6).toList()
                val gameOne: List<Int> = (1..20).toList()
                val tutnumbers: List<Int> = (1..3).toList()
                val tutletters: List<Char> = ('A'..'C').toList()
                val tutorialTwo: List<Any> = tutnumbers.zip(tutletters).flatMap { (num, char) ->
                    listOf(num, char)
                }
                val numbers: List<Int> = (1..10).toList()
                val letters: List<Char> = ('A'..'J').toList()
                val gameTwo: List<Any> = numbers.zip(letters).flatMap { (num, char) ->
                    listOf(num, char)
                }

                val items = when (game){
                    1 -> tutorialOne
                    2 -> gameOne
                    3 -> tutorialTwo
                    4 -> gameTwo
                    else -> {tutorialOne}
                }

                addItemsRandomly(items)

                withContext(Dispatchers.Main) {
                    start.isEnabled = true
                }
            }
        }
    }

    /**
     * Shows a popup dialog with the given text.
     *
     * @param text The text to be displayed in the popup.
     */
    private fun showPopup(text: String) {
        if (popupDialog == null) {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.popup_layout, null)
            popupTextView = view.findViewById(R.id.popupText)
            builder.setView(view)
            builder.setCancelable(true)
            popupDialog = builder.create()
        }
        popupTextView.text = text
        popupDialog?.show()
    }

    /**
     * Closes the popup dialog.
     *
     * @param view The view that triggers the close action.
     */
    fun onCloseButtonClick(view: View) {
        popupDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        popupDialog?.dismiss()
    }

    /**
     * Calculates the score based on the given time.
     *
     * @param timeScore The time taken to complete the task.
     */
    private fun calculateScore(timeScore: CharSequence){
        val timeParts = timeScore.split(":")
        val minutes = timeParts[0].toInt()
        val seconds = timeParts[1].toInt()
        val score = minutes * 60 + seconds

        when (game) {
            2 -> {
                scoreA = score
                resetCanvas()
            }
            4 -> {
                scoreB = score
                val handler = android.os.Handler()
                val intent = Intent(this, TrailResultActivity::class.java)
                intent.putExtra("scoreA", scoreA)
                intent.putExtra("scoreB", scoreB)
                handler.postDelayed({
                    startActivity(intent)
                    finish()
                }, 2000)
            }
            else -> {
                resetCanvas()
            }
        }

    }

    /**
     * Adds items randomly on the canvas.
     *
     * @param itemList The list of items to be added.
     */
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

            val pointNumber = itemView.findViewById<TextView>(R.id.pointNumber)
            pointNumber.text = i.toString()

            var attempts = 0
            var m = 2.2

            var randomX: Int
            var randomY: Int
            var tooFar: Boolean
            var tooClose: Boolean

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

            runOnUiThread {
                itemView.id = View.generateViewId()
                constraintLayout.addView(itemView)

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

    /**
     * Handles the click event on a point.
     *
     * @param pointNumber The number of the clicked point.
     * @param x The x-coordinate of the clicked point.
     * @param y The y-coordinate of the clicked point.
     * @param items The list of items.
     * @param size The size of the point.
     */
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
                    timeScore = timerText.text.toString()

                    calculateScore(timeScore)
                }
            }
            lastNumber = pointNumber
        }
    }

    /**
     * Draws a line on the canvas.
     *
     * @param startX The x-coordinate of the start point.
     * @param startY The y-coordinate of the start point.
     * @param stopX The x-coordinate of the end point.
     * @param stopY The y-coordinate of the end point.
     */
    private suspend fun drawLineOnCanvas(startX: Float, startY: Float, stopX: Float, stopY: Float) {
        withContext(Dispatchers.IO) {
            mCanvas.drawLine(startX, startY, stopX, stopY, mPaint)
        }
        withContext(Dispatchers.Main) {
            mImageView.setImageBitmap(mBitmap)
        }
    }

    /**
     * Checks if a point is valid - next point on the list.
     *
     * @param pointNumber The number of the point to check.
     * @param lastNumber The last number that was clicked.
     * @param items The list of items.
     * @return True if the point is valid, false otherwise.
     */
    private fun isPointValid(pointNumber: Any, lastNumber: Any, items: List<Any>): Boolean{
        val index = items.indexOf(pointNumber)
        return items[index-1] == lastNumber
    }

    /**
     * Toggles the timer.
     */
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

    /**
     * Resets the canvas for the Trail Making Test, clearing previous drawings and preparing for a new session.
     */
    @SuppressLint("SetTextI18n")
    private fun resetCanvas() {
        CoroutineScope(Dispatchers.IO).launch {
            mBitmap = Bitmap.createBitmap(constraintLayout.width, constraintLayout.height, Bitmap.Config.ARGB_8888)
            mCanvas.setBitmap(mBitmap)
            mCanvas.drawColor(Color.TRANSPARENT)
            placedItems.clear()

            withContext(Dispatchers.Main) {
                mImageView.setImageBitmap(mBitmap)
                constraintLayout.removeAllViews()
                constraintLayout.addView(mImageView)
                mPaint.color = Color.BLACK
                mPaint.style = Paint.Style.STROKE
                mPaint.strokeWidth = 20F
                mPaint.isAntiAlias = true
                timerUtil.resetTimer()
                timerText.text = "00:00"
                when(game){
                    1, 3 -> showPopup("Now the real test. Good luck!")
                    2 -> showPopup("On this page are some numbers and letters. Begin at 1 and draw a line from 1 to A, A to 2, 2 to B and so on, in order, until you reach the end.\n" +
                            "Draw your line as fast as you can.\nThis is a tutorial sample, ready?")
                }

            }
        }
    }
}

