package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.navigation.NavigationItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NavigationItemBinding

class NavigationEmptyViewHolder(parent:ViewGroup) : BaseViewHolder<NavigationItem,NavigationItemBinding>(R.layout.navigation_item_empty,parent) {
    override fun bind(data: NavigationItem) {

    }

}