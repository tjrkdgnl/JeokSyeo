package com.jeoksyeo.wet.activity.signup

import android.app.Activity
import android.content.Context
import android.widget.Button
import com.viewmodel.SignUpViewModel

interface SignUpContract {

    interface BaseView{
        fun nextView()
        fun beforeView()

    }

    interface BasePresenter{
        var view:BaseView

        fun hideKeypad(activity:Activity,buttonName: Button)
        fun setBaseView(view:BaseView)

    }
}