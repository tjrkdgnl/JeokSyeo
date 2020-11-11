package com.jeoksyeo.wet.activity.alcohol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.adapter.alcohol_rated.RatedViewPagerAdapter
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayoutMediator
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :AlcoholRatedContact.BasesPresenter {
    override lateinit var view: AlcoholRatedContact.BaseView
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun initProfile(provider:String?) {
        provider?.let {
            view.getView().alcoholRatedName.text = GlobalApplication.userInfo.nickName +"님,"
            getMyReviewCount()
        }
    }

    override fun initTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getView().ratedViewPager2.adapter = RatedViewPagerAdapter(context)
            val lst = listOf<String>("전체","전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getView().ratedTablayout,view.getView().ratedViewPager2,
                TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                    val textView = TextView(context)
                    tab.customView =textView
                    textView.text = lst[position]
                    textView.textSize =15f
                    textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    textView.setTextColor(context.resources.getColor(R.color.tabColor,null))
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }).attach()
        }
    }

    @SuppressLint("SetTextI18n")
     fun getMyReviewCount() {
        JWTUtil.settingUserInfo(false)
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyRatedReviewSum(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.summary?.let {sum->
                    view.getView().ratedCountText.text = "총 " + sum.reviewCount.toString() +"개의 주류를 평가하셨습니다."
                    view.getView().ratedLevelName.text ="LV" + sum.level.toString() +". " + GlobalApplication.instance.getLevelName(sum.level!!)
                }
            },{t->
                Log.e("총 리뷰 개수 조회 에러",t.message.toString())
            }))
    }
}