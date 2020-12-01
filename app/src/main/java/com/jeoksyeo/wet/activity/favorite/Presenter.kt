package com.jeoksyeo.wet.activity.favorite

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.favorite.FavoriteViewPagerAdapter
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.tabs.TabLayoutMediator
import com.vuforia.engine.wet.R
import io.reactivex.disposables.CompositeDisposable

class Presenter : FavoriteContract.BasePresenter {

    override lateinit var context: Context

    override lateinit var view: FavoriteContract.BaseView

    private val compositeDisposable = CompositeDisposable()

    override fun initTabLayout() {
        val tabList = listOf("전체","전통주","맥주","와인","양주","사케")

        if(context is FragmentActivity){
            view.getBinding().favoriteViewPager2.adapter = FavoriteViewPagerAdapter(context as FragmentActivity)
        }

        TabLayoutMediator(view.getBinding().favoriteTablayout,view.getBinding().favoriteViewPager2
        ) { tab, position ->
            var textView = TextView(context)
            tab.customView = textView
            textView.text = tabList[position]
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.tab_text_size)
            )
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textView.setTextColor(context.resources.getColor(R.color.tabColor, null))
            textView.gravity = Gravity.CENTER_HORIZONTAL

        }.attach()

            view.getBinding().favoriteViewPager2.offscreenPageLimit =5
    }

    override fun initProfile() {
        GlobalApplication.userInfo.getProfile()?.let { lst->
            if(lst.isNotEmpty()){
                Glide.with(context)
                    .load(lst[lst.size-1].mediaResource?.small?.src.toString())
                    .apply(
                        RequestOptions()
                            .signature(ObjectKey("signature"))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                    )
                    .into(view.getBinding().profileHeader.favoriteProfile)
            }
        }
        view.getBinding().profileHeader.alcoholRatedName.text = GlobalApplication.userInfo.nickName
    }

    override fun detach() {
        compositeDisposable.dispose()
    }
}