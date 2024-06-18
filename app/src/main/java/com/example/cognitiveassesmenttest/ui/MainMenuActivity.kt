package com.example.cognitiveassesmenttest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.mmse.RepetitionActivity
import com.google.firebase.FirebaseApp

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)
        val gameOneButton = findViewById<Button>(R.id.gameOne)
        val gameTwoButton = findViewById<Button>(R.id.gameTwo)
        val gameThreeButton = findViewById<Button>(R.id.gameThree)

        gameOneButton.setOnClickListener {

        }

        gameTwoButton.setOnClickListener {
            val intent = Intent(this, RepetitionActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        gameThreeButton.setOnClickListener {

        }
    }
}