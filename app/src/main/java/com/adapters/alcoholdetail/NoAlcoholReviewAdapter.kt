package com.adapters.alcoholdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.model.review.ReviewList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoReviewItemBinding

/**
 * 주류에 대한 리뷰가 없을 때 표시되는 기본 어댑터
 */
class NoAlcoholReviewAdapter(private val lst:MutableList<ReviewList>):RecyclerView.Adapter<NoAlcoholReviewAdapter.MyViewHolder>() {
    private lateinit var binding:NoReviewItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoAlcoholReviewAdapter.MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.no_review_item,parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoAlcoholReviewAdapter.MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    inner class MyViewHolder(view:NoReviewItemBinding) :RecyclerView.ViewHolder(view.root){

    }
}