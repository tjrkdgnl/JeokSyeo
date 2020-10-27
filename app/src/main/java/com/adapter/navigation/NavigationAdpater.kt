package com.adapter.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NavigationViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.jeoksyeo.wet.activity.editprofile.EditProfile
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.navigation.NavigationItem

class NavigationAdpater(
    private val context: Context,
    private val activity:Activity,
    private val lst: MutableList<NavigationItem>,
    private var provider:String?
) : RecyclerView.Adapter<NavigationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().navigationLinearLayout.setOnClickListener {

            when (position) {
                0 -> { }
                1 -> {plzLogin(1)}
                2 -> {plzLogin(2) }
                3 -> {plzLogin(3) }
                4 -> {plzLogin(4)}
                5 -> {plzLogin(5) }
                6 ->  checkLogin(lst[position].title)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    private fun checkLogin(check: String?) {
        check?.let {
            if (it.equals("로그아웃")) {
                when (provider) {
                    "NAVER" -> { NaverLogin(context).naverLogOut(context) }
                    "KAKAO" -> { KakaoLogin(context).kakaoLogOut() }
                    "GOOGLE" -> { GoogleLogin(context,activity).googleLogOut() }
                    "APPLE" -> { AppleLogin(context,activity).appleSignOut() }
                }

            } else {
                context.startActivity(Intent(context, Login::class.java))
            }
        }
    }

    private fun plzLogin(position: Int){
        provider?.let {
            when(position){
                1 -> context.startActivity(Intent(context, EditProfile::class.java))
                2 -> {}
                3 -> {}
                4 -> {}
                5 -> {}
            }

        } ?: CustomDialog.loginDialog(context,0)
    }

}