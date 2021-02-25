package com.fragments.alcohol_rated

import android.app.Activity
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

interface FragmentRated_Contract {

    interface BaseView{
        fun getBinding():FragmentAlcholRatedBinding
    }

    interface BasePresenter{
        var position:Int
        var view:BaseView
        var activity:Activity
        var viewmodel : RatedViewModel
        fun initRatedList()
    }

}