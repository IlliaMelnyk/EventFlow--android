package com.example.eventflow.image

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object FileUtil {
    fun from(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, UUID.randomUUID().toString() + ".jpg")
            val outputStream = file.outputStream()
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}