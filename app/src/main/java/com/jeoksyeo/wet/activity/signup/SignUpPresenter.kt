package com.jeoksyeo.wet.activity.signup

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Button

class SignUpPresenter : SignUpContract.BasePresenter {
    override lateinit var view: SignUpContract.BaseView

    override fun hideKeypad(activity: Activity,buttonName: Button) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(buttonName.windowToken, 0)
    }

    override fun setBaseView(view: SignUpContract.BaseView) {
        this.view = view
    }

}