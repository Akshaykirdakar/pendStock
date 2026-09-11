package com.pendshop.stockmanager.data.model

data class Product(
    val id: String = "",
    val brandId: String = "",
    val brandName: String = "",       // denormalized for easy display without a join
    val name: String = "",             // variety/description, e.g. "Cattle Feed Premium"
    val bagWeightKg: Double = 0.0,
    val photoUrl: String = "",
    val fullBagPrice: Double = 0.0,
    val perKgPrice: Double = 0.0,
    val lowStockThreshold: Int = 5,    // in number of bags
    val qrCode: String = "",           // usually just the productId, encoded into the QR image
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
