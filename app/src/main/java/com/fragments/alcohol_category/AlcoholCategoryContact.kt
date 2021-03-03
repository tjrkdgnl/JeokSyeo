package com.fragments.alcohol_category

import androidx.fragment.app.Fragment
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding

interface AlcoholCategoryContact {

    interface AlcoholCategoryView: BaseView<AlcoholCategoryBinding> {
        /**
         * 리스너 셋팅 및 텍스트 크기 설정 등 기본적으로 갖춰야할 값을 셋팅하는 메서드
         */
        fun init()

        /**
         * 카테고리 보는 방식은 그리드 방식과 리스트 방식이 존재한다.
         * 유저가 클릭하는 방법에 따라 카테고리 방식이 변경되며, 리싸이클러뷰 어댑터도 교체되는
         * 작업이 진행되는 메서드이다.
         */
        fun changeToggle(toggle:Boolean)
    }

    interface AlcoholCategoryPresenter:BasePresenter<AlcoholCategoryBinding>{
        val view: AlcoholCategoryView
        var viewObj:AlcoholCategoryView?

        /**
         *기본적으로 탭레이아웃과 뷰페이저2를 연결하는 역할을 한다.
         * 1.카테고리 프래그먼트 내부에 뷰페이저가 존재하는 형태이다. 또한 뷰페이저의 페이지는 프래그먼트이기 때문에
         * 중첩 프래그먼트인 형태를 갖는다. 따라서 {@param fragment}를 받아서 뷰페이저의 어댑터로 넘겨줘야한다.(액티비티를 넘겨주면 안된다)
         *
         * 2.{@param currentItem}은 메인화면에서 유저가 타입별로 클릭했을 시, 해당 타입에 대한 뷰페이저의 위치를 의미한다.
         * 만약 [전통주, 맥주, 와인, 양주 사케] 타입이고 유저가 와인을 클릭했을 시, 이는 뷰페이저의 currentItem =2 임을 의미한다.
         * 따라서 카테고리 화면은 곧장 와인에 대한 카테고리를 보여주기 위해 currentItem 파라미터가 필요하다.
         *
         */
        fun inintTabLayout(fragment: Fragment,currentItem:Int)

        /**
         * 뷰페이저에서 보여지고 있는 프래그먼트 호출메서드
         */
        fun getFragement(position:Int): Fragment?


        fun executeSorting(sort: String)

        /**
         * 메모리 할당하제
         */
        fun detach()
    }
}