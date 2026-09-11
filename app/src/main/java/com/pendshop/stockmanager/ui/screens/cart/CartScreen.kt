package com.pendshop.stockmanager.ui.screens.cart

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.data.model.PaymentMode
import com.pendshop.stockmanager.ui.navigation.Routes
import com.pendshop.stockmanager.viewmodel.CartViewModel

@Composable
fun CartScreen(navController: NavHostController, vm: CartViewModel = viewModel()) {
    val items by vm.items.collectAsState()
    val context = LocalContext.current

    var customerName by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lastBillNumber by remember { mutableStateOf<Long?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("Cart") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            if (items.isEmpty() && lastBillNumber == null) {
                Text("Cart is empty. Scan a product to add it.")
            } else if (items.isEmpty() && lastBillNumber != null) {
                // Just checked out — show a simple receipt with a share button
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bill #$lastBillNumber created", style = MaterialTheme.typography.titleLarge)
                        Text("Total: ₹${vm.lastReceiptTotal}")
                        vm.lastReceiptItems.forEach { item ->
                            Text("- ${item.brandName} ${item.productName}: ${item.quantityOrWeight} ${if (item.saleType == "BAG") "bag(s)" else "kg"} @ ₹${item.rate} = ₹${item.lineTotal}")
                        }
                        Button(
                            onClick = {
                                val receiptText = buildString {
                                    appendLine("Pend Shop - Bill #$lastBillNumber")
                                    vm.lastReceiptItems.forEach { item ->
                                        appendLine("${item.brandName} ${item.productName}: ${item.quantityOrWeight} ${if (item.saleType == "BAG") "bag(s)" else "kg"} @ ₹${item.rate} = ₹${item.lineTotal}")
                                    }
                                    appendLine("Total: ₹${vm.lastReceiptTotal}")
                                }
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, receiptText)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share receipt"))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Share Receipt (WhatsApp / SMS / Print)")
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { item ->
                        ElevatedCard {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("${item.brandName} - ${item.productName}", style = MaterialTheme.typography.titleSmall)
                                    Text("${item.quantityOrWeight} ${if (item.saleType == "BAG") "bag(s)" else "kg"} @ ₹${item.rate}")
                                    if (item.isPriceOverridden) {
                                        Text("Price edited", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text("₹${item.lineTotal}", style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = { vm.removeItem(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Total: ₹${vm.total()}", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer name (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PaymentMode.values().forEach { mode ->
                        FilterChip(selected = paymentMode == mode, onClick = { paymentMode = mode }, label = { Text(mode.name) })
                    }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        vm.checkout(
                            customerName = customerName,
                            paymentMode = paymentMode,
                            onSuccess = { _, billNumber -> lastBillNumber = billNumber; errorMessage = null },
                            onError = { msg -> errorMessage = msg }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Checkout")
                }

                errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {
                lastBillNumber = null
                navController.navigate(Routes.SCAN)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Scan Another Item")
            }
        }
    }
}
