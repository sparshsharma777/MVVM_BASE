package com.ss.instagramdownloader.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Singleton


@Singleton
class AppCache(private val context: Context)
{

   private val TAG: String = AppCache::class.java.simpleName

    private val CHILD_DIR = "images"
    private val GENCRAFT_IMAGE = "Gencraft/images"
    private val TEMP_FILE_NAME = "img"
    private val FILE_EXTENSION = ".png"

    private val COMPRESS_QUALITY = 100

    /**
     * Save image to the App cache
     * @param bitmap to save to the cache
     * @param name file name in the cache.
     * If name is null file will be named by default [.TEMP_FILE_NAME]
     * @return file dir when file was saved
     */
    fun saveImgToCache(bitmap: Bitmap, @Nullable name: String?): File? {
        var cachePath: File? = null
        var fileName: String? = TEMP_FILE_NAME
        if (!TextUtils.isEmpty(name)) {
            fileName = name
        }
        try {
            cachePath = File(context.cacheDir, CHILD_DIR)
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/$fileName$FILE_EXTENSION")
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, stream)
            stream.close()
        } catch (e: IOException) {
            Log.e(TAG, "saveImgToCache error: $bitmap", e)
        }
        return cachePath
    }


    /**
     * Save image to the App cache
     * @param bitmap to save to the cache
     * @param name file name in the cache.
     * If name is null file will be named by default [.TEMP_FILE_NAME]
     * @return file dir when file was saved
     */
    fun saveImgToDownloadFolder(bitmap: Bitmap, @Nullable name: String?): File? {
        var filePath: File? = null
        var fileName: String? = System.currentTimeMillis().toString()+TEMP_FILE_NAME
        if (!TextUtils.isEmpty(name)) {
            fileName = name
        }
        try {
            filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), GENCRAFT_IMAGE)
            filePath.mkdirs()

            //val stream = FileOutputStream("$filePath/$fileName$FILE_EXTENSION")
            val stream = FileOutputStream("$filePath/$fileName$FILE_EXTENSION")
            bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, stream)
            stream.close()
        } catch (e: IOException) {
            Log.e(TAG, "saveImgToCache error: $bitmap", e)
        }
        return filePath
    }

    /**
     * Save an image to the App cache dir and return it [Uri]
     * @param bitmap to save to the cache
     */
    fun saveToCacheAndGetUri(bitmap: Bitmap): Uri? {
        return saveToCacheAndGetUri(bitmap, null)
    }

    /**
     * Save an image to the App cache dir and return it [Uri]
     * @param bitmap to save to the cache
     * @param name file name in the cache.
     * If name is null file will be named by default [.TEMP_FILE_NAME]
     */
    fun saveToCacheAndGetUri(bitmap: Bitmap, @Nullable name: String?): Uri? {
        val file: File? = saveImgToCache(bitmap, name)
        return getImageUri(file, name)
    }

    /**
     * Get a file [Uri]
     * @param name of the file
     * @return file Uri in the App cache or null if file wasn't found
     */
    @Nullable
    fun getUriByFileName(name: String): Uri? {

        val fileName: String
        fileName = if (!TextUtils.isEmpty(name)) {
            name
        } else {
            return null
        }
        val imagePath = File(context.cacheDir, CHILD_DIR)
        val newFile = File(imagePath, fileName + FILE_EXTENSION)
        return FileProvider.getUriForFile(context, context.packageName + ".provider", newFile)
    }

    // Get an image Uri by name without extension from a file dir
    private fun getImageUri(fileDir: File?, @Nullable name: String?): Uri? {
        var fileName: String? = TEMP_FILE_NAME
        if (!TextUtils.isEmpty(name)) {
            fileName = name
        }
        val newFile = File(fileDir, fileName + FILE_EXTENSION)
        return FileProvider.getUriForFile(context, context.packageName + ".provider", newFile)
    }

    /**
     * Get Uri type by [Uri]
     */
    fun getContentType(uri: Uri?): String? {
        return context.contentResolver.getType(uri!!)
    }
}