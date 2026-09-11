package com.pendshop.stockmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pendshop.stockmanager.ui.screens.cart.CartScreen
import com.pendshop.stockmanager.ui.screens.catalogue.AddEditProductScreen
import com.pendshop.stockmanager.ui.screens.catalogue.CatalogueScreen
import com.pendshop.stockmanager.ui.screens.reports.ReportsScreen
import com.pendshop.stockmanager.ui.screens.scan.ProductDetailScreen
import com.pendshop.stockmanager.ui.screens.scan.QrScanScreen
import com.pendshop.stockmanager.ui.screens.stock.StockDashboardScreen

object Routes {
    const val HOME = "home"
    const val CATALOGUE = "catalogue"
    const val ADD_EDIT_PRODUCT = "add_edit_product"
    const val SCAN = "scan"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val STOCK = "stock"
    const val REPORTS = "reports"

    fun productDetail(productId: String) = "product_detail/$productId"
}

@Composable
fun PendShopNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.CATALOGUE) {
            CatalogueScreen(navController)
        }
        composable(Routes.ADD_EDIT_PRODUCT) {
            AddEditProductScreen(navController)
        }
        composable(Routes.SCAN) {
            QrScanScreen(navController)
        }
        composable(Routes.PRODUCT_DETAIL) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController, productId)
        }
        composable(Routes.CART) {
            CartScreen(navController)
        }
        composable(Routes.STOCK) {
            StockDashboardScreen(navController)
        }
        composable(Routes.REPORTS) {
            ReportsScreen(navController)
        }
    }
}
