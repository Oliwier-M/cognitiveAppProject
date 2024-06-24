package com.example.cognitiveassesmenttest.ui.user

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

    private fun enableEditing(enable: Boolean) {
        username.isEnabled = enable
        name.isEnabled = enable
        surname.isEnabled = enable
        email.isEnabled = enable
        editButton.visibility = if (enable) View.GONE else View.VISIBLE
        updateButton.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun updateUserProfile() {
        val database = Firebase.database
        val userRef = database.getReference("users").child(Firebase.auth.currentUser!!.uid)
        val updatedUser = User(
            username = username.text.toString(),
            name = name.text.toString(),
            surname = surname.text.toString(),
            email = email.text.toString()
        )
        userRef.setValue(updatedUser)
        enableEditing(false)
    }

    private fun deleteUserFromDatabase() {
        val database = Firebase.database
        val userRef = database.getReference("users").child(Firebase.auth.currentUser!!.uid)
        userRef.removeValue()
        deleteScoresForUserId()
        Firebase.auth.currentUser!!.delete()
    }

    private fun deleteScoresForUserId() {
        val database = Firebase.database
        val scoresRef = database.getReference("scores").child(Firebase.auth.currentUser!!.uid)
        scoresRef.removeValue()
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

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
