package com.activities.favorite

import android.content.Context
import com.activities.editprofile.EditProfileContract
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

interface FavoriteContract {

    interface FavoriteView : BaseView<FavoriteActivityBinding>{

        fun setHeaderInit()
    }

    interface FavoritePresenter:BasePresenter<FavoriteActivityBinding>{
        /**
         * 액티비티로부터 구현된 FavoriteView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        var view: FavoriteView


        fun detach()
        fun initTabLayout()
        fun initProfile()
    }
}