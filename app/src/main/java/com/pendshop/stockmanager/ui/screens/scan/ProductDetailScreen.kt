package com.pendshop.stockmanager.ui.screens.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pendshop.stockmanager.data.model.BillItem
import com.pendshop.stockmanager.data.model.SaleType
import com.pendshop.stockmanager.ui.navigation.Routes
import com.pendshop.stockmanager.viewmodel.CartViewModel
import com.pendshop.stockmanager.viewmodel.ScanViewModel
import java.util.UUID

/**
 * Shown right after a QR scan. Lets the user choose Full Bag or By Weight (kg),
 * optionally override the price (per FR3.3a), and add the line to the cart.
 */
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: String,
    scanVm: ScanViewModel = viewModel(),
    cartVm: CartViewModel = viewModel()
) {
    LaunchedEffect(productId) {
        if (productId.isNotBlank()) scanVm.onQrScanned(productId)
    }

    val product by scanVm.scannedProduct.collectAsState()
    val stock by scanVm.scannedStock.collectAsState()

    var saleType by remember { mutableStateOf(SaleType.BAG) }
    var quantityText by remember { mutableStateOf("1") }
    var rateText by remember { mutableStateOf("") }
    var priceOverridden by remember { mutableStateOf(false) }

    // Default the rate field whenever the product or sale type changes
    LaunchedEffect(product, saleType) {
        product?.let {
            rateText = if (saleType == SaleType.BAG) it.fullBagPrice.toString() else it.perKgPrice.toString()
            priceOverridden = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Product") }) }) { padding ->
        val p = product
        if (p == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Loading / product not found...")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(model = p.photoUrl, contentDescription = p.name, modifier = Modifier.fillMaxWidth().height(180.dp))

            Text(p.brandName, style = MaterialTheme.typography.labelLarge)
            Text(p.name, style = MaterialTheme.typography.titleLarge)
            Text("Bag weight: ${p.bagWeightKg} kg")
            Text("Bag price: ₹${p.fullBagPrice}   |   Per-kg: ₹${p.perKgPrice}")
            Text("In stock: ${stock?.bagsRemaining ?: 0} bags, ${stock?.looseKgRemaining ?: 0.0} kg loose")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = saleType == SaleType.BAG, onClick = { saleType = SaleType.BAG }, label = { Text("Full Bag") })
                FilterChip(selected = saleType == SaleType.KG, onClick = { saleType = SaleType.KG }, label = { Text("By Weight (kg)") })
            }

            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it },
                label = { Text(if (saleType == SaleType.BAG) "Number of bags" else "Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rateText,
                onValueChange = {
                    rateText = it
                    val defaultRate = if (saleType == SaleType.BAG) p.fullBagPrice else p.perKgPrice
                    priceOverridden = it.toDoubleOrNull() != defaultRate
                },
                label = { Text(if (saleType == SaleType.BAG) "Rate per bag (₹) — editable" else "Rate per kg (₹) — editable") },
                modifier = Modifier.fillMaxWidth()
            )
            if (priceOverridden) {
                Text("Price overridden from catalogue default", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 0.0
                    val rate = rateText.toDoubleOrNull() ?: 0.0
                    val item = BillItem(
                        id = UUID.randomUUID().toString(),
                        productId = p.id,
                        productName = p.name,
                        brandName = p.brandName,
                        saleType = saleType.name,
                        quantityOrWeight = qty,
                        rate = rate,
                        lineTotal = qty * rate,
                        isPriceOverridden = priceOverridden
                    )
                    cartVm.addItem(item)
                    navController.navigate(Routes.CART)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Cart")
            }
        }
    }
}
