package com.example.cognitiveassesmenttest.ui.mmse

import android.content.Intent
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.cognitiveassesmenttest.R

class DrawingActivity : AppCompatActivity() {
    private var sentenceInput: EditText? = null
    private var checkSentenceButton: Button? = null
    private var checkDrawingButton: Button? = null
    private var pentagon1: ImageView? = null
    private var pentagon2: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_drawing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sentenceInput = findViewById(R.id.sentenceInput)
        checkSentenceButton = findViewById(R.id.checkSentenceButton)
        checkDrawingButton = findViewById(R.id.checkDrawingButton)
        pentagon1 = findViewById(R.id.pentagon1)
        pentagon2 = findViewById(R.id.pentagon2)

        pentagon1?.isVisible = false
        pentagon2?.isVisible = false
        checkDrawingButton?.isVisible = false

        var finalScore = intent.getIntExtra("score", 0)

        checkSentenceButton?.setOnClickListener {
            val sentence = sentenceInput?.text.toString()

            if (sentence.equals("Wroclaw", ignoreCase = true)) {
                Toast.makeText(this, "Answer is valid", Toast.LENGTH_SHORT).show()
                finalScore += 1
            } else {
                Toast.makeText(this, "Answer is not valid", Toast.LENGTH_SHORT).show()
            }
            pentagon1?.isVisible = true
            pentagon2?.isVisible = true
            checkDrawingButton?.isVisible = true
        }


        checkDrawingButton?.setOnClickListener {
            if (checkIntersectingPentagons()) {
                Toast.makeText(this, "Pentagons are valid", Toast.LENGTH_SHORT).show()
                finalScore += 1

                val handler = android.os.Handler()
                val intent = Intent(this, RepeatingActivity::class.java)
                intent.putExtra("score", finalScore)
                handler.postDelayed({
                    startActivity(intent)
                    finish()
                }, 2000)

            } else {
                Toast.makeText(this, "Pentagons are not valid", Toast.LENGTH_SHORT).show()
            }
        }

        setHorizontalTouchListener(pentagon2!!)
    }

    private fun setHorizontalTouchListener(imageView: ImageView) {
        imageView.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .setDuration(0)
                            .start()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        return true
                    }
                    else -> return false
                }
            }
        })
    }

    private fun checkIntersectingPentagons(): Boolean {

        val rect1 = Rect()
        pentagon1?.getHitRect(rect1)
        val rect2 = Rect()
        pentagon2?.getHitRect(rect2)

        return checkEmbeddedFigure(rect1, rect2)
    }


    private fun checkEmbeddedFigure(rect1: Rect, rect2: Rect): Boolean {
        val intersectionRect = RectF()

        if (intersectionRect.setIntersect(RectF(rect1), RectF(rect2))) {
            val left = intersectionRect.left
            val right = intersectionRect.right

            if(left < rect1.right - 55 && left > rect1.left + 50 && right > rect2.left + 55 && right < rect2.right - 50) {
                return true
            }
        }
        return false
    }
}
