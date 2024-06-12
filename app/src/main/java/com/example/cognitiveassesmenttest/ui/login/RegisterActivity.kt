package com.example.cognitiveassesmenttest.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cognitiveassesmenttest.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Activity for the register screen.
 */
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    /**
     * Creates the view for the register screen.
     * @param savedInstanceState The saved instance state.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val signin = findViewById<Button>(R.id.signInButton)
        val inputEmail = findViewById<TextView>(R.id.emailInput)
        val inputUsername = findViewById<TextView>(R.id.usernameInput)
        val inputPassword = findViewById<TextView>(R.id.passwordInput)
        val inputPasswordRep = findViewById<TextView>(R.id.repPasswordInput)
        val login = findViewById<TextView>(R.id.login)


        auth = Firebase.auth

        // Login button clicked action
        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Sign up button clicked action
        signin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val username = inputUsername.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            val repeatedPassword = inputPasswordRep.text.toString().trim()

            if (verifyData(email, username, password, repeatedPassword)) {
                createUser(email, password)
            }
        }
    }


    /**
     * Verifies the data entered by the user.
     * @param email The user's email.
     * @param username The user's username.
     * @param password The user's password.
     * @param repeatedPassword The user's repeated password.
     * @return True if the data is correct, false otherwise.
     */
    private fun verifyData(
        email: String, username: String, password: String, repeatedPassword: String
    ): Boolean {
        var result = true
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            result = false
        }
        if (password != repeatedPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            result = false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT)
                .show()
            result = false
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
            result = false
        }
        var hasDigit = false
        var hasLetter = false
        var hasSpecialChar = false
        val specialChars =
            charArrayOf('-', '_', '.', '@', '!', '#', '$', '%', '^', '&', '*', '(', ')')

        for (char in password) {
            if (specialChars.contains(char)) hasSpecialChar = true
            if (char.isDigit()) hasDigit = true
            if (char.isLetter()) hasLetter = true
        }
        if (!hasDigit || !hasLetter) {
            Toast.makeText(
                this,
                "Password must contain at least one letter and one digit",
                Toast.LENGTH_SHORT
            ).show()
            result = false
        }
        if (!hasSpecialChar) {
            Toast.makeText(
                this,
                "Password must contain at least one special character",
                Toast.LENGTH_SHORT
            ).show()
            result = false
        }
        return result
    }

    /**
     * Creates a user with the given email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    runBlocking {
                        launch(Dispatchers.IO) {
                            // TODO: addUserToDatabase()
                        }
                    }

                    Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(this, "User with this email already exists.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    /** TODO:
    /**
     * Adds the user to the database.
    */
    private suspend fun addUserToDatabase() {
    return withContext(Dispatchers.IO) {
    val connection = DBconnection.getConnection()
    val user = Users(
    nameInput.text.toString(),
    lastNameInput.text.toString(),
    emailInput.text.toString()
    )
    val userQueries = UsersQueries(connection)
    userQueries.addUser(user)
    }
    }

     */

}
