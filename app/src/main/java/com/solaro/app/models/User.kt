package com.solaro.app.models

data class User(
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val userType: String // "admin" or "user"
)