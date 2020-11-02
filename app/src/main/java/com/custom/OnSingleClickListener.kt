package com.custom

import android.view.View

class OnSingleClickListener(private val onSingleClick: (View) -> Unit) : View.OnClickListener {
    companion object {
        const val CLICK_INTERVAL = 500
    }

    private var lastClickedTime: Long = 0L

    override fun onClick(v: View?) {
        if (isSafe() && v != null) {
            onSingleClick(v)
        }
        lastClickedTime = System.currentTimeMillis()
    }

    private fun isSafe(): Boolean {
        return System.currentTimeMillis() - lastClickedTime > CLICK_INTERVAL
    }
}