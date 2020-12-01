package com.jeoksyeo.wet.activity.signup

import android.app.Activity
import android.widget.Button

interface SignUpContract {

    interface BaseView{
        fun nextView()
    }

    interface BasePresenter{
        var view:BaseView

        fun hideKeypad(activity:Activity,buttonName: Button)
        fun setBaseView(view:BaseView)

    }
}