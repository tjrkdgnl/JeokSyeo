package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.navigation.NavigationItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NavigationItemBinding

class NavigationViewHolder(parent:ViewGroup) : BaseViewHolder<NavigationItem,NavigationItemBinding>(R.layout.navigation_item,parent) {

    override fun bind(data: NavigationItem) {
        binding.navigationItem = data
        binding.executePendingBindings()

    }

}