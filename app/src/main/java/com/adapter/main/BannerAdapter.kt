package com.adapter.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.BannerItemBinding

class BannerAdapter(
    private val context: Context, private val itemList: MutableList<Int>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {

    private lateinit var binding: BannerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerAdapter.MyViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.banner_item,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerAdapter.MyViewHolder, position: Int) {
        holder.setImg(itemList[position])
        if(position == itemList.size-2){
            viewPager2.post(addItemRunnable)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyViewHolder(private val view: BannerItemBinding) :
        RecyclerView.ViewHolder(view.root) {

        init {
            view.bannerImg.setOnClickListener { v ->
                //클릭시, webView 전환
            }
        }

        fun setImg(resoureImg:Int){
            Glide.with(context)
                .load(resoureImg)
                .into(binding.bannerImg)
        }
    }

    private var addItemRunnable = Runnable {
        itemList.addAll(itemList)
    }
}