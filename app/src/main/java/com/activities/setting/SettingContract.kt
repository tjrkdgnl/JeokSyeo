package com.activities.setting

import android.content.Context
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.SettingBinding

interface SettingContract {

    interface SettingView:BaseView<SettingBinding>{

        fun setStatusBarInit()
    }

    interface SettingPresenter:BasePresenter<SettingBinding>{
        /**
         * 액티비티로부터 구현된 SettingView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view:SettingView
        var viewObj:SettingView?

        /**
         * 설정화면에서 보여질 아이템 초기화
         */
        fun initItem(context: Context)

        fun detachView()
    }

}