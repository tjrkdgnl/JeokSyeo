package com.adapter.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.RecommendAlcholViewHolder
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alchol_detail.AlcholDetail
import com.model.recommend_alchol.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecommendAlcholAdapter(private val context: Context,
private var lst:MutableList<AlcholList>) : RecyclerView.Adapter<RecommendAlcholViewHolder>() {

    private var disposable: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecommendAlcholViewHolder {
        return RecommendAlcholViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecommendAlcholViewHolder, position: Int) {
        holder.bind(lst.get(position))

        holder.getViewBinding().recommendParentLayout.setOnClickListener{
            lst[position].alcholId?.let {alcholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alchol)
                        GlobalApplication.instance.moveActivity(context,AlcholDetail::class.java
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
}