package com.fragment.alcohol_category

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.adapter.alcohol_category.ListViewPagerAdapter
import com.adapter.navigation.NavigationAdpater
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.fragment.alcohol_category.viewpager_items.Fragment_Grid
import com.fragment.alcohol_category.viewpager_items.Fragment_List
import com.google.android.material.tabs.TabLayoutMediator
import com.model.navigation.NavigationItem
import com.service.JWTUtil
import com.service.NetworkUtil
import com.vuforia.engine.wet.R
import kotlinx.android.synthetic.main.alcohol_category.view.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter: AlcoholCategoryContact.BasePresenter {
    override lateinit var view: AlcoholCategoryContact.BaseView
    override lateinit var context: Context


    @SuppressLint("ClickableViewAccessibility")
    override fun inintTabLayout(context: Context, currentItem: Int) {
        if(context is FragmentActivity){
            view.getViewBinding().viewPager2Container.adapter = GridViewPagerAdapter(context)
            val lst = mutableListOf("전통주", "맥주", "와인", "양주", "사케")
            TabLayoutMediator(
                view.getViewBinding().tabLayoutAlcoholList, view.getViewBinding().viewPager2Container
            ) { tab, position ->
                val textView = TextView(context)
                tab.customView = textView
                textView.text = lst[position]

                //(폰트 고정 사이즈 * textview의 고정 넓이) * 비율로 계산된 값
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,(16f/72f)*(GlobalApplication.instance.device_width/5f))

                textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(context.resources.getColor(R.color.tabColor, null))
                }
                else{
                    textView.setTextColor(ContextCompat.getColor(context, R.color.tabColor))
                }
                textView.gravity = Gravity.CENTER_HORIZONTAL


                //탭을 클릭할 시, 리스트의 최상단으로 이동하기 위한 코드
                tab.view.setOnClickListener {
                    val fragment = getFragement(currentItem)

                    if(fragment is Fragment_Grid){
                        fragment.moveTopPosition()
                    }
                    else if (fragment is Fragment_List){
                        fragment.moveTopPosition()
                    }

                }
            }.attach()

            Log.e("탭 값",currentItem.toString())
            view.getViewBinding().viewPager2Container.offscreenPageLimit=5
            view.getViewBinding().viewPager2Container.post{
                view.getViewBinding().viewPager2Container.setCurrentItem(currentItem,true)
            }
        }
    }


    override fun getFragement(position: Int): Fragment? {
        var fragment:Fragment? =null
        if(view.getViewBinding().viewPager2Container.adapter is GridViewPagerAdapter){
            fragment= (view.getViewBinding().viewPager2Container.adapter as GridViewPagerAdapter)
                .getFragment(view.getViewBinding().viewPager2Container.currentItem)
        }
        else if (view.getViewBinding().viewPager2Container.adapter is ListViewPagerAdapter){
            fragment= (view.getViewBinding().viewPager2Container.adapter as ListViewPagerAdapter)
                .getFragment(view.getViewBinding().viewPager2Container.currentItem)
        }

        return fragment
    }

    override fun checkSort(position: Int, sort: String) {
        getFragement(position)?.let { fragment ->
            if(fragment is Fragment_Grid){
                if(fragment.getSort() != sort){
                    fragment.changeSort(sort)
                }
            }
            else if (fragment is Fragment_List){
                if(fragment.getSort() != sort){
                    fragment.changeSort(sort)
                }
            }
        }
    }

    override fun initNavigationItemSet(context: Context, activity: Activity) {

        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
                val lst = mutableListOf<NavigationItem>()
                lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
                lst.add(NavigationItem(R.mipmap.btn_top_setting, "설정"))
                lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
                lst.add(NavigationItem(R.mipmap.navigation1_img, "내가 평가한 주류"))
                lst.add(NavigationItem(R.mipmap.navigation2_img, "나의 주류 레벨"))
                lst.add(NavigationItem(R.mipmap.navigation3_img, "내가 찜한 주류"))
                lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
                lst.add(GlobalApplication.userInfo.getProvider()?.let {
                    NavigationItem(
                        R.mipmap.navigation5_img,
                        "로그아웃"
                    )
                }
                    ?: NavigationItem(R.mipmap.navigation5_img, "로그인"))

                view.getViewBinding().categoryNavigation.navigationContainer.setHasFixedSize(true)
                view.getViewBinding().categoryNavigation.navigationContainer.layoutManager = LinearLayoutManager(
                    context
                )
                view.getViewBinding().categoryNavigation.navigationContainer.adapter = NavigationAdpater(
                    context,
                    activity,
                    lst,
                    GlobalApplication.userInfo.getProvider(),
                    GlobalApplication.ACTIVITY_HANDLING_CATEGORY
                )
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun checkLogin(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
                GlobalApplication.userInfo.getProvider()?.let {
                    //유저 프로필 설정하는 화면 필요함
                    view.getViewBinding().categoryDrawerLayout.category_navigation.navigation_header_Name.text= GlobalApplication.userInfo.nickName + "님,"
                    view.getViewBinding().categoryDrawerLayout.category_navigation.navigation_header_hello.text = "안녕하세요"
                }
                GlobalApplication.userInfo.getProfile()?.let { lst->
                    Log.e("프로필 변경", "변경")
                    if(lst.isNotEmpty()){
                        Glide.with(context)
                            .load(lst[lst.size - 1].mediaResource?.small?.src.toString())
                            .apply(
                                RequestOptions()
                                    .signature(ObjectKey("signature"))
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