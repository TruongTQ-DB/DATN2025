package com.graduate.datn.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.graduate.datn.BaseApplication
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtil {
    @Throws(IOException::class)
    fun getCorrectAngleBitmap(photoPath: String?, bitmap: Bitmap): Bitmap {
        val ei = ExifInterface(
            photoPath!!
        )
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        var rotatedBitmap: Bitmap? = null
        rotatedBitmap =
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                    bitmap,
                    90f
                )
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                    bitmap,
                    180f
                )
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                    bitmap,
                    270f
                )
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
        return rotatedBitmap!!
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun saveImageToExternal(
        bm: Bitmap?
    ): String {
        val folder = BaseApplication.context.getExternalFilesDir("knd") ?: return ""
        if (!folder.exists()) {
            folder.mkdir()
        }
        val fname = "Image-" + System.currentTimeMillis().toString() + ".jpg"
        val file = File(folder, fname)
        if (file.exists()) file.delete()
        return try {
            val out = FileOutputStream(file)
            bm?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            file.absolutePath
        } catch (e: Exception) {
            print("error io")
            ""
        }
    }
    fun isFileLessThan7MB(file: File?): Boolean {
        val maxFileSize = 10 * 1024 * 1024
        val length = file?.length() ?: 0
        return length <= maxFileSize
    }

}