package com.solaro.app.models

data class MaintenanceTicket(
    val id: Long,
    val userId: Long,
    val description: String,
    val imagePath: String?,
    val status: String
)