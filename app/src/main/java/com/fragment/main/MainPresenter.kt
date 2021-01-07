package com.fragment.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.main.AlcoholRankAdapter
import com.adapter.main.BannerAdapter
import com.adapter.main.RecommendAlcoholAdapter
import com.application.GlobalApplication
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class MainPresenter : MainContract.BasePresenter {
    override lateinit var activity: FragmentActivity
    override lateinit var view: MainContract.BaseView
    private  var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private lateinit var bannerAdapter: BannerAdapter
    var bannerItem:Int =0
    private val SEGMENT_PROMOTION = "alcoholDetail"
    private val KEY_CODE = "alcoholId"


    private val recommendProgressBar:(Boolean)->Unit ={check->
        if(check){
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getViewBinding().recommendProgressbar.root.visibility= View.VISIBLE
        }
        else{
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getViewBinding().recommendProgressbar.root.visibility=View.INVISIBLE
        }
    }

    private val monthlyProgressBar:(Boolean)->Unit ={check->
        if(check){
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getViewBinding().monthlyProgressbar.root.visibility= View.VISIBLE
        }
        else{
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            view.getViewBinding().monthlyProgressbar.root.visibility=View.INVISIBLE
        }
    }

//    //링크에 대한 url 내용물 구성하기
//    override fun checkDeepLink(): Uri {
//        val promoteCode = "1234"
//        return Uri.parse("https://jeoksyeo.com/ ${SEGMENT_PROMOTION}?${KEY_CODE}=${promoteCode}")
//    }
//
//    //url을 가지고서 링크를 만들기
//    override fun createDynamicLink() {
//        val dynamicLink=  FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(checkDeepLink())
//            .setDomainUriPrefix("https://jeoksyeo.page.link")
//            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.vuforia.engine.wet").build())
//            .buildDynamicLink()
//
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLongLink(dynamicLink.uri)
//            .buildShortDynamicLink()
//            .addOnCompleteListener {task->
//                if(task.isSuccessful){
//                    val shortLink = task.result?.shortLink
//
//                    Log.e("shortLink ",shortLink.toString())
//                }
//            }
//    }

    override fun initBanner(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
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

                            view.getViewBinding().mainBanner.adapter =bannerAdapter
                            view.getViewBinding().mainBanner.currentItem = bannerItem *100

                        }
                    },{ t->
                        Log.e(ErrorManager.BANNER,t.message.toString()) })) }
            }
    }

    fun autoSlide(){
        compositeDisposable.add(Observable.interval(6000L,TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.getViewBinding().mainBanner.currentItem = view.getViewBinding().mainBanner.currentItem +1
                view.getViewBinding().bannerCount.text = "${view.getViewBinding().mainBanner.currentItem % bannerItem +1} / $bannerItem"
            },{t ->
                Log.e("banner auto slide",t.message.toString())

            }))
    }



    override fun initRecommendViewPager(context: Context)  {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
                //뷰페이저 슬라이딩 애니메이션 셋팅
                view.getViewBinding().activityMainRecommendViewPager2.setPageTransformer(
                    ViewPagerTransformer(
                        context
                    )
                )
                view.getViewBinding().activityMainRecommendViewPager2.offscreenPageLimit =3
                view.getViewBinding().activityMainRecommendViewPager2.clipToPadding =false
                view.getViewBinding().activityMainRecommendViewPager2.clipChildren =false

                //default 이미지 셋팅
                val lst = mutableListOf<AlcoholList>()
                for(i in 0 until 5){
                    lst.add(AlcoholList().apply {
                        type = -1
                    })
                }
                view.getViewBinding().activityMainRecommendViewPager2.adapter = RecommendAlcoholAdapter(context,lst,recommendProgressBar)

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
                            view.getViewBinding().activityMainRecommendViewPager2.adapter = RecommendAlcoholAdapter(context,lst.toMutableList(),recommendProgressBar)

                        }
                    }, { t ->
                        Log.e(ErrorManager.ALCHOL_RECOMMEND,t.message.toString()) }))
            }
        }
    }

    fun initNavigationItemSet(context: Context,activity:Activity) {

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
                lst.add(GlobalApplication.userInfo.getProvider()?.let { NavigationItem(R.mipmap.navigation5_img, "로그아웃") }
                    ?: NavigationItem(R.mipmap.navigation5_img, "로그인"))

//                view.getViewBinding().mainNavigation.navigationContainer.setHasFixedSize(true)
//                view.getViewBinding().mainNavigation.navigationContainer.layoutManager = LinearLayoutManager(context)
//                view.getViewBinding().mainNavigation.navigationContainer.adapter = NavigationAdpater(context,activity,lst
//                    ,GlobalApplication.userInfo.getProvider(),GlobalApplication.ACTIVITY_HANDLING_MAIN)
            }
        }
    }

    override fun initAlcoholRanking(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
                view.getViewBinding().monthlyRecylcerView.setHasFixedSize(true)
                view.getViewBinding().monthlyRecylcerView.layoutManager = LinearLayoutManager(context)

                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholRanking(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.getViewBinding().monthlyRecylcerView.adapter =
                            AlcoholRankAdapter(context, it.data?.alcoholList?.toMutableList()!!,monthlyProgressBar)

                    }, { t ->
                        Log.e(ErrorManager.ALCHOL_RANKING, t.message.toString()) })
                )
            }
        }
    }

    override fun detachView() {
        compositeDisposable.dispose()

    }
}