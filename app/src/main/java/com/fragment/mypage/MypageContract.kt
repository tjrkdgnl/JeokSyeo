package com.fragment.mypage

import android.app.Activity
import android.content.Context
import com.vuforia.engine.wet.databinding.MypageBinding

interface MypageContract {

    interface BaseView{
        fun getViewBinding():MypageBinding
    }

    interface BasePresenter{
        var baseView:BaseView
        var activity: Activity

        fun initTextSize()

        fun initRecyclerView()

        fun checkLogin()

    }
}