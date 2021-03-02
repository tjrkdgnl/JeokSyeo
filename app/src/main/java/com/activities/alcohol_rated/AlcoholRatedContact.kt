package com.activities.alcohol_rated

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.activities.alcohol_detail.AlcoholDetailContract
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.AlcoholRatedBinding

interface AlcoholRatedContact {

    interface RatedView:BaseView<AlcoholRatedBinding>


    interface RatedPresenter:BasePresenter<AlcoholRatedBinding> {
        /**
         * 액티비티로부터 구현된 RatedView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view: RatedView
        var viewObj:RatedView?

        fun initProfile(provider: String?)

        fun initTabLayout(context: Context)

        fun detach()

    }
}