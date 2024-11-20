package com.graduate.datn.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val launchIntent: Intent? =
            packageManager.getLaunchIntentForPackage("com.graduate.datn")
        if (launchIntent != null) {
            startActivity(launchIntent)
        }else{
            finish()
        }
    }
}