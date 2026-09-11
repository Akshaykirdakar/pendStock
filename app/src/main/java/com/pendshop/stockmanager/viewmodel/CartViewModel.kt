package com.pendshop.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pendshop.stockmanager.data.model.BillItem
import com.pendshop.stockmanager.data.model.PaymentMode
import com.pendshop.stockmanager.data.repository.CartRepository
import com.pendshop.stockmanager.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repo: FirestoreRepository = FirestoreRepository(),
    private val cart: CartRepository = CartRepository()
) : ViewModel() {

    val items: StateFlow<List<BillItem>> = cart.items

    var lastReceiptItems: List<BillItem> = emptyList()
        private set
    var lastReceiptTotal: Double = 0.0
        private set

    fun addItem(item: BillItem) = cart.addItem(item)
    fun removeItem(itemId: String) = cart.removeItem(itemId)
    fun updateItem(item: BillItem) = cart.updateItem(item)
    fun total(): Double = cart.total()

    fun checkout(
        customerName: String,
        paymentMode: PaymentMode,
        onSuccess: (billId: String, billNumber: Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentItems = items.value
        if (currentItems.isEmpty()) {
            onError("Cart is empty")
            return
        }
        viewModelScope.launch {
            try {
                val (billId, billNumber) = repo.checkoutBill(customerName, paymentMode, currentItems)
                lastReceiptItems = currentItems
                lastReceiptTotal = currentItems.sumOf { it.lineTotal }
                cart.clear()
                onSuccess(billId, billNumber)
            } catch (e: Exception) {
                onError(e.message ?: "Checkout failed")
            }
        }
    }
}
