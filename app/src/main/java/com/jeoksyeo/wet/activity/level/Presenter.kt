package com.jeoksyeo.wet.activity.level

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
import com.service.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter :LevelContract.BasePresenter{

    override lateinit var view: LevelContract.BaseView

    override lateinit var context: Context

    private val compositeDisposable = CompositeDisposable()

     val miniAlcoholList = mutableListOf<ImageView>()

    lateinit var networkUtil: NetworkUtil

    var rankCount:Int =0

    override fun setNetworkUtil() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(context)
            networkUtil.register()
        }
    }

    override fun initMiniImageArray(){
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv1)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv2)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv3)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv4)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv5)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv6)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv7)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv8)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv9)
        miniAlcoholList.add(view.getView().levelBottomBottle.imageViewEvaluationByMeBottleLv10)
    }

    override fun detach() {
        compositeDisposable.dispose()
    }

    override fun getMyLevel() {
        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkToken()

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
                                view.getView().defaultMainBottle.visibility = View.INVISIBLE

                                rankCount = info.level5Rank

                            }
                        },{ t ->
                            CustomDialog.networkErrorDialog(context)
                            Log.e(ErrorManager.LEVEL_INFO,t.message.toString())
                        }))
                }
                else{
                    view.getView().imageViewEvaluationByMeMainBottle.visibility = View.INVISIBLE
                }
            }
        }
    }
}