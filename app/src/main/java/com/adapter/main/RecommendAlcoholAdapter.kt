package com.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.RecommendAlcoholViewHolder
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.custom.OnSingleClickListener
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.model.recommend_alcohol.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecommendAlcoholAdapter(private val context: Context,
private var lst:MutableList<AlcoholList>) : RecyclerView.Adapter<RecommendAlcoholViewHolder>() {

    private var disposable: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecommendAlcoholViewHolder {
        return RecommendAlcoholViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecommendAlcoholViewHolder, position: Int) {
        holder.bind(lst.get(position))

        holder.getViewBinding().recommendParentLayout.setOnSingleClickListener{
            JWTUtil.settingUserInfo(false)

            lst[position].alcoholId?.let {alcoholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcoholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alcohol)
                        GlobalApplication.instance.moveActivity(context,AlcoholDetail::class.java
                            ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                    },{t->Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString())})
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()

    }

    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val setOnSingleClickListener = OnSingleClickListener{
            onSingleClick(it)
        }
        setOnClickListener(setOnSingleClickListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        disposable?.dispose()
    }
}