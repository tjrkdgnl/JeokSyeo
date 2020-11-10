package com.fragment.alcohol_rated

import android.content.Context
import com.adapter.alcohol_rated.AlcoholRatedAdapter
import com.custom.CenterSmoothScroller
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

interface FragmentRated_Contract {

    interface BaseView{
        fun getBinding():FragmentAlcholRatedBinding
    }

    interface BasePresenter{
        var position:Int
        var view:BaseView
        var smoothScrollListener:Fragment_alcoholRated.SmoothScrollListener
        fun initRatedList(context: Context)
    }

}