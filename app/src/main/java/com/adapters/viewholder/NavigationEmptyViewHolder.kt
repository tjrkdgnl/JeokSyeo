package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.navigation.NavigationItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.NavigationItemBinding

/**
 * mypage에서 디자인 상, 빈공간을 만들기 위해서 사용되는 뷰홀더
 */
class NavigationEmptyViewHolder(parent:ViewGroup) : BaseViewHolder<NavigationItem,NavigationItemBinding>(R.layout.navigation_item_empty,parent) {
    override fun bind(data: NavigationItem) {

    }

}