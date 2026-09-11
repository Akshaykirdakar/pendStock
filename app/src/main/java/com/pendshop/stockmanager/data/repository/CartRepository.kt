package com.pendshop.stockmanager.data.repository

import com.pendshop.stockmanager.data.model.BillItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory cart, cleared after checkout. Kept as its own class (not a ViewModel)
 * so it can be shared across screens via a single instance if needed, but in this
 * skeleton it's held inside CartViewModel.
 */
class CartRepository {
    private val _items = MutableStateFlow<List<BillItem>>(emptyList())
    val items: StateFlow<List<BillItem>> = _items.asStateFlow()

    fun addItem(item: BillItem) {
        _items.value = _items.value + item
    }

    fun removeItem(itemId: String) {
        _items.value = _items.value.filterNot { it.id == itemId }
    }

    fun updateItem(updated: BillItem) {
        _items.value = _items.value.map { if (it.id == updated.id) updated else it }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun total(): Double = _items.value.sumOf { it.lineTotal }
}
