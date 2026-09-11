package com.pendshop.stockmanager.ui.screens.catalogue

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.util.QrCodeGenerator
import com.pendshop.stockmanager.util.QrShareHelper
import com.pendshop.stockmanager.viewmodel.CatalogueViewModel

/**
 * Shows every product with its generated QR code, so the owner can print or share
 * each one to build a physical catalogue sheet for the shop counter.
 */
@Composable
fun QrCatalogueScreen(navController: NavHostController, vm: CatalogueViewModel = viewModel()) {
    val products by vm.products.collectAsState()
    val context = LocalContext.current

    // Cache generated bitmaps so scrolling doesn't regenerate them every recomposition
    val bitmapCache = remember { mutableMapOf<String, Bitmap>() }

    Scaffold(topBar = { TopAppBar(title = { Text("QR Catalogue") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product: Product ->
                val bitmap = bitmapCache.getOrPut(product.id) { QrCodeGenerator.generate(product.id, 400) }
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR for ${product.name}",
                            modifier = Modifier.size(96.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.brandName, style = MaterialTheme.typography.labelMedium)
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text("${product.bagWeightKg}kg  |  ₹${product.fullBagPrice}/bag  |  ₹${product.perKgPrice}/kg")
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = {
                                QrShareHelper.shareQrBitmap(context, bitmap, "${product.brandName}_${product.name}")
                            }) {
                                Text("Share / Print this QR")
                            }
                        }
                    }
                }
            }
        }
    }
}
