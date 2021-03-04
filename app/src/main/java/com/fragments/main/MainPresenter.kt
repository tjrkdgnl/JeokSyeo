package com.fragments.main

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.main.AlcoholRankAdapter
import com.adapters.main.BannerAdapter
import com.adapters.main.RecommendAlcoholAdapter
import com.application.GlobalApplication
import com.custom.ViewPagerTransformer
import com.error.ErrorManager
import com.model.banner.Banner
import com.model.recommend_alcohol.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class MainPresenter : MainContract.MainPresenter {
    override lateinit var activity: Activity
    override val view: MainContract.MainView by lazy {
        viewObj!!
    }
    var bannerItem: Int =0
    override var viewObj: MainContract.MainView? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var bannerAdapter: BannerAdapter

    //parent layout이 scrollview이기 때문에 각 영역마다 로빙화면을 셋팅
    private val recommendProgressBar: (Boolean) -> Unit = { check ->
        if (check) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getBindingObj().recommendProgressbar.root.visibility = View.VISIBLE
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getBindingObj().recommendProgressbar.root.visibility = View.INVISIBLE
        }
    }

    //parent layout이 scrollview이기 때문에 각 영역마다 로빙화면을 셋팅
    private val monthlyProgressBar: (Boolean) -> Unit = { check ->
        if (check) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getBindingObj().monthlyProgressbar.root.visibility = View.VISIBLE
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getBindingObj().monthlyProgressbar.root.visibility = View.INVISIBLE
        }
    }

    override fun initBanner() {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                compositeDisposable.add(
                    ApiGenerator.retrofit.create(ApiService::class.java)
                        .getBannerData(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken()
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //좌우로 넘어가는 뷰페이저를 만들기 위해서 배너 아이템의 100배를 곱한 수 만큼 데이터 추가
                            //예를들어 배너 아이템이 2개면 200개의 중복 데이터를 만들어서 100번째에서 뷰페이저 시작
                            it.data?.banner?.let { lst ->
                                 bannerItem= lst.size
                                val infiniteLst = mutableListOf<Banner>()
                                var i = 0
                                while (i != bannerItem * 100) {
                                    infiniteLst.addAll(lst)
                                    i++
                                }
                                bannerAdapter = BannerAdapter(activity, infiniteLst)
                                view.getBindingObj().mainBanner.adapter = bannerAdapter
                                view.getBindingObj().mainBanner.currentItem = bannerItem * 50

                                if(bannerItem!=0){
                                    view.getBindingObj().bannerCount.text =
                                        "${view.getBindingObj().mainBanner.currentItem % bannerItem + 1} / $bannerItem"
                                }
                            }
                        }, { t ->
                            Log.e(ErrorManager.BANNER, t.message.toString())
                        })
                )
            }
        }
    }

    override fun bannerNumber(position: Int) {
        if(bannerItem !=0){
            view.getBindingObj().bannerCount.text =
                "${position % bannerItem + 1} / $bannerItem"
        }
    }

    fun autoSlide() {
        compositeDisposable.add( //6초 간격으로 자동으로 뷰페이저 슬라이드
            Observable.interval(6000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.getBindingObj().mainBanner.currentItem =
                        view.getBindingObj().mainBanner.currentItem + 1

                }, { t ->
                    Log.e("banner auto slide", t.message.toString())
                })
        )
    }

    override fun initRecommendViewPager() {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                //뷰페이저 슬라이딩 애니메이션 셋팅
                view.getBindingObj().activityMainRecommendViewPager2.setPageTransformer(
                    ViewPagerTransformer(activity)
                )
                view.getBindingObj().activityMainRecommendViewPager2.offscreenPageLimit = 3
                view.getBindingObj().activityMainRecommendViewPager2.clipToPadding = false
                view.getBindingObj().activityMainRecommendViewPager2.clipChildren = false

                //default 이미지 셋팅
                //만약 네트워크 문제로 인해 추천주류 리싸이클러뷰 아이템 갱신이 되지 않을 때, 기본적인
                //이미지가 나올 수 있도록 셋팅
                val lst = mutableListOf<AlcoholList>()
                for (i in 0 until 5) {
                    lst.add(AlcoholList().apply {
                        type = -1
                    })
                }
                view.getBindingObj().activityMainRecommendViewPager2.adapter =
                    RecommendAlcoholAdapter(activity, lst, recommendProgressBar)

                //추천 주류 불러오기
                compositeDisposable.add(
                    ApiGenerator.retrofit.create(ApiService::class.java)
                        .getRecommendAlcohol(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            it.data?.alcoholList?.let { lst ->
                                //어댑터 자체에서 갱신하면 아이템 사이즈가 커지는 문제가 발생하여
                                //새로운 어댑터를 셋팅하는 방법으로 일시 대체
                                view.getBindingObj().activityMainRecommendViewPager2.adapter =
                                    RecommendAlcoholAdapter(
                                        activity,
                                        lst.toMutableList(),
                                        recommendProgressBar
                                    )
                            }
                        }, { t ->
                            Log.e(ErrorManager.ALCHOL_RECOMMEND, t.message.toString())
                        })
                )
            }
        }
    }

    override fun initAlcoholRanking() {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                compositeDisposable.add(
                    ApiGenerator.retrofit.create(ApiService::class.java)
                        .getAlcoholRanking(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken()
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            view.getBindingObj().monthlyRecylcerView.adapter =
                                AlcoholRankAdapter(
                                    activity,
                                    it.data?.alcoholList?.toMutableList()!!,
                                    monthlyProgressBar
                                )
                            view.getBindingObj().monthlyRecylcerView.setHasFixedSize(true)
                            view.getBindingObj().monthlyRecylcerView.layoutManager =
                                LinearLayoutManager(activity)

                        }, { t ->
                            Log.e(ErrorManager.ALCHOL_RANKING, t.message.toString())
                        })
                )
            }
        }
    }

    override fun detachView() {
        compositeDisposable.dispose()
        viewObj = null
    }
}