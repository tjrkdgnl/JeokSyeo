package com.adapter.alchol_category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryGridViewHolder
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alchol_detail.AlcholDetail
import com.model.alchol_category.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GridAdapter(private val context: Context
                  , private val lst:MutableList<AlcholList>):RecyclerView.Adapter<AlcholCategoryGridViewHolder>() {
    private var duplicate =false
    private lateinit var disposable:Disposable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryGridViewHolder {
        return AlcholCategoryGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryGridViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarGridRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().gridItemParentLayout.setOnClickListener {
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

    fun updateList(list:MutableList<AlcholList>){
        val newList = mutableListOf<AlcholList>()
        newList.clear()

        for(newData in list.withIndex()){
            for(previousData in lst){
                if(newData.value.equals(previousData.alcholId)){
                    duplicate=true
                    break
                }
            }
            if(!duplicate)
                newList.add(newData.value)

            duplicate=false
        }

        lst.addAll(newList)
        notifyDataSetChanged()
    }

    fun changeSort(list:MutableList<AlcholList>){
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    fun getLastAlcholId():String?{
        if(lst.size>1)
            return lst.get(lst.size-1).alcholId
        else
            return null
    }
}