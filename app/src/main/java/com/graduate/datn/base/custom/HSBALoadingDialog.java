package com.graduate.datn.base.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.graduate.datn.R;


public class HSBALoadingDialog {

    private static boolean shown = false;

    private AlertDialog dialog = null;

    private static HSBALoadingDialog instance = null;

    private Context context;

    public static HSBALoadingDialog getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            instance = new HSBALoadingDialog(context);
            return instance;
        }
    }

    private HSBALoadingDialog(Context context) {
        this.context = context;
        if (context != null && !HSBALoadingDialog.isShown()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater li = LayoutInflater.from(context);
            View dialogView = li.inflate(R.layout.dialog_loading, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            dialog = dialogBuilder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
//            dialog.setOnKeyListener((dialog, keyCode, event) -> {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    // we cannot close dialog when we press back button
//                }
//                return false;
//            });
        }
    }

    public void show() {
        if (!HSBALoadingDialog.isShown() && dialog != null) {
            forceShown();
            dialog.show();
        }
    }

    public void hidden() {
        if (HSBALoadingDialog.isShown() && dialog != null && dialog.isShowing()) {
            initialize();
            dialog.dismiss();
        }
    }

    private static boolean isShown() {
        return shown;
    }

    private static void forceShown() {
        shown = true;
    }

    private static void initialize() {
        shown = false;
    }

    public void destroyLoadingDialog() {
        if (instance != null && instance.dialog != null) {
            instance.dialog.dismiss();
        }
        instance = null;
    }
}
