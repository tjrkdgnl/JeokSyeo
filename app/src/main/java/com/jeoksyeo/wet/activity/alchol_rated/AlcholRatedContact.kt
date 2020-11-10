package com.jeoksyeo.wet.activity.alchol_rated

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.alchol_category.GridViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholRatedBinding

interface AlcholRatedContact {

    interface BaseView{
        fun getView():AlcholRatedBinding
    }

    interface BasesPresenter {
        var view: BaseView
        fun initProfile(provider: String?)

        fun initTabLayout(context: Context)

    }
}