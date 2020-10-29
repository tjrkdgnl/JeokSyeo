package com.jeoksyeo.wet.activity.alchol_detail

import android.util.Log
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :AlcholDetailContract.BasePresenter {
    override lateinit var view: AlcholDetailContract.BaseView
     private  var compositeDisposable =CompositeDisposable()

    override fun executeLike(alcholId:String) {
        compositeDisposable.add( ApiGenerator.retrofit.create(ApiService::class.java)
            .alcholLike(GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken()
            ,alcholId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    view.setLike(true)
            },{t-> Log.e(ErrorManager.ALCHOL_LIKE,t.message.toString()) }))
    }

    override fun cancelAlcholLike(alcholId: String) {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .cancelAlcholLike(GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(),alcholId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    view.setLike(false)
            },{t-> Log.e(ErrorManager.ALCHOL_CANCEL_LIKE,t.message.toString()) }))
    }
}