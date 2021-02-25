package com.fragments.alcohol_category

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.adapters.alcohol_category.GridViewPagerAdapter
import com.adapters.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.fragments.alcohol_category.viewpager_items.Fragment_Grid
import com.fragments.alcohol_category.viewpager_items.Fragment_List
import com.google.android.material.tabs.TabLayoutMediator
import com.service.JWTUtil
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import kotlinx.android.synthetic.main.alcohol_category.view.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter : AlcoholCategoryContact.BasePresenter {
    override lateinit var view: AlcoholCategoryContact.BaseView
    override lateinit var context: Context


    override fun inintTabLayout(fragment: Fragment, currentItem: Int,toggle:String) {

        if(toggle ==AlcoholCategoryViewModel.GRID_TOGGLE){
            view.getViewBinding().viewPager2Container.adapter = GridViewPagerAdapter(fragment)

        }
        else{
            view.getViewBinding().viewPager2Container.adapter = ListViewPagerAdapter(fragment)
        }

        val lst = mutableListOf("전통주", "맥주", "와인", "양주", "사케")
        TabLayoutMediator(
            view.getViewBinding().tabLayoutAlcoholList, view.getViewBinding().viewPager2Container
        ) { tab, position ->
            val textView = TextView(context)
            tab.customView = textView
            textView.text = lst[position]

            //(폰트 고정 사이즈 * textview의 고정 넓이) * 비율로 계산된 값
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                GlobalApplication.instance.getCalculatorTextSize(16f, true, 5)
            )

            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.resources.getColor(R.color.tabColor, null))
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.tabColor))
            }
            textView.gravity = Gravity.CENTER_HORIZONTAL

            //탭을 클릭할 시, 리스트의 최상단으로 이동하기 위한 코드
            tab.view.setOnClickListener {
                val fg = getFragement(currentItem)

                if (fg is Fragment_Grid) {
                    fg.moveTopPosition()
                } else if (fg is Fragment_List) {
                    fg.moveTopPosition()
                }

            }
        }.attach()

        view.getViewBinding().viewPager2Container.offscreenPageLimit = 5
        view.getViewBinding().viewPager2Container.post {
            view.getViewBinding().viewPager2Container.setCurrentItem(currentItem, true)
        }

        view.getViewBinding().viewPager2Container.isSaveEnabled = false
    }


    override fun getFragement(position: Int): Fragment? {
        var fragment: Fragment? = null
        if (view.getViewBinding().viewPager2Container.adapter is GridViewPagerAdapter) {
            fragment = (view.getViewBinding().viewPager2Container.adapter as GridViewPagerAdapter)
                .getFragment(view.getViewBinding().viewPager2Container.currentItem)
        } else if (view.getViewBinding().viewPager2Container.adapter is ListViewPagerAdapter) {
            fragment = (view.getViewBinding().viewPager2Container.adapter as ListViewPagerAdapter)
                .getFragment(view.getViewBinding().viewPager2Container.currentItem)
        }

        return fragment
    }

    override fun checkSort(position: Int, sort: String) {
        getFragement(position)?.let { fragment ->
            if (fragment is Fragment_Grid) {
                if (fragment.getSort() != sort) {
                    fragment.changeSort(sort)
                }
            } else if (fragment is Fragment_List) {
                if (fragment.getSort() != sort) {
                    fragment.changeSort(sort)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun checkLogin(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                GlobalApplication.userInfo.getProvider()?.let {
                    //유저 프로필 설정하는 화면 필요함
                    view.getViewBinding().categoryDrawerLayout.category_navigation.navigation_header_Name.text =
                        GlobalApplication.userInfo.nickName + "님,"
                    view.getViewBinding().categoryDrawerLayout.category_navigation.navigation_header_hello.text =
                        "안녕하세요"
                }
                GlobalApplication.userInfo.getProfile()?.let { lst ->
                    Log.e("프로필 변경", "변경")
                    if (lst.isNotEmpty()) {
                        Glide.with(context)
                            .load(lst[lst.size - 1].mediaResource?.small?.src.toString())
                            .apply(
                                RequestOptions()
                                    .signature(ObjectKey(System.currentTimeMillis()))
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .circleCrop()
                            )
                            .into(view.getViewBinding().categoryNavigation.navigationHeader.navigationHeaderProfile)
                    }
                }
            }
        }
    }

    override fun detach() {

    }
}