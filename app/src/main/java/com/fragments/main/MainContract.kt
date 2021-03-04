package com.fragments.main

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.MainBinding

interface MainContract {
    interface MainView:BaseView<MainBinding>

    interface MainPresenter:BasePresenter<MainBinding> {
        /**
         * 메인프래그먼트에서 구현한 MainView 인터페이스의 메스드를 presenter에서
         * 이용할 수 있도록 하기 위한 변수
         */
        val view: MainView
        var viewObj:MainView?


        /**
         * 배너 데이터를 뷰페이저에 api로부터 얻은 리스트를 할당하기 위한 메서드
         */
        fun initBanner()

        /**
         * 배너의 쪽수를 표시하기 위한 계산값 리턴
         */
        fun bannerNumber(position:Int)


        /**
         * 주류 추천을 위해서 뷰페이저에 api로부터 얻은 리스트를 할당하기 위한 메서드
         */
        fun initRecommendViewPager()

        /**
         * 이달의 주류 목록을 보여주기 위해서 리싸이클러뷰에 api로부터 얻은 리스트를 할당하기 위한 메서드
         */
        fun initAlcoholRanking()

        /**
         * 메모리 할당 해제
         */
        fun detachView()
    }
}