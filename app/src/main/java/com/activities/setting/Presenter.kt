package com.activities.setting

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.navigation.SettingAdapter
import com.application.GlobalApplication
import com.model.setting.SettingItem

class Presenter : SettingContract.SettingPresenter {
    override lateinit var view: SettingContract.SettingView
    override lateinit var activity: Activity

    override fun initItem(context: Context, activity:Activity) {
        val lst = listOf<SettingItem>(
            SettingItem("회원정보 수정", 1),
            SettingItem("이용약관", 1),
            SettingItem("개인정보 취급방침", 1),
            SettingItem("앱버전",0),
            SettingItem("회원탈퇴", 0))

        view.getBindingObj().settingRecylcerView.adapter = SettingAdapter(context,activity,lst.toMutableList(),GlobalApplication.userInfo.getProvider())
        view.getBindingObj().settingRecylcerView.setHasFixedSize(true)
        view.getBindingObj().settingRecylcerView.layoutManager = LinearLayoutManager(context)

    }

    override fun detachView() {

    }
}