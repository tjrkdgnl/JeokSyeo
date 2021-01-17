package com.fragment.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.TypedValue
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.navigation.NavigationAdpater
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.model.navigation.NavigationItem
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPagePresenter:MypageContract.BasePresenter {
    override lateinit var baseView: MypageContract.BaseView
    override lateinit var activity: Activity
    private lateinit var adapter: NavigationAdpater

    override fun initTextSize() {
        baseView.getViewBinding().helpEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        baseView.getViewBinding().helpTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        baseView.getViewBinding().helpHoliyday.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        baseView.getViewBinding().myPageHeader.navigationHeaderHello.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))
        baseView.getViewBinding().myPageHeader.navigationHeaderName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(30f))
    }

    override fun initRecyclerView() {
        val lst = mutableListOf<NavigationItem>()
        lst.add(NavigationItem(1,"설정",true))
        lst.add(NavigationItem(-1,"",false))
        lst.add(NavigationItem(1,"테이스트 저널",true))
        lst.add(NavigationItem(1,"내가 평가한 주류",false))
        lst.add(NavigationItem(1,"나의 주류 레벨",false))
        lst.add(NavigationItem(1,"내가 찜한 주류",false))
        lst.add(NavigationItem(-1,"",false))
        GlobalApplication.userInfo.getProvider()?.let {
            lst.add(NavigationItem(1,"로그아웃",true))
        } ?: lst.add((NavigationItem(1,"로그인",true)))
        lst.add(NavigationItem(-1,"",false))
        lst.add(NavigationItem(1,"Contact Us",true))

        adapter = NavigationAdpater(activity,lst)

        baseView.getViewBinding().myPageRecyclerView.adapter = adapter
        baseView.getViewBinding().myPageRecyclerView.setHasFixedSize(true)
        baseView.getViewBinding().myPageRecyclerView.layoutManager= LinearLayoutManager(activity)
    }

    @SuppressLint("SetTextI18n")
    override fun checkLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main){
                GlobalApplication.userInfo.getProvider()?.let {
                    //유저 프로필 설정하는 화면 필요함
                    val level = GlobalApplication.userInfo.getLevel()
                    baseView.getViewBinding().myPageHeader.navigationHeaderName.text = GlobalApplication.userInfo.nickName
                    baseView.getViewBinding().myPageHeader.navigationHeaderHello.text = "level.${level} ${GlobalApplication.instance.getLevelName(level-1)}"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        baseView.getViewBinding().myPageHeader.navigationHeaderHello.setTextColor(activity.resources.getColor(
                            R.color.orange,null))
                    }
                }
                GlobalApplication.userInfo.getProfile()?.let { lst->
                    if(lst.isNotEmpty()){
                        Glide.with(activity)
                            .load(lst[lst.size - 1].mediaResource?.small?.src.toString())
                            .apply(
                                RequestOptions()
                                    .signature(ObjectKey(System.currentTimeMillis()))
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .circleCrop()
                            )
                            .into(baseView.getViewBinding().myPageHeader.navigationHeaderProfile)
                    }
                }
            }
        }
    }
}