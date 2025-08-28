package com.solaro.app.models

data class Installation(
    val id: Long = 0,
    val userId: Long,
    val address: String,
    val status: String
)