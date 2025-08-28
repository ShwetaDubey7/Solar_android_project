package com.solaro.app.models

data class MaintenanceTicket(
    val id: Long = 0,
    val userId: Long,
    val description: String,
    val imagePath: String?, // This can be null if no image is attached
    val status: String
)