package com.pendshop.stockmanager.data.model

data class Brand(
    val id: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
