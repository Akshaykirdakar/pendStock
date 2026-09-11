package com.pendshop.stockmanager.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QrShareHelper {

    /** Saves the QR bitmap to the app's cache dir and returns a share Intent for it,
     * so the shop owner can send it to a printer app, WhatsApp, or Google Drive. */
    fun shareQrBitmap(context: Context, bitmap: Bitmap, label: String) {
        val cacheDir = File(context.cacheDir, "qr_codes").apply { mkdirs() }
        val file = File(cacheDir, "${label.replace(" ", "_")}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share/Print QR - $label"))
    }
}
