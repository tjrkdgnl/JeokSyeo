package com.jeoksyeo.wet.activity.alcohol_rated

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedBinding

interface AlcoholRatedContact {

    interface BaseView{
        fun getView():AlcoholRatedBinding
    }

    interface BasesPresenter {
        var view: BaseView
        fun initProfile(provider: String?)

        fun initTabLayout(context: Context)

    }
}