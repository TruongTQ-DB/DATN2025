package com.graduate.datn.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.graduate.datn.R

object DialogUtil {
    fun displayDialogSettings(
        context: Context,
        message: String,
        cancelable: Boolean = true,
        dismissCompletion: (() -> Unit)? = null,
        action: () -> Unit
    ): AlertDialog {

        val alertDialog = AlertDialog.Builder(context)
            .create()
        alertDialog.setTitle(context.getString(R.string.app_name))
        alertDialog.setIcon(R.mipmap.ic_launcher_round)
        alertDialog.setCancelable(cancelable)

        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.ok)
        ) { _, _ ->
            run {
                action()
                alertDialog.dismiss()
            }
        }
        alertDialog.setOnDismissListener {
            dismissCompletion?.invoke()
        }
        return alertDialog
    }
}