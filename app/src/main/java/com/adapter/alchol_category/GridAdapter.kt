package com.adapter.alchol_category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholCategoryGridViewHolder
import com.application.GlobalApplication
import com.custom.OnSingleClickListener
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
                  , private val lst:MutableList<AlcholList>
                ,private val executeProgressBar:(Boolean)->Unit):RecyclerView.Adapter<AlcholCategoryGridViewHolder>() {
    private var duplicate =false
    private lateinit var disposable:Disposable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcholCategoryGridViewHolder {
        return AlcholCategoryGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcholCategoryGridViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarGridRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().gridItemParentLayout.setOnSingleClickListener {
            executeProgressBar(true)
            lst[position].alcholId?.let { alcholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alchol)
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
                //detail 창에서 좋아요 한 후, 화면 갱신을 위한 코드도 포함해야 함.
                holder.getViewBinding().imageViewGridHeart.setImageResource(R.mipmap.small_heart_full)
            }
            else{
                holder.getViewBinding().imageViewGridHeart.setImageResource(R.mipmap.small_heart_empty)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateList(list:MutableList<AlcholList>){
        val newList = mutableListOf<AlcholList>()
        newList.clear()
        val currentItemCount = lst.size
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
        notifyItemChanged(currentItemCount-1,newList.size)
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


    //확장함수로 중복클릭 방지
    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val onSingleClickListener = OnSingleClickListener{
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }
}