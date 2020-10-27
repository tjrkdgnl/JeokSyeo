package com.adapter.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.setting.SettingItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingAgreementItemBinding

class SettingAgreementViewHolder(parent:ViewGroup)
    :BaseViewHolder<SettingItem,SettingAgreementItemBinding>(R.layout.setting_agreement_item,parent) {

    override fun bind(data: SettingItem) {
        binding.settingName = data.name
        binding.executePendingBindings()
    }
}