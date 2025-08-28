package com.solaro.app.models

import java.io.Serializable

data class Product(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
) : Serializable // Serializable allows passing this object between activities
