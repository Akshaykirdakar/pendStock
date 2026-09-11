package com.pendshop.stockmanager.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pendshop.stockmanager.viewmodel.AuthViewModel

@Composable
fun HomeScreen(navController: NavHostController, authVm: AuthViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pend Shop") },
                actions = {
                    TextButton(onClick = {
                        authVm.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }) { Text("Logout") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = { navController.navigate(Routes.SCAN) }, modifier = Modifier.fillMaxWidth()) {
                Text("Scan QR to Sell")
            }
            Button(onClick = { navController.navigate(Routes.CART) }, modifier = Modifier.fillMaxWidth()) {
                Text("View Cart / Checkout")
            }
            Button(onClick = { navController.navigate(Routes.CATALOGUE) }, modifier = Modifier.fillMaxWidth()) {
                Text("Catalogue (Brands & Products)")
            }
            Button(onClick = { navController.navigate(Routes.QR_CATALOGUE) }, modifier = Modifier.fillMaxWidth()) {
                Text("Print QR Catalogue")
            }
            Button(onClick = { navController.navigate(Routes.STOCK) }, modifier = Modifier.fillMaxWidth()) {
                Text("Stock Dashboard")
            }
            Button(onClick = { navController.navigate(Routes.REPORTS) }, modifier = Modifier.fillMaxWidth()) {
                Text("Reports")
            }
        }
    }
}
