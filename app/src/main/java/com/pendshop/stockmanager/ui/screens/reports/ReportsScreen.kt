package com.pendshop.stockmanager.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.viewmodel.ReportsViewModel

@Composable
fun ReportsScreen(navController: NavHostController, vm: ReportsViewModel = viewModel()) {
    val bills by vm.todayBills.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Today's Report") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Total sales today: ₹${vm.todayTotal()}", style = MaterialTheme.typography.titleLarge)
            Text("${bills.size} bills", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(bills) { bill ->
                    ElevatedCard {
                        Column(Modifier.padding(12.dp)) {
                            Text("Bill: ${bill.id}", style = MaterialTheme.typography.labelMedium)
                            Text("Customer: ${bill.customerName.ifBlank { "Walk-in" }}")
                            Text("Payment: ${bill.paymentMode}")
                            Text("Total: ₹${bill.totalAmount}", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }
    }
}
