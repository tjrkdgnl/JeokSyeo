package com.adapter.alcholdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.model.review.ReviewList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NoReviewItemBinding

class NoAlcholReviewAdapter(private val lst:MutableList<ReviewList>):RecyclerView.Adapter<NoAlcholReviewAdapter.MyViewHolder>() {
    private lateinit var binding:NoReviewItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoAlcholReviewAdapter.MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.no_review_item,parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoAlcholReviewAdapter.MyViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    inner class MyViewHolder(view:NoReviewItemBinding) :RecyclerView.ViewHolder(view.root){

    }
}