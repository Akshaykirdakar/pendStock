package com.pendshop.stockmanager.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorageUploader {

    /** Uploads a local image Uri (from the gallery picker) to Firebase Storage and
     * returns the public download URL to store on the Product document. */
    suspend fun uploadProductPhoto(localUri: Uri): String {
        val storage = FirebaseStorage.getInstance()
        val fileName = "product_photos/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(localUri).await()
        return ref.downloadUrl.await().toString()
    }
}
