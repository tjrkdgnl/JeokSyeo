package com.fragment.favorite

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.favorite.FavoriteAdapter
import com.model.favorite.AlcoholList
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

interface FavoriteContract {

    interface BaseView{
        fun getBinding():FragmentFavoriteBinding
        fun updateList(lst:MutableList<AlcoholList>)
        fun setAdapter(favoriteAdapter: FavoriteAdapter)
        fun getGridLayoutManager():GridLayoutManager

    }

    interface BasePresenter{
        var view:BaseView
        var viewModel:FavoriteViewModel
        var context:Context
        fun getMyAlcohol()
        fun detach()
    }
}