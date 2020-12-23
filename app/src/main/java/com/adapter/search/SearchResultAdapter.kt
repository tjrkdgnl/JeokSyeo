package com.adapter.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.SearchListViewholder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.model.alcohol_category.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchResultAdapter(private val context: Context,
                          private val lst:MutableList<AlcoholList>,
                          private val executeProgressBar:(Boolean)->Unit
):RecyclerView.Adapter<SearchListViewholder>() {

    private var disposable: Disposable? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewholder {
        return SearchListViewholder(parent)
    }

    override fun onBindViewHolder(holder: SearchListViewholder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarListRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().listItemParentLayout.setOnSingleClickListener{
            executeProgressBar(true)
            lst[position].alcoholId?.let {alcholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alcohol)

                        GlobalApplication.instance.moveActivity(context,AlcoholDetail::class.java
                            ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                        executeProgressBar(false)
                    },{t->
                        CustomDialog.networkErrorDialog(context)
                        executeProgressBar(false)
                        Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString())})
            }
        }

        lst[position].isLiked?.let {
            if(it ){
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.list_heart_full)
            }
            else{
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.list_heart_empty)

            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateList(list:MutableList<AlcoholList>){
        var duplicate =false
        val newlist = mutableListOf<AlcoholList>()
        val currentSize = lst.size

        for(newData in list.withIndex()){
            for(previousData in lst){
                if(newData.value.equals(previousData.alcoholId)){
                    duplicate =true
                    break }
            }

            if(!duplicate){
                newlist.add(newData.value)
            }

            duplicate=false
        }
        lst.addAll(newlist)
        notifyItemChanged(currentSize,newlist.size)
    }

    fun changeSort(list:MutableList<AlcoholList>){
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    fun getLastAlcoholId():String?{
        if(lst.size>1)
            return lst.get(lst.size-1).alcoholId
        else
            return null
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }

    //중복 클릭을 방지하기 위해서 확장함수 선언
    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val onSingleClickListener =OneClickListener{
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        disposable?.dispose()
    }
}