package com.adapter.alcholdetail

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholReviewViewHolder
import com.adapter.viewholder.NoAlcholReviewViewHolder
import com.model.alchol_detail.Alchol
import com.model.review.ReviewList
import com.vuforia.engine.wet.R
import java.lang.RuntimeException

class AlcholReviewAdapter(private val context: Context,private val alcholId: String?, private val lst: MutableList<ReviewList>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //viewType을 위한 상수값
    private val NO_ITEM = 0
    private val ITEM = 1

    //좋아요, 싫어요 여부 체크리스트
    private val likeList = mutableListOf<Boolean>()
    private val disLikeList = mutableListOf<Boolean>()

    init {
        for(idx  in 0..lst.size){
            likeList.add(false)
            disLikeList.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM -> {
                AlcholReviewViewHolder(context,parent)
            }
            NO_ITEM -> {
                NoAlcholReviewViewHolder(parent)
            }
            else -> throw RuntimeException("알수 없는 viewtype 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlcholReviewViewHolder) {
            holder.bind(lst[position])

            lst[position].has_like?.let {
                if(it){
                    holder.getViewBinding().imageViewRecommendUpButton.setImageResource(R.mipmap.like_full)
                    likeList[position] =true }
            }
            lst[position].has_dislike?.let {
                if(it){
                    holder.getViewBinding().imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_full)
                    disLikeList[position]=true }
            }

            holder.getViewBinding().imageViewRecommendUpButton.setOnClickListener{
                if(!likeList[position]){
                    likeList[position] =true
                    holder.setLike(alcholId,lst[position],disLikeList,position)
                }
                else{
                    holder.setUnlike(alcholId,lst[position])
                    likeList[position] =false
                }
            }
            holder.getViewBinding().imaveViewRecommendDownButton.setOnClickListener{
                if(!disLikeList[position]){
                    disLikeList[position]=true
                    holder.setDislike(alcholId,lst[position],likeList,position)
                }
                else{
                    holder.setUnDislike(alcholId,lst[position])
                    disLikeList[position]=false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].nickname?.let { ITEM } ?: NO_ITEM
    }
}