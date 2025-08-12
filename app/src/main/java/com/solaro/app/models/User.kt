package com.solaro.app.models

data class User(
    val id: Long = 0, // 0 for new users before insertion, actual ID from DB
    val username: String,
    val email: String,
    val passwordHash: String, // Storing hashed password
    val userType: String, // "admin" or "user"
    val address: String? = null
)