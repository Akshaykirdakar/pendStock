package com.pendshop.stockmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pendshop.stockmanager.ui.navigation.PendShopNavGraph
import com.pendshop.stockmanager.ui.theme.PendShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PendShopTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PendShopNavGraph()
                }
            }
        }
    }
}
