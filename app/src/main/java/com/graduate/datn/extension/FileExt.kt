package com.graduate.datn.extension

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.util.*

fun File.copy(dest: File) {
    var fi: FileInputStream? = null
    var fo: FileOutputStream? = null
    var ic: FileChannel? = null
    var oc: FileChannel? = null
    try {
        if (!dest.exists()) {
            dest.createNewFile()
        }
        fi = FileInputStream(this)
        fo = FileOutputStream(dest)
        ic = fi.channel
        oc = fo.channel
        ic.transferTo(0, ic.size(), oc)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fi?.close()
        fo?.close()
        ic?.close()
        oc?.close()
    }
}

fun File.move(dest: File) {
    copy(dest)
    delete()
}

fun Bitmap.convertFile(context: Context) : File {
    val wrapper = ContextWrapper(context)
    var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
    file = File(file,"${UUID.randomUUID()}.jpg")
    val stream: OutputStream = FileOutputStream(file)
    this.compress(Bitmap.CompressFormat.JPEG,25,stream)
    stream.flush()
    stream.close()
    return file
}