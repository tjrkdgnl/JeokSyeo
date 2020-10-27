package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.setting.SettingItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingItemBinding

class SettingViewHolder(parent:ViewGroup)
    : BaseViewHolder<SettingItem,SettingItemBinding>(R.layout.setting_item,parent) {

    override fun bind(data: SettingItem) {
        binding.settingName = data.name
        binding.executePendingBindings()
    }
}