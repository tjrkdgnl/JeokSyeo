package com.adapters.viewholder

import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.setting.SettingItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingAgreementItemBinding


/**
 * 설정화면에 표시되는 처리방침에 관한 아이템 뷰홀더
 */
class SettingAgreementViewHolder(parent:ViewGroup)
    :BaseViewHolder<SettingItem,SettingAgreementItemBinding>(R.layout.setting_agreement_item,parent) {

    override fun bind(data: SettingItem) {
        binding.settingName = data.name
        binding.executePendingBindings()
    }
}