package com.pendshop.stockmanager

import android.app.Application
import com.google.firebase.FirebaseApp

class PendShopApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
