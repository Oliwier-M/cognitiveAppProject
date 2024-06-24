package com.example.cognitiveassesmenttest.ui.db

data class User(
    var userId: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var email: String? = null,
    var username: String? = null
) {
    constructor() : this(null, null, null, null, null)
}
