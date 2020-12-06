package com.adapter.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.RecommendAlcoholViewHolder
import com.adapter.viewholder.RecommendEmptyAlcoholViewHolder
import com.application.GlobalApplication
import com.custom.OneClickListener
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.model.recommend_alcohol.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecommendAlcoholAdapter(private val context: Context,
private var lst:MutableList<AlcoholList>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var disposable: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        return when(viewType){
            -1 ->{RecommendEmptyAlcoholViewHolder(parent)}
            1->{RecommendAlcoholViewHolder(parent)}
            else->{throw RuntimeException("알 수 없는 뷰타입 에러")}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is RecommendAlcoholViewHolder){
            holder.bind(lst.get(position))

            holder.getViewBinding().recommendParentLayout.setOnSingleClickListener{
                JWTUtil.settingUserInfo()

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
    }

    fun updateList(list:MutableList<AlcoholList>){
        lst.clear()
        lst.addAll(list)
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()

    }

    override fun getItemViewType(position: Int): Int {
        return if(lst[position].type != 1) -1 else 1
    }

    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val setOnSingleClickListener = OneClickListener{
            onSingleClick(it)
        }
        setOnClickListener(setOnSingleClickListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        disposable?.dispose()
    }
}