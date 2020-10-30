package com.jeoksyeo.wet.activity.alchol_detail

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.alcholdetail.AlcholComponentAdapter
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.model.alchol_detail.Alchol
import com.model.alchol_detail.AlcholComponentData
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

    override fun initComponent(context:Context,alchol: Alchol) {
        //SRM value에 따른 색 지정하기
        //SRM value가 무조건 정수는 아니기 때문에 try/catch로 parsing에러 잡아서 핸들링하기
        alchol.more?.let {
            val lst = mutableListOf(
                AlcholComponentData("IBU",it.ibu), AlcholComponentData("SRM",it.srm),
                AlcholComponentData("HOP",it.hop),AlcholComponentData("MALT",it.malt)
            )
            view.getView().alcholComponentRecyclerView.adapter=AlcholComponentAdapter(lst)
            view.getView().alcholComponentRecyclerView.setHasFixedSize(true)
            view.getView().alcholComponentRecyclerView.addItemDecoration(GridSpacingItemDecoration(2,4,true,0))
            view.getView().alcholComponentRecyclerView.layoutManager = GridLayoutManager(context,2)
        }
    }
}