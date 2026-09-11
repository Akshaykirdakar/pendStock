package com.pendshop.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pendshop.stockmanager.data.model.Bill
import com.pendshop.stockmanager.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class ReportsViewModel(private val repo: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _todayBills = MutableStateFlow<List<Bill>>(emptyList())
    val todayBills: StateFlow<List<Bill>> = _todayBills.asStateFlow()

    init { loadToday() }

    fun loadToday() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24 * 60 * 60 * 1000L

        viewModelScope.launch {
            _todayBills.value = repo.getBillsBetween(start, end)
        }
    }

    fun todayTotal(): Double = _todayBills.value.sumOf { it.totalAmount }
}
