package com.activities.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.vuforia.engine.wet.databinding.RealMainActivityBinding

interface Contract {

    interface BaseView{
        fun getBinding():RealMainActivityBinding

        fun replaceFragment(fragment: Fragment,name:String)

        fun cancelTheJourneyLoginToast()

        fun showTheJourneyLoginToast()

        fun bottomNavigationVisiblity(check:Int)

    }

    interface BasePresenter{
        var view:BaseView
        var activity: FragmentActivity

        fun detachView()

        fun setNetworkUtil()

        fun getAlcohol(alcoholId: String)

        fun handleDeepLink()

    }
}