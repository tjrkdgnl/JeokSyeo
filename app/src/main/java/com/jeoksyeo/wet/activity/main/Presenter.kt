package com.jeoksyeo.wet.activity.main

import android.content.Context
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
import com.service.JWTUtil
import com.service.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class Presenter:Contract.BasePresenter {

    override lateinit var view: Contract.BaseView

    override lateinit var activity: FragmentActivity

    lateinit var networkUtil:NetworkUtil

    val compositeDisposable =CompositeDisposable()


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