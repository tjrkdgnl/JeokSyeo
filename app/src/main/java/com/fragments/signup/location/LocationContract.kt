package com.fragments.signup.location

import com.base.BasePresenter
import com.base.BaseView
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.databinding.FragmentSignupLocationBinding

interface LocationContract {

    interface LocationView : BaseView<FragmentSignupLocationBinding> {

        /**
         * LocationAdapter에서 뷰모델을 사용하기 위한 메서드
         */
        fun getViewModel(): SignUpViewModel

        /**
         * 도 이름 셋팅
         */
        fun setCityName()

        /**
         * 시/군 이름 셋팅
         */
        fun setMiddleTownName()

        /**
         *  구 이름 셋팅
         */
        fun setSmallTownName()
    }


    interface LocationPresenter : BasePresenter<FragmentSignupLocationBinding> {
        val view: LocationView
        var viewObj: LocationView?

        /**
         * {@param code} code 파라미터에 따라서 지역 정보를 얻어오는 메서드
         */
        fun getArea(code:String?)

        /**
         * townText를 클릭했을 시, 이전 depth로 돌아가기 위한 메서드
         */
        fun initTownArea()

        /**
         * cityText를 클릭했을 시, 지역 셋팅 초기화
         */
        fun initCityArea()

        /**
         * 메모리 할당 해제
         */
        fun detach()
    }

}