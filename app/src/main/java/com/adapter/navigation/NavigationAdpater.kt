package com.adapter.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.NavigationViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.jeoksyeo.wet.activity.alchol_rated.AlcholRated
import com.jeoksyeo.wet.activity.editprofile.EditProfile
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.jeoksyeo.wet.activity.setting.SettingActivity
import com.model.navigation.NavigationItem
import com.vuforia.engine.wet.R

class NavigationAdpater(
    private val context: Context,
    private val activity:Activity,
    private val lst: MutableList<NavigationItem>,
    private var provider:String?,
    private val activityNumber:Int
) : RecyclerView.Adapter<NavigationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().navigationLinearLayout.setOnClickListener {
        Log.e("네이게이션",activityNumber.toString())
            when (position) {
                0 -> {
                    if(context is Activity){
                        GlobalApplication.instance.moveActivity(context,SettingActivity::class.java,0)
                    }

                }
                1 -> {checkProvider(1)}
                2 -> {checkProvider(2) }
                3 -> {checkProvider(3) }
                4 -> {checkProvider(4)}
                5 -> {checkProvider(5) }
                6 ->  checkLoginOut(lst[position].title)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    private fun checkLoginOut(check: String?) {
        check?.let {
            if (it.equals("로그아웃")) {
                when (provider) {
                    "NAVER" -> { NaverLogin(context).naverLogOut() }
                    "KAKAO" -> { KakaoLogin(context).kakaoLogOut() }
                    "GOOGLE" -> { GoogleLogin(context,activity).googleLogOut() }
                    "APPLE" -> { AppleLogin(context,activity).appleSignOut() }
                }
            } else {
                //로그인일 때
                if(context is Activity){
                    val bundle = Bundle()
                    bundle.putInt(GlobalApplication.ACTIVITY_HANDLING,activityNumber)
                    GlobalApplication.instance.moveActivity(context,Login::class.java
                        ,0,bundle,GlobalApplication.ACTIVITY_HANDLING_BUNDLE)
                }
            }
        }
    }

    //프로바이더의 유무에 따라서 로그인 여부를 판단하고, 이후에 액티비티 전환
    private fun checkProvider(position: Int){
        provider?.let {
            when(position){
                1 -> { GlobalApplication.instance.moveActivity(context,EditProfile::class.java) }
                2 -> { GlobalApplication.instance.moveActivity(context,AlcholRated::class.java)}
                3 -> {}
                4 -> {}
                5 -> {}
            }

            // 프로바이더가 없으면 로그인을 통해 프로바이더를 얻어오기 위해서 로그인화면으로 유도
        } ?: CustomDialog.loginDialog(context,activityNumber)
    }
}