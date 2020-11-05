package com.adapter.alchol_category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryListViewHolder
import com.application.GlobalApplication
import com.custom.OnSingleClickListener
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alchol_detail.AlcholDetail
import com.model.alchol_category.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ListAdapter(private val context: Context,
                  private val categoryPosition:Int,
                  private val lst:MutableList<AlcholList>,
                  private val executeProgressBar:(Boolean)->Unit
):RecyclerView.Adapter<AlcholCategoryListViewHolder>() {

    private var disposable: Disposable? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryListViewHolder {
        return AlcholCategoryListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryListViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarListRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().listItemParentLayout.setOnSingleClickListener{
            executeProgressBar(true)
            lst[position].alcholId?.let {alcholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alchol)
                        bundle.putInt(GlobalApplication.CATEGORY_POSITION,categoryPosition)
                        val intent = Intent(context,AlcholDetail::class.java)
                        intent.putExtra(GlobalApplication.ALCHOL_BUNDLE,bundle)
                        GlobalApplication.instance.moveActivity(context,AlcholDetail::class.java
                            ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                        executeProgressBar(false)
                    },{t->
                        executeProgressBar(false)
                        Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString())})
            }
        }

        lst[position].isLiked?.let {
            if(it ){
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.small_heart_full)
            }
            else{
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.small_heart_empty)

            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateList(list:MutableList<AlcholList>){
        var duplicate =false
        val newlist = mutableListOf<AlcholList>()
        val currentSize = lst.size

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
        notifyItemChanged(currentSize-1,newlist.size)
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }

    //중복 클릭을 방지하기 위해서 확장함수 선언
    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val onSingleClickListener =OnSingleClickListener{
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        disposable?.dispose()
    }
}