package com.adapters.navigation

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.activities.alcohol_rated.AlcoholRated
import com.activities.favorite.FavoriteActivity
import com.activities.level.LevelActivity
import com.activities.login.Login
import com.activities.login.apple.AppleLogin
import com.activities.login.google.GoogleLogin
import com.activities.login.kakao.KakaoLogin
import com.activities.login.naver.NaverLogin
import com.activities.setting.SettingActivity
import com.adapters.viewholder.NavigationEmptyViewHolder
import com.adapters.viewholder.NavigationViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.model.navigation.NavigationItem

class MyPageAdapter(
    private val activity: Activity,
    private val lst: MutableList<NavigationItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SETTING = 0
    private val RATED = 3
    private val LEVEL = 4
    private val FAVORITE = 5
    private val LOGOUT = 7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            -1 -> {
                NavigationEmptyViewHolder(parent)
            }
            1 -> {
                NavigationViewHolder(parent)
            }
            else -> {
                throw RuntimeException("알 수 없는 뷰타입 에러")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is NavigationViewHolder) {
            holder.bind(lst[position])

            holder.getViewBinding().navigationLinearLayout.setOnClickListener {
                when (position) {
                    SETTING -> {
                        checkProvider(0)
                    }
                    RATED -> {
                        checkProvider(3)
                    }
                    LEVEL -> {
                        GlobalApplication.instance.moveActivity(activity, LevelActivity::class.java)
                    }
                    FAVORITE -> {
                        checkProvider(5)
                    }
                    LOGOUT -> {
                        checkLoginOut(lst[position].title)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    private fun checkLoginOut(check: String?) {
        check?.let {
            if (it == "로그아웃") {
                when (GlobalApplication.userInfo.getProvider()) {
                    "NAVER" -> {
                        NaverLogin(activity).naverLogOut()
                    }
                    "KAKAO" -> {
                        KakaoLogin(activity).kakaoLogOut()
                    }
                    "GOOGLE" -> {
                        GoogleLogin(activity).googleLogOut()
                    }
                    "APPLE" -> {
                        AppleLogin(activity).appleSignOut()
                    }
                }
            } else {
                //로그인일 때
                GlobalApplication.instance.moveActivity(
                    activity, Login::class.java
                    , 0
                )

            }
        }
    }

    //프로바이더의 유무에 따라서 로그인 여부를 판단하고, 이후에 액티비티 전환
    private fun checkProvider(position: Int) {
        GlobalApplication.userInfo.getProvider()?.let {
            when (position) {
                SETTING -> {
                    GlobalApplication.instance.moveActivity(
                        activity,
                        SettingActivity::class.java,
                        0
                    )
                }
                RATED -> {
                    GlobalApplication.instance.moveActivity(activity, AlcoholRated::class.java)
                }
                FAVORITE -> {
                    GlobalApplication.instance.moveActivity(activity, FavoriteActivity::class.java)
                }
            }
            // 프로바이더가 없으면 로그인을 통해 프로바이더를 얻어오기 위해서 로그인화면으로 유도
        } ?: CustomDialog.loginDialog(activity, 0)
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].type
    }
}