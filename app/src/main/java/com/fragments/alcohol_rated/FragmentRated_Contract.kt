package com.fragments.alcohol_rated

import android.app.Activity
import com.base.BasePresenter
import com.base.BaseView
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

interface FragmentRated_Contract {

    interface FragmentRatedView:BaseView<FragmentAlcholRatedBinding>

    interface FragmentRatedPresenter:BasePresenter<FragmentAlcholRatedBinding>{
        /**
         * fragment에서 구현한 FragmentRatedView를 presenter에게 넘김으로써
         * presenter가 binding 객체 및 구현한 메서드에 접근할 수 있도록 허용
         */
        val view:FragmentRatedView
        var viewObj:FragmentRatedView?

        /**
         * 뷰페이저의 fragment와 뷰페이저를 갖고 있는 fragment 간의 통신을 위해서 사용
         */
        var viewmodel : RatedViewModel

        /**
         * typeList에 정의된 type을 가져오기 위해서 fragment 생성 시, 타입에 대한 포지션을 얻음
         * 이를 통해서 typeList 요소에 접근하여 type을 얻어온다. 얻어온 type으로 api 통신을 진행하여
         * 해당 type에 맞는 주류 리스트를 얻어올 수 있다.
         */
        var typePosition:Int

        /**
         * 주류 타입별로 내가 평가한 주류를 api통신을 통해 얻어온 후, 이를 리싸이클러뷰에 셋팅하는 메서드
         */
        fun initRatedList()

        /**
         * 메모리 할당해제
         */
        fun detach()
    }

}