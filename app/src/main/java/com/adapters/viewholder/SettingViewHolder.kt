package com.adapters.viewholder

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.ViewGroup
import com.base.BaseViewHolder
import com.model.setting.SettingItem
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingItemBinding

/**
 * 설정화면에서 표시될 아이템 뷰홀더
 */
class SettingViewHolder(val parent:ViewGroup)
    : BaseViewHolder<SettingItem,SettingItemBinding>(R.layout.setting_item,parent) {

    @SuppressLint("SetTextI18n")
    override fun bind(data: SettingItem) {
        binding.settingName = data.name
        binding.executePendingBindings()

        //만약 아이템 이름이 "앱버전인 경우, 현재 버전을 가져와서 셋팅
        if(data.name =="앱버전"){
            val version = parent.context.packageManager.getPackageInfo(parent.context.packageName, PackageManager.GET_META_DATA).versionName.split(".")
            binding.settingVersionText.text = "V ${version[0]}.${version[1]}.${version[2]}"
        }
    }
}