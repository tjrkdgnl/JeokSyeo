package com.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<D,T:ViewDataBinding>(@LayoutRes layoutRes:Int, parent:ViewGroup)
    : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes,parent,false)) {

    protected var binding: T = DataBindingUtil.bind(itemView)!!

    abstract fun bind(data:D)

    fun getViewBinding(): T {
        return DataBindingUtil.bind(itemView)!!
    }
}