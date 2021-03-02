package com.activities.favorite

import android.content.Context
import com.activities.editprofile.EditProfileContract
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

interface FavoriteContract {

    interface FavoriteView : BaseView<FavoriteActivityBinding>{

        /**
         * header 객체에서 필요한 부분만 수정하여 재 사용
         */
        fun setHeaderInit()
    }

    interface FavoritePresenter:BasePresenter<FavoriteActivityBinding>{
        /**
         * 액티비티로부터 구현된 FavoriteView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view: FavoriteView
        var viewObj: FavoriteView?

        /**
         * 메모리 할당 해제
         */
        fun detach()

        /**
         * 탭레이아웃과 뷰페이저를 연결하는 기본 셋팅
         */
        fun initTabLayout()

        /**
         * 기본 유저의 정보 셋팅
         */
        fun initProfile()
    }
}