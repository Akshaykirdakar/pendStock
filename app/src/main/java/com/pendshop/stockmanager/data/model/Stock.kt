package com.pendshop.stockmanager.data.model

data class Stock(
    val productId: String = "",
    val bagsRemaining: Int = 0,
    val looseKgRemaining: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)
