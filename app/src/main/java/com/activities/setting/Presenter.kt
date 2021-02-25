package com.activities.setting

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.navigation.SettingAdapter
import com.application.GlobalApplication
import com.model.setting.SettingItem

class Presenter : SettingContract.BasePresenter {
    override lateinit var view: SettingContract.BaseView


    override fun initItem(context: Context,activity:Activity) {
        val lst = listOf<SettingItem>(
            SettingItem("회원정보 수정", 1),
            SettingItem("이용약관", 1),
            SettingItem("개인정보 취급방침", 1),
            SettingItem("앱버전",0),
            SettingItem("회원탈퇴", 0))

        view.getView().settingRecylcerView.adapter = SettingAdapter(context,activity,lst.toMutableList(),GlobalApplication.userInfo.getProvider())
        view.getView().settingRecylcerView.setHasFixedSize(true)
        view.getView().settingRecylcerView.layoutManager = LinearLayoutManager(context)

    }

    override fun detachView() {

    }
}