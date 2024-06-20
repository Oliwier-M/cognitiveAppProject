package com.example.cognitiveassesmenttest.ui.hrb

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.bumptech.glide.Glide
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.google.firebase.storage.FirebaseStorage

class NamingActivity : AppCompatActivity() {

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private lateinit var edit1: EditText
    private lateinit var edit2: EditText
    private lateinit var edit3: EditText
    private lateinit var edit4: EditText
    private lateinit var checkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_naming)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        imageView3 = findViewById(R.id.imageView3)
        imageView4 = findViewById(R.id.imageView4)
        edit1 = findViewById(R.id.editText1)
        edit2 = findViewById(R.id.editText2)
        edit3 = findViewById(R.id.editText3)
        edit4 = findViewById(R.id.editText4)
        checkButton = findViewById(R.id.checkButton)
        var finalScore = intent.getIntExtra("score", 0)

        val storage = FirebaseStorage.getInstance()

        val imagePaths = arrayOf(
            "objects/pencil.jpg",
            "objects/shoe.png",
            "objects/apple.jpg",
            "objects/watch.jpg"
        )

        val imageViews = arrayOf(imageView1, imageView2, imageView3, imageView4)

        for (i in imagePaths.indices) {
            val storageRef = storage.reference.child(imagePaths[i])
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(imageViews[i])
            }.addOnFailureListener {
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkButton.isEnabled = edit1.text.isNotEmpty() &&
                        edit2.text.isNotEmpty() &&
                        edit3.text.isNotEmpty() &&
                        edit4.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        edit1.addTextChangedListener(textWatcher)
        edit2.addTextChangedListener(textWatcher)
        edit3.addTextChangedListener(textWatcher)
        edit4.addTextChangedListener(textWatcher)

        checkButton.setOnClickListener {
            val score = checkAnswers()
            finalScore += score
            val handler = android.os.Handler()
            val intent = Intent(this, SentenceActivity::class.java)
            intent.putExtra("score", finalScore)
            handler.postDelayed({
                startActivity(intent)
                finish()
            }, 2000)
        }

        checkButton.isEnabled = false
    }

    private fun checkAnswers() : Int {
        val answers = listOf(
            Pair(edit1, "pencil"),
            Pair(edit2, "shoe"),
            Pair(edit3, "apple"),
            Pair(edit4, "watch")
        )
        var score = 0
        answers.forEach { (editText, correctAnswer) ->
            val userAnswer = editText.text.toString().trim().lowercase()
            if (userAnswer == correctAnswer) {
                score++
            }
        }
        return score
    }
}
