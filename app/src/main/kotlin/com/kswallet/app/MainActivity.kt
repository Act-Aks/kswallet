package com.kswallet.app

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = TextView(this).apply {
            text = getString(R.string.foundation_ready)
            textSize = 24f
            gravity = Gravity.CENTER
        }

        setContentView(content)
    }
}