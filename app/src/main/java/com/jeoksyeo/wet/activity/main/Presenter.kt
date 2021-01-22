package com.jeoksyeo.wet.activity.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.application.GlobalApplication
import com.error.ErrorManager
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.service.ApiGenerator
import com.service.ApiService
import com.service.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter:Contract.BasePresenter {

    override lateinit var view: Contract.BaseView

    override lateinit var activity: FragmentActivity

    lateinit var networkUtil:NetworkUtil

    val compositeDisposable =CompositeDisposable()

    override fun bottomNavigationVisiblity(check: Int) {
        if(check ==1){
            view.getBinding().navigationBottomBar
                .animate()
                .translationY(300f)
                .setDuration(300)
                .startDelay =300
        }
        else if(check ==0) {
            view.getBinding().navigationBottomBar
                .animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }

    override fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(activity)
            networkUtil.register()
        }
    }

    override fun getAlcohol(alcoholId: String) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
            .getAlcoholDetail(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(),alcoholId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({alcohol->
                val intent = Intent(activity, AlcoholDetail::class.java)
                val bundle = Bundle()
                bundle.putParcelable(
                    GlobalApplication.MOVE_ALCHOL,
                    alcohol.data?.alcohol
                )
                intent.putExtra(GlobalApplication.ALCHOL_BUNDLE, bundle)

                GlobalApplication.instance.moveActivity(activity
                    , AlcoholDetail::class.java,
                    0,bundle, GlobalApplication.ALCHOL_BUNDLE,0)

            },{t->
                Log.e(ErrorManager.DEEP_LINK,t.message.toString())}))

    }

//    //링크에 대한 url 내용물 구성하기
//     fun checkDeepLink(): Uri {
//        val promoteCode = "bbb0f754-cf69-484f-96dc-e9a7b51e9c66"
//        val SEGMENT_PROMOTION = "alcoholDetail"
//        val KEY_CODE = "alcoholID"
//        return Uri.parse("https://jeoksyeo.com/ ${SEGMENT_PROMOTION}?${KEY_CODE}=${promoteCode}")
//    }
//
//    //url을 가지고서 링크를 만들기
//     fun createDynamicLink() {
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

    //링크를 타고 들어왔을 때 핸들링
    override fun handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(activity.intent)
            .addOnSuccessListener { pendingDynamicData ->
                //링크를 안타고 들어오면 null, 링크를 타서 들어오면 not null

                pendingDynamicData?.let { data->
                    val deepLink = data.link

//                Log.e("query",deepLink?.getQueryParameter("alcoholID").toString())

                    deepLink?.let {  segment ->
                        //링크를 타고 들어왔을 때, 해당 Link url을 받아서 분기하기
                        Log.e("lastSegment", segment.lastPathSegment.toString())
                        Log.e("alcoholId", deepLink.getQueryParameter("alcoholID").toString())

                        getAlcohol(deepLink.getQueryParameter("alcoholID").toString())
                    }
                }
            }
    }

    override fun detachView() {
        compositeDisposable.dispose()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
    }
}