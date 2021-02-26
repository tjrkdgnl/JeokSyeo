package com.activities.level

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter :LevelContract.LevelPresenter{

    override lateinit var view: LevelContract.LevelView

    override lateinit var activity: Activity

    private val compositeDisposable = CompositeDisposable()

    val miniAlcoholList = mutableListOf<ImageView>()

    var rankCount:Int =0


    override fun initMiniImageArray(){
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv1)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv2)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv3)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv4)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv5)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv6)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv7)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv8)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv9)
        miniAlcoholList.add(view.getBindingObj().levelBottomBottle.imageViewEvaluationByMeBottleLv10)
    }

    override fun getMyLevel() {
        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                if(check){
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .getLevelData(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            it.data?.let {info->
                                view.settingMainAlcholGIF(info.level)
                                view.settingExperience(info.reviewCount,info.level)
                                view.getBindingObj().defaultMainBottle.visibility = View.INVISIBLE

                                rankCount = info.level5Rank

                            }
                        },{ t ->
                            CustomDialog.networkErrorDialog(activity)
                            Log.e(ErrorManager.LEVEL_INFO,t.message.toString())
                        }))
                }
                else{
                    view.getBindingObj().imageViewEvaluationByMeMainBottle.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun detach() {
        compositeDisposable.dispose()
    }
}