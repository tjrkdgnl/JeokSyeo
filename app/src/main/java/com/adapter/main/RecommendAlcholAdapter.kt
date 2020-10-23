package com.adapter.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.model.recommend_alchol.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RecommendAlcholItemBinding

class RecommendAlcholAdapter(private val context: Context,
private var lst:MutableList<AlcholList>?) : RecyclerView.Adapter<RecommendAlcholAdapter.MyViewHolder>() {
    private lateinit var binding: RecommendAlcholItemBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendAlcholAdapter.MyViewHolder {
       binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.recommend_alchol_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendAlcholAdapter.MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return lst?.size ?:0
    }

    inner class MyViewHolder(private val view:RecommendAlcholItemBinding) :RecyclerView.ViewHolder(view.root){

    }
}