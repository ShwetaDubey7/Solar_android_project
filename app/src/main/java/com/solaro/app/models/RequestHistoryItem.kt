package com.solaro.app.models

data class RequestHistoryItem(
    val id: Long,
    val type: String, // "Installation" or "Maintenance"
    val description: String,
    val status: String
)