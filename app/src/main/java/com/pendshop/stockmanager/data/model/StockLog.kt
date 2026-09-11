package com.pendshop.stockmanager.data.model

enum class StockLogType {
    PURCHASE, SALE, ADJUSTMENT, BAG_OPENED
}

data class StockLog(
    val id: String = "",
    val productId: String = "",
    val type: String = StockLogType.ADJUSTMENT.name,
    val quantity: Double = 0.0,        // bags for PURCHASE/ADJUSTMENT/BAG_OPENED, kg or bags for SALE
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
