package com.fragments.favorite

import android.content.Context
import com.base.BasePresenter
import com.base.BaseView
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

interface FavoriteContract {

    interface FavoriteView:BaseView<FragmentFavoriteBinding>

    interface FavoritePresenter:BasePresenter<FragmentFavoriteBinding>{
        val view:FavoriteView
        var viewObj:FavoriteView?

        /**
         * 뷰페이저의 프래그먼트와 내가 찜한 주류 fragment와 상호작용을 위해서 뷰모델 변수 생성
         */
        var viewModel:FavoriteViewModel


        fun getMyAlcohol()
        fun detach()
    }
}