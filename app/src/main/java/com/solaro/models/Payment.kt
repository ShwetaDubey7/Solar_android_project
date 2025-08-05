package com.solaro.models.payment

data class Payment(
    val id: Int = 0,
    val userId: Int,
    val amount: Double,
    val paymentDate: String, // Using String for simplicity
    val serviceDescription: String? = null
)