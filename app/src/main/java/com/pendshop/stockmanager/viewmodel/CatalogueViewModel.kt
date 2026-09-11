package com.pendshop.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pendshop.stockmanager.data.model.Brand
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogueViewModel(private val repo: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _brands.value = repo.getBrands()
            _products.value = repo.getProducts()
        }
    }

    fun addBrand(name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.addBrand(name)
            refresh()
            onDone()
        }
    }

    fun saveProduct(product: Product, onDone: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.addOrUpdateProduct(product)
            refresh()
            onDone(id)
        }
    }
}
