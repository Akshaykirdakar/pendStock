package com.pendshop.stockmanager.ui.screens.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.data.model.Brand
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.viewmodel.CatalogueViewModel

/**
 * Add a new product to the catalogue. Photo upload to Firebase Storage is left as a
 * clearly-marked TODO — wire it to your preferred image picker + Storage upload call,
 * then set photoUrl before calling vm.saveProduct(...).
 */
@Composable
fun AddEditProductScreen(navController: NavHostController, vm: CatalogueViewModel = viewModel()) {
    val brands by vm.brands.collectAsState()

    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var newBrandName by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var bagWeight by remember { mutableStateOf("") }
    var fullBagPrice by remember { mutableStateOf("") }
    var perKgPrice by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("5") }
    // TODO: replace with real Firebase Storage URL once photo upload is wired up
    var photoUrl by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("Add Product") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Brand", style = MaterialTheme.typography.titleSmall)
            LazyColumn(modifier = Modifier.heightIn(max = 120.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(brands) { brand ->
                    FilterChip(
                        selected = selectedBrand?.id == brand.id,
                        onClick = { selectedBrand = brand },
                        label = { Text(brand.name) }
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newBrandName,
                    onValueChange = { newBrandName = it },
                    label = { Text("New brand name") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (newBrandName.isNotBlank()) {
                        vm.addBrand(newBrandName) { newBrandName = "" }
                    }
                }) { Text("Add") }
            }

            Divider()

            OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product / variety name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = bagWeight, onValueChange = { bagWeight = it }, label = { Text("Bag weight (kg)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fullBagPrice, onValueChange = { fullBagPrice = it }, label = { Text("Full bag price (₹)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = perKgPrice, onValueChange = { perKgPrice = it }, label = { Text("Per-kg price (₹)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lowStockThreshold, onValueChange = { lowStockThreshold = it }, label = { Text("Low stock alert threshold (bags)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    val brand = selectedBrand ?: return@Button
                    val product = Product(
                        brandId = brand.id,
                        brandName = brand.name,
                        name = productName,
                        bagWeightKg = bagWeight.toDoubleOrNull() ?: 0.0,
                        photoUrl = photoUrl,
                        fullBagPrice = fullBagPrice.toDoubleOrNull() ?: 0.0,
                        perKgPrice = perKgPrice.toDoubleOrNull() ?: 0.0,
                        lowStockThreshold = lowStockThreshold.toIntOrNull() ?: 5
                    )
                    vm.saveProduct(product) { navController.popBackStack() }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedBrand != null && productName.isNotBlank()
            ) {
                Text("Save Product & Generate QR")
            }
        }
    }
}
