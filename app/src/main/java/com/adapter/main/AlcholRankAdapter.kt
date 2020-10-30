package com.adapter.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholRankViewHolder
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alchol_detail.AlcholDetail
import com.model.alchol_ranking.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AlcholRankAdapter(
    private val context: Context,
    private val lst:MutableList<AlcholList>
) : RecyclerView.Adapter<AlcholRankViewHolder>() {

    private var disposable: Disposable? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholRankViewHolder {
        return AlcholRankViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlcholRankViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().rankingText.text = (position+1).toString()

        holder.getViewBinding().alcholRankParentLayout.setOnClickListener{
            disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholDetail(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    lst[position].alcholId!!)
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

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }
}