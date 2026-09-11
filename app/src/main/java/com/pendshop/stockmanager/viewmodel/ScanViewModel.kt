package com.pendshop.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.data.model.Stock
import com.pendshop.stockmanager.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(private val repo: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _scannedProduct = MutableStateFlow<Product?>(null)
    val scannedProduct: StateFlow<Product?> = _scannedProduct.asStateFlow()

    private val _scannedStock = MutableStateFlow<Stock?>(null)
    val scannedStock: StateFlow<Stock?> = _scannedStock.asStateFlow()

    /** Called with the raw text decoded from the QR (expected to be the product's Firestore id). */
    fun onQrScanned(productId: String) {
        viewModelScope.launch {
            val product = repo.getProductById(productId)
            _scannedProduct.value = product
            _scannedStock.value = product?.let { repo.getStock(it.id) }
        }
    }

    fun clear() {
        _scannedProduct.value = null
        _scannedStock.value = null
    }
}
