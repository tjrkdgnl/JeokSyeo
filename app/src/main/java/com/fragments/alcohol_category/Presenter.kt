package com.fragments.alcohol_category

import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.adapters.alcohol_category.GridViewPagerAdapter
import com.adapters.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.fragments.alcohol_category.viewpager_items.Fragment_Grid
import com.fragments.alcohol_category.viewpager_items.Fragment_List
import com.google.android.material.tabs.TabLayoutMediator
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R

class Presenter : AlcoholCategoryContact.AlcoholCategoryPresenter {
    override var viewObj: AlcoholCategoryContact.AlcoholCategoryView? =null
    override val view: AlcoholCategoryContact.AlcoholCategoryView by lazy {
        viewObj!!
    }
    override lateinit var activity: Activity
    lateinit var viewmodel:AlcoholCategoryViewModel

    override fun inintTabLayout(fragment: Fragment, currentItem: Int) {
        view.getBindingObj().viewPager2Container.adapter = GridViewPagerAdapter(fragment)

        val lst = mutableListOf("전통주", "맥주", "와인", "양주", "사케")
        TabLayoutMediator(
            view.getBindingObj().tabLayoutAlcoholList, view.getBindingObj().viewPager2Container
        ) { tab, position ->
            val textView = TextView(activity)
            tab.customView = textView
            textView.text = lst[position]

            //(폰트 고정 사이즈 * textview의 고정 넓이) * 비율로 계산된 값
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                GlobalApplication.instance.getCalculatorTextSize(16f, true, 5)
            )

            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(activity.resources.getColor(R.color.tabColor, null))
            } else {
                textView.setTextColor(ContextCompat.getColor(activity, R.color.tabColor))
            }
            textView.gravity = Gravity.CENTER_HORIZONTAL

            //탭을 클릭할 시, 리스트의 최상단으로 이동하기 위한 코드
            tab.view.setOnClickListener {
                val fg = getFragement(currentItem)

                if (fg is Fragment_Grid) {
                    fg.gridPresenter.moveTopPosition()
                } else if (fg is Fragment_List) {
                    fg.listPresenter.moveTopPosition()
                }
            }
        }.attach()

        view.getBindingObj().viewPager2Container.offscreenPageLimit = 5
        view.getBindingObj().viewPager2Container.post {
            view.getBindingObj().viewPager2Container.setCurrentItem(currentItem, true)
        }

        //뷰의 상태값을 갖는지에 대한 여부는 기본적으로 true이다. 이 때문에 카테고리->검색화면->카테고리로
        //프래그먼트 전환 시, 다시 카테고리 화면으로 돌아왔을 때, 정보 값을 잃어버리는데 뷰의 상태값을 찾으려고
        //하므로 에러를 발생시킨다. 따라서 뷰의 상태값을 저장하지 않도록한다.
//        view.getBindingObj().viewPager2Container.isSaveEnabled = false
    }


    override fun getFragement(position: Int): Fragment? {
        var fragment: Fragment? = null
        if (view.getBindingObj().viewPager2Container.adapter is GridViewPagerAdapter) {
            fragment = (view.getBindingObj().viewPager2Container.adapter as GridViewPagerAdapter)
                .getFragment(view.getBindingObj().viewPager2Container.currentItem)
        } else if (view.getBindingObj().viewPager2Container.adapter is ListViewPagerAdapter) {
            fragment = (view.getBindingObj().viewPager2Container.adapter as ListViewPagerAdapter)
                .getFragment(view.getBindingObj().viewPager2Container.currentItem)
        }

        return fragment
    }

    override fun executeSorting(sort: String) {
        viewmodel.currentSort.value = sort
    }

    override fun detach() {
        viewObj=null
    }
}