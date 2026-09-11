package com.pendshop.stockmanager.data.model

enum class SaleType { BAG, KG }

data class BillItem(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",     // denormalized for display on the bill
    val brandName: String = "",
    val saleType: String = SaleType.BAG.name,
    val quantityOrWeight: Double = 0.0,
    val rate: Double = 0.0,
    val lineTotal: Double = 0.0,
    val isPriceOverridden: Boolean = false
)
