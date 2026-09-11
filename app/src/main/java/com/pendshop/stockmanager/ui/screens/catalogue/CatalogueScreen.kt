package com.pendshop.stockmanager.ui.screens.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.ui.navigation.Routes
import com.pendshop.stockmanager.viewmodel.CatalogueViewModel

@Composable
fun CatalogueScreen(navController: NavHostController, vm: CatalogueViewModel = viewModel()) {
    val products by vm.products.collectAsState()
    val brands by vm.brands.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catalogue") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ADD_EDIT_PRODUCT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            Text("${brands.size} brands, ${products.size} products", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products) { product ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(product.brandName, style = MaterialTheme.typography.labelMedium)
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text("${product.bagWeightKg}kg bag  |  Bag ₹${product.fullBagPrice}  |  ₹${product.perKgPrice}/kg")
                        }
                    }
                }
            }
        }
    }
}
