package com.example.cognitiveassesmenttest.ui.user

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cognitiveassesmenttest.R
import com.example.cognitiveassesmenttest.ui.MainActivity
import com.example.cognitiveassesmenttest.ui.db.User
import com.example.cognitiveassesmenttest.ui.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


/**
 * Profile activity displays user data and enables managing the data.
 *
 * @constructor Create Profile activity
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var email: EditText
    private lateinit var logoutButton: Button
    private lateinit var deleteButton: Button
    private lateinit var menuButton: Button
    private lateinit var editButton: Button
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        username = findViewById(R.id.usernameView)
        name = findViewById(R.id.nameView)
        surname = findViewById(R.id.surnameView)
        email = findViewById(R.id.emailView)
        logoutButton = findViewById(R.id.logout)
        deleteButton = findViewById(R.id.delete)
        menuButton = findViewById(R.id.menu)
        editButton = findViewById(R.id.edit)
        updateButton = findViewById(R.id.update)
        fetchUserData()

        logoutButton.setOnClickListener {
            logoutDialog()
        }

        deleteButton.setOnClickListener {
            alertDialog()
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        editButton.setOnClickListener {
            enableEditing(true)
        }

        updateButton.setOnClickListener {
            updateUserProfile()
        }
    }

    /**
     * Fetch user data from the realtime database
     *
     */
    private fun fetchUserData() {
        val database = Firebase.database
        val userRef = database.getReference("users").child(Firebase.auth.currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    username.setText(user.username)
                    name.setText(user.name)
                    surname.setText(user.surname)
                    email.setText(user.email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * Enable editing of textFields of user's data.
     *
     * @param enable
     */
    private fun enableEditing(enable: Boolean) {
        username.isEnabled = enable
        name.isEnabled = enable
        surname.isEnabled = enable
        email.isEnabled = enable
        editButton.visibility = if (enable) View.GONE else View.VISIBLE
        updateButton.visibility = if (enable) View.VISIBLE else View.GONE
    }

    /**
     * Update user profile in the database, only if changes are provided.
     */
    private fun updateUserProfile() {
        val newUsername = username.text.toString()
        val newName = name.text.toString()
        val newSurname = surname.text.toString()
        val newEmail = email.text.toString()

        val originalUser = User(
            username = username.hint.toString(),
            name = name.hint.toString(),
            surname = surname.hint.toString(),
            email = email.hint.toString()
        )

        if (newUsername == originalUser.username &&
            newName == originalUser.name &&
            newSurname == originalUser.surname &&
            newEmail == originalUser.email
        ) {
            return
        }

        val user = Firebase.auth.currentUser
        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                } else {
                    Log.e(TAG, "Failed to update email address.", task.exception)
                }
            }

        val database = Firebase.database
        val userRef = database.getReference("users").child(user!!.uid)
        val updatedUser = User(
            username = newUsername,
            name = newName,
            surname = newSurname,
            email = newEmail
        )
        userRef.setValue(updatedUser)
        enableEditing(false)
    }


    /**
     * Delete user from database and authentication
     *
     */
    private fun deleteUserFromDatabase() {
        val database = Firebase.database
        val userRef = database.getReference("users").child(Firebase.auth.currentUser!!.uid)
        userRef.removeValue()
        deleteScoresForUserId()
        Firebase.auth.currentUser!!.delete()
    }

    /**
     * Delete scores for user id from the database
     *
     */
    private fun deleteScoresForUserId() {
        val database = Firebase.database
        val scoresRef = database.getReference("scores").child(Firebase.auth.currentUser!!.uid)
        scoresRef.removeValue()
    }

    /**
     * Move to login activity by intent
     *
     */
    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Alert dialog for deleting a user with confirmation
     *
     */
    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account?")
        builder.setPositiveButton("Yes") { dialog, which ->
            deleteUserFromDatabase()
            moveToLoginActivity()
        }
        builder.setNegativeButton("No") { dialog, which -> }
        builder.show()
    }

    /**
     * Logout dialog with confirmation for logging out
     *
     */
    private fun logoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which ->
            Firebase.auth.signOut()
            moveToLoginActivity()
        }
        builder.setNegativeButton("No") { dialog, which -> }
        builder.show()
    }

}
