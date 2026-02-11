package com.example.eventflow.image

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object EventImageHandler {

    private const val CLOUDINARY_UPLOAD_URL =
        "https://api.cloudinary.com/v1_1/dfwxjq49f/image/upload"
    private const val UPLOAD_PRESET = "eventflow_preset"

    suspend fun uploadImageToCloudinary(fileUri: Uri, getFile: (Uri) -> File?): String? {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile(fileUri) ?: return@withContext null

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build()

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(CLOUDINARY_UPLOAD_URL)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
                        .find(responseBody ?: "")?.groupValues?.get(1)
                        ?.replace("\\/", "/")
                    Log.e("Cloudinary", "Extracted secure URL: $secureUrl")
                    return@withContext secureUrl
                } else {
                    Log.e("Cloudinary", "Upload failed: ${response.message}")
                    return@withContext null
                }

            } catch (e: Exception) {
                Log.e("Cloudinary", "Exception during upload", e)
                return@withContext null
            }
        }
    }
}