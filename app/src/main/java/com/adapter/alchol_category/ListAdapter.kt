package com.adapter.alchol_category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryListViewHolder
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

class ListAdapter(private val context: Context,private val lst:MutableList<AlcholList>):RecyclerView.Adapter<AlcholCategoryListViewHolder>() {
    private var disposable: Disposable? =null
    private var duplicate =false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryListViewHolder {
        return AlcholCategoryListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryListViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarListRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().listItemParentLayout.setOnClickListener{
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

    fun updateList(list:MutableList<AlcholList>){
        val newlist = mutableListOf<AlcholList>()

        for(newData in list.withIndex()){
            for(previousData in lst){
                if(newData.value.equals(previousData.alcholId)){
                    duplicate =true
                    break }
            }

            if(!duplicate){
                newlist.add(newData.value)
            }

            duplicate=false
        }
        lst.addAll(newlist)
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