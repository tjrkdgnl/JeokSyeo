package com.jeoksyeo.wet.activity.signup

import android.app.Activity
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

interface SignUpContract {

    interface BaseView{
        fun nextView()
        fun getBinding():ActivitySignupBinding
    }

    interface BasePresenter{
        var view:BaseView
        var activity:FragmentActivity

        fun hideKeypad(activity:Activity,buttonName: Button)
        fun signUp()
        fun initViewpager()
        fun setNetworkUtil()
        fun detachView()
    }
}