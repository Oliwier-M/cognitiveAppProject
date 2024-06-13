package com.example.cognitiveassesmenttest.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cognitiveassesmenttest.ui.MainMenuActivity
import com.example.cognitiveassesmenttest.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity for the login screen.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    /**
     * Creates the view for the login screen.
     * @param savedInstanceState The saved instance state.
     */

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val login = findViewById<Button>(R.id.loginButton)
        val inputEmail = findViewById<TextView>(R.id.emailText)
        val inputPassword = findViewById<TextView>(R.id.passwordText)
        val register = findViewById<TextView>(R.id.RegisterButton)

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            userVerification(email, password)
        }
    }

    /**
     * Verifies the user's email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    private fun userVerification(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    val intent = Intent(this, MainMenuActivity::class.java)
                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                    intent.putExtra("email", email)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed. Check your email and password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

