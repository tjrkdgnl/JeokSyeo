package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.main.AlcoholRankAdapter
import com.adapter.main.BannerAdapter
import com.adapter.main.RecommendAlcoholAdapter
import com.adapter.navigation.NavigationAdpater
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.custom.ViewPagerTransformer
import com.error.ErrorManager
import com.model.banner.Banner
import com.model.navigation.NavigationItem
import com.model.recommend_alcohol.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class Presenter : MainContract.BasePresenter {
    override lateinit var view: MainContract.BaseView
    private  var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private lateinit var bannerAdapter: BannerAdapter
    var bannerItem:Int =0


    override fun initBanner(context: Context) {
       JWTUtil.settingUserInfo(false)

        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getBannerData(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.banner?.let {lst->
                    bannerItem = lst.size
                    val infiniteLst = mutableListOf<Banner>()
                    var i = 0
                    while(i !=200){
                        infiniteLst.addAll(lst)
                        i++
                    }
                    bannerAdapter =  BannerAdapter(context,infiniteLst)

                    view.getView().mainBanner.adapter =bannerAdapter
                    view.getView().mainBanner.currentItem = bannerItem *100

                }
            },{ t->Log.e(ErrorManager.BANNER,t.message.toString()) }))
    }

    fun autoSlide(){
        compositeDisposable.add(Observable.interval(6000L,TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.getView().mainBanner.currentItem = view.getView().mainBanner.currentItem +1
                view.getView().bannerCount.text = "${view.getView().mainBanner.currentItem % bannerItem +1} / $bannerItem"
            },{t ->Log.e("banner auto slide",t.message.toString())

            }))
    }



    override fun initRecommendViewPager(context: Context)  {
        JWTUtil.settingUserInfo(false)

        //뷰페이저 슬라이딩 애니메이션 셋팅
        view.getView().activityMainRecommendViewPager2.setPageTransformer(
            ViewPagerTransformer(
                context
            )
        )
        view.getView().activityMainRecommendViewPager2.offscreenPageLimit =3
        view.getView().activityMainRecommendViewPager2.clipToPadding =false
        view.getView().activityMainRecommendViewPager2.clipChildren =false

        //default 이미지 셋팅
        val lst = mutableListOf<AlcoholList>()
        for(i in 0 until 5){
            lst.add(AlcoholList().apply {
                type = -1
            })
        }
        view.getView().activityMainRecommendViewPager2.adapter = RecommendAlcoholAdapter(context,lst)

        //추천 주류 불러오기
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getRecommendAlcohol(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.alcoholList?.let { lst->
                    //어댑터 자체에서 갱신하면 아이템 사이즈가 커지는 문제가 발생하여
                    //새로운 어댑터를 셋팅하는 방법으로 일시 대체
                    view.getView().activityMainRecommendViewPager2.adapter = RecommendAlcoholAdapter(context,lst.toMutableList())

                }
            }, { t -> Log.e(ErrorManager.ALCHOL_RECOMMEND,t.message.toString()) }))
    }

    fun initNavigationItemSet(context: Context,activity:Activity) {
        JWTUtil.settingUserInfo(false)

        val lst = mutableListOf<NavigationItem>()
        lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
        lst.add(NavigationItem(R.mipmap.btn_top_setting, "설정"))
        lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
        lst.add(NavigationItem(R.mipmap.navigation1_img, "내가 평가한 주류"))
        lst.add(NavigationItem(R.mipmap.navigation2_img, "나의 주류 레벨"))
        lst.add(NavigationItem(R.mipmap.navigation3_img, "내가 찜한 주류"))
        lst.add(NavigationItem(R.mipmap.btn_top_setting, "-1"))
        lst.add(GlobalApplication.userInfo.getProvider()?.let { NavigationItem(R.mipmap.navigation5_img, "로그아웃") }
            ?: NavigationItem(R.mipmap.navigation5_img, "로그인"))

        view.getView().mainNavigation.navigationContainer.setHasFixedSize(true)
        view.getView().mainNavigation.navigationContainer.layoutManager = LinearLayoutManager(context)
        view.getView().mainNavigation.navigationContainer.adapter = NavigationAdpater(context,activity,lst
            ,GlobalApplication.userInfo.getProvider(),GlobalApplication.ACTIVITY_HANDLING_MAIN)
    }

    override fun initAlcoholRanking(context: Context) {
       JWTUtil.settingUserInfo(false)

        view.getView().monthlyRecylcerView.setHasFixedSize(true)
        view.getView().monthlyRecylcerView.layoutManager = LinearLayoutManager(context)

        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java).getAlcoholRanking(
            GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              view.getView().monthlyRecylcerView.adapter = AlcoholRankAdapter(context,it.data?.alcoholList?.toMutableList()!!)

            },{t ->Log.e(ErrorManager.ALCHOL_RANKING,t.message.toString())}))

    }


    override fun checkLogin(context: Context) {
        JWTUtil.settingUserInfo(false)

        GlobalApplication.userInfo.getProvider()?.let {
            //유저 프로필 설정하는 화면 필요함
            view.getView().mainDrawerLayout.main_navigation.navigation_header_Name.text=
                GlobalApplication.userInfo.nickName + "님,"
            view.getView().mainDrawerLayout.main_navigation.navigation_header_hello.text = "안녕하세요"
        }
        GlobalApplication.userInfo.getProfile()?.let {lst->
            if(lst.isNotEmpty()){
                //가장 최근에 업데이트한 이미지는 리스트의 마지막 번째에 존재함.
               Glide.with(context)
                   .load(lst[lst.size-1].mediaResource?.small?.src.toString())
                   .apply(
                       RequestOptions()
                           .signature(ObjectKey("signature"))
                           .skipMemoryCache(true)
                           .diskCacheStrategy(DiskCacheStrategy.NONE)
                           .circleCrop()
                   )
                   .into(view.getView().mainNavigation.navigationHeader.navigationHeaderProfile)
            }
        }
    }

    override fun detachView() {
        compositeDisposable.dispose()
    }
}