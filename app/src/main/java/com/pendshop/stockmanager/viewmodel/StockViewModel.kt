package com.pendshop.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.data.model.Stock
import com.pendshop.stockmanager.data.model.StockLogType
import com.pendshop.stockmanager.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StockRow(val product: Product, val stock: Stock)

class StockViewModel(private val repo: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _rows = MutableStateFlow<List<StockRow>>(emptyList())
    val rows: StateFlow<List<StockRow>> = _rows.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val products = repo.getProducts()
            val stockList = repo.getAllStock().associateBy { it.productId }
            _rows.value = products.map { p ->
                StockRow(p, stockList[p.id] ?: Stock(productId = p.id))
            }
        }
    }

    fun lowStockRows(): List<StockRow> =
        _rows.value.filter { it.stock.bagsRemaining < it.product.lowStockThreshold }

    fun addPurchase(productId: String, bags: Int, note: String = "Purchase / stock-in") {
        viewModelScope.launch {
            repo.adjustStockBags(productId, bags, note, StockLogType.PURCHASE)
            refresh()
        }
    }

    fun adjustStock(productId: String, bagsDelta: Int, note: String) {
        viewModelScope.launch {
            repo.adjustStockBags(productId, bagsDelta, note, StockLogType.ADJUSTMENT)
            refresh()
        }
    }

    fun openBag(productId: String, bagWeightKg: Double) {
        viewModelScope.launch {
            repo.openBag(productId, bagWeightKg)
            refresh()
        }
    }
}
