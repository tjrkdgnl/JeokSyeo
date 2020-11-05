package com.fragment.alchol_rated

import android.content.Context
import com.adapter.alchol_rated.AlcholRatedAdapter
import com.custom.CenterSmoothScroller
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

interface FragmentRated_Contract {

    interface BaseView{
        fun getBinding():FragmentAlcholRatedBinding
    }

    interface BasePresenter{
        var position:Int
        var view:BaseView
        var smoothScrollListener:Fragment_alcholRated.SmoothScrollListener
        fun initRatedList(context: Context)
    }

}