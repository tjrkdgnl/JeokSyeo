package com.fragment.favorite

import android.content.Context
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

interface FavoriteContract {

    interface BaseView{
        fun getBinding():FragmentFavoriteBinding



    }

    interface BasePresenter{
        var view:BaseView
        var viewModel:FavoriteViewModel
        var context:Context
        fun getMyAlcohol()
        fun detach()
    }
}