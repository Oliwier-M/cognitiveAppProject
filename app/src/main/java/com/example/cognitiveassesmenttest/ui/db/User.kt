package com.example.cognitiveassesmenttest.ui.db

/**
 * Data class representing a user.
 * @property userId The user's ID.
 * @property name The user's name.
 * @property surname The user's surname.
 * @property email The user's email.
 * @property username The user's username.
 */
data class User(
    var userId: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var email: String? = null,
    var username: String? = null
)