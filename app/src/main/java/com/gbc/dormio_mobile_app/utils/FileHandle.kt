package com.gbc.dormio_mobile_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream


data class PreparedImage(
    val part: MultipartBody.Part,
    val file: File
)
object FileHandle {

    fun prepareImage(context: Context, partName: String, fileUri: Uri): PreparedImage? {
        val file = getCompressedFileFromUri(context, fileUri) ?: return null

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(partName, file.name, requestFile)

        return PreparedImage(part, file)
    }

    private fun getCompressedFileFromUri(context: Context, uri: Uri): File? {
        val fileName = "upload_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        return try {
            val contentResolver = context.contentResolver

            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }

            options.inSampleSize = calculateInSampleSize(options, 1080, 1080)
            options.inJustDecodeBounds = false

            val scaledBitmap = contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return null

            FileOutputStream(tempFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            scaledBitmap.recycle()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}