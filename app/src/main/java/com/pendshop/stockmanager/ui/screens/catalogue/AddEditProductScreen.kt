package com.pendshop.stockmanager.ui.screens.catalogue

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import coil.compose.AsyncImage
import com.pendshop.stockmanager.data.model.Brand
import com.pendshop.stockmanager.data.model.Product
import com.pendshop.stockmanager.util.StorageUploader
import com.pendshop.stockmanager.viewmodel.CatalogueViewModel
import kotlinx.coroutines.launch

@Composable
fun AddEditProductScreen(navController: NavHostController, vm: CatalogueViewModel = viewModel()) {
    val brands by vm.brands.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    var newBrandName by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var bagWeight by remember { mutableStateOf("") }
    var fullBagPrice by remember { mutableStateOf("") }
    var perKgPrice by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("5") }

    var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedPhotoUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            localPhotoUri = uri
            isUploading = true
            coroutineScope.launch {
                try {
                    uploadedPhotoUrl = StorageUploader.uploadProductPhoto(uri)
                } finally {
                    isUploading = false
                }
            }
        }
    }

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

            Text("Product Photo", style = MaterialTheme.typography.titleSmall)
            if (localPhotoUri != null) {
                AsyncImage(model = localPhotoUri, contentDescription = "Selected photo", modifier = Modifier.fillMaxWidth().height(160.dp))
            }
            OutlinedButton(onClick = { photoPickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (localPhotoUri == null) "Choose Photo" else "Change Photo")
            }
            if (isUploading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Uploading photo...", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    val brand = selectedBrand ?: return@Button
                    isSaving = true
                    val product = Product(
                        brandId = brand.id,
                        brandName = brand.name,
                        name = productName,
                        bagWeightKg = bagWeight.toDoubleOrNull() ?: 0.0,
                        photoUrl = uploadedPhotoUrl,
                        fullBagPrice = fullBagPrice.toDoubleOrNull() ?: 0.0,
                        perKgPrice = perKgPrice.toDoubleOrNull() ?: 0.0,
                        lowStockThreshold = lowStockThreshold.toIntOrNull() ?: 5
                    )
                    vm.saveProduct(product) {
                        isSaving = false
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedBrand != null && productName.isNotBlank() && !isUploading && !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save Product & Generate QR")
            }
        }
    }
}
