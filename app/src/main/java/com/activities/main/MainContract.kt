package com.activities.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.MainBinding
import com.vuforia.engine.wet.databinding.RealMainActivityBinding

interface MainContract {

    interface MainView:BaseView<RealMainActivityBinding>{
        fun replaceFragment(fragment: Fragment,name:String)

        fun cancelTheJourneyLoginToast()

        fun showTheJourneyLoginToast()

        fun bottomNavigationVisiblity(check:Int)

    }

    interface MainPresenter: BasePresenter<RealMainActivityBinding>{
        var view:MainView

        fun detachView()

        fun getAlcohol(alcoholId: String)

        fun handleDeepLink()

    }
}