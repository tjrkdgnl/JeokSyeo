package com.jeoksyeo.wet.activity.level

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :LevelContract.BasePresenter{

    override lateinit var view: LevelContract.BaseView

    override lateinit var context: Context

    private val compositeDisposable = CompositeDisposable()

    val miniAlcoholList = mutableListOf<ImageView>()

    lateinit var networkUtil: NetworkUtil

    override fun setNetworkUtil() {
        networkUtil = NetworkUtil(context)
        networkUtil.register()
    }

    override fun initMiniImageArray(){
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv1)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv2)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv3)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv4)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv5)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv6)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv7)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv8)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv9)
        miniAlcoholList.add(view.getView().imageViewEvaluationByMeBottleLv10)
    }

    override fun detach() {
        compositeDisposable.dispose()
    }

    override fun getMyLevel() {
        val check = JWTUtil.settingUserInfo()

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .getLevelData(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.let {info->
                        view.settingMainAlcholGIF(info.level)
                        view.settingExperience(info.reviewCount,info.level)
                        view.getView().defaultMainBottle.visibility = View.INVISIBLE
                    }
                },{
                    t -> Log.e(ErrorManager.LEVEL_INFO,t.message.toString())
                }))
        }
        else{
            view.getView().imageViewEvaluationByMeMainBottle.visibility = View.INVISIBLE

        }

    }
}