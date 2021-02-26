package com.activities.signup

import android.app.Activity
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

interface SignUpContract {

    interface SignUpView:BaseView<ActivitySignupBinding>{
        fun nextView()
        fun setStatusBarInit()
    }

    interface SignUpPresenter:BasePresenter<ActivitySignupBinding>{
        var view:SignUpView

        fun hideKeypad(activity:Activity,buttonName: Button)
        fun signUp()
        fun initViewpager()
        fun detachView()
    }
}