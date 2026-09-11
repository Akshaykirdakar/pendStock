package com.pendshop.stockmanager.data.model

enum class PaymentMode { CASH, UPI, CREDIT }

data class Bill(
    val id: String = "",
    val billNumber: Long = 0L,
    val customerName: String = "",
    val totalAmount: Double = 0.0,
    val paymentMode: String = PaymentMode.CASH.name,
    val createdAt: Long = System.currentTimeMillis(),
    val items: List<BillItem> = emptyList() // populated when reading; stored as a subcollection in Firestore
)
