package com.fragments.alcohol_category.viewpager_items

import android.app.Activity
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.model.alcohol_category.AlcoholList

/**
 * Grid framgment 와 List fragment가 동시에 사용하고 있기 때문이 이 자체가 BASE가 된다.
 * 따라서 별도로 만들어놓은 BaseFragment를 상속받지 않는다.
 */
interface ViewPagerCategoryContact {

    interface CategoryBaseView<T:ViewDataBinding>{
        /**
         * 프래그먼트에서 구현한 databinding객체를 사용할 수있는 메서드
         */
        fun getbinding(): T

    }
    interface BasePresenter<T:ViewDataBinding>{
        val view:CategoryBaseView<T>
        var viewObj:CategoryBaseView<T>?
        var activity:Activity

        /**
         * type에 맞는 주류정보들을 api통신을 통해서 셋팅한다.
         *
         */
        fun initRecyclerView(context: Context)

        /**
         * pagination을 위한 리스너 셋팅
         */
        fun initScrollListener()

        /**
         *  다음 페이지의 정보를 api통신을 통해 얻어오는 메서드
         */
        fun pagination()

        /**
         * {@param sort}를 통해서 서버에서 정렬된 리스트를 받아오는 메서드
         */
        fun changeSort(sort: String)

        /**
         * 페이지네이션 후, 얻어오는 데이터를 어댑터에 추가하는 메서드
         */
        fun updateList(list:MutableList<AlcoholList>)

        /**
         * 정렬된 리스트로 어댑터 리스트를 변경하는 메서드
         */
        fun changeSortedList(list:MutableList<AlcoholList>)

        /**
         * 탭 클릭 시, 리스트 최상단으로 스크롤 이동시키는 메서드
         */
        fun moveTopPosition()

        /**
         * 어댑터 초기화 메서드
         */
        fun setAdapter(list:MutableList<AlcoholList>)


        fun detach()

    }
}