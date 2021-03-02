package com.activities.favorite

import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.adapters.favorite.FavoriteViewPagerAdapter
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R
import io.reactivex.disposables.CompositeDisposable

class Presenter : FavoriteContract.FavoritePresenter {
    override var viewObj: FavoriteContract.FavoriteView? = null
    override val view: FavoriteContract.FavoriteView by lazy {
        viewObj!!
    }
    override lateinit var activity: Activity
    private val compositeDisposable = CompositeDisposable()
    val tabList = listOf("전체", "전통주", "맥주", "와인", "양주", "사케")

    override fun initTabLayout() {
        view.getBindingObj().favoriteViewPager2.adapter =
            FavoriteViewPagerAdapter(activity as FragmentActivity)

        //뷰페이저와 탭레이아웃 연결
        TabLayoutMediator(
            view.getBindingObj().favoriteTablayout, view.getBindingObj().favoriteViewPager2
        ) { tab, position ->
            var textView = TextView(activity)
            tab.customView = textView
            textView.text = tabList[position]

            //(폰트 고정 사이즈 * textview의 고정 넓이) * 비율로 계산된 값
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                GlobalApplication.instance.getCalculatorTextSize(16f, true, 6)
            )

            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(activity.resources.getColor(R.color.tabColor, null))
            } else {
                textView.setTextColor(ContextCompat.getColor(activity, R.color.tabColor))
            }
            textView.gravity = Gravity.CENTER_HORIZONTAL

        }.attach()

        //한번에 모든 page 셋팅
        view.getBindingObj().favoriteViewPager2.offscreenPageLimit = 5

    }

    //유저 프로필 셋팅
    override fun initProfile() {
        GlobalApplication.userInfo.getProfile()?.let { lst ->
            if (lst.isNotEmpty()) {
                Glide.with(activity)
                    .load(lst[lst.size - 1].mediaResource?.small?.src.toString())
                    .apply(
                        RequestOptions()
                            .signature(ObjectKey(System.currentTimeMillis()))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                    )
                    .into(view.getBindingObj().profileHeader.favoriteProfile)
            }
        }
        view.getBindingObj().profileHeader.alcoholRatedName.text =
            GlobalApplication.userInfo.nickName
    }

    override fun detach() {
        compositeDisposable.dispose()
        viewObj = null
    }
}