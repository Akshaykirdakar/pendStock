package com.pendshop.stockmanager.ui.screens.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.viewmodel.StockViewModel

@Composable
fun StockDashboardScreen(navController: NavHostController, vm: StockViewModel = viewModel()) {
    val rows by vm.rows.collectAsState()
    val lowStock = vm.lowStockRows()

    Scaffold(topBar = { TopAppBar(title = { Text("Stock Dashboard") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {

            if (lowStock.isNotEmpty()) {
                ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Low Stock Alert (${lowStock.size})", style = MaterialTheme.typography.titleSmall)
                        lowStock.forEach { row ->
                            Text("${row.product.brandName} - ${row.product.name}: ${row.stock.bagsRemaining} bags left")
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Text("All Products", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(rows) { row ->
                    ElevatedCard {
                        Column(Modifier.padding(12.dp)) {
                            Text("${row.product.brandName} - ${row.product.name}", style = MaterialTheme.typography.titleSmall)
                            Text("Bags: ${row.stock.bagsRemaining}   Loose: ${row.stock.looseKgRemaining} kg")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { vm.addPurchase(row.product.id, 10) }) {
                                    Text("+10 bags (purchase)")
                                }
                                OutlinedButton(onClick = { vm.openBag(row.product.id, row.product.bagWeightKg) }) {
                                    Text("Open 1 bag")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
