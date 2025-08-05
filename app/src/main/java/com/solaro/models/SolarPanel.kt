package com.yourcompany.solaro.models

data class SolarPanel(
    val id: Int = 0,
    val userId: Int,
    val panelName: String,
    val panelType: String? = null,
    val capacityKw: Double? = null,
    val installationDate: String? = null, // Using String for simplicity, consider LocalDate for production
    val lastMaintenanceDate: String? = null,
    val notes: String? = null,
    val imagePath: String? = null // Path to the image file
)