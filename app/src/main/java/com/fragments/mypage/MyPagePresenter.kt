package com.fragments.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.TypedValue
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.navigation.NavigationAdpater
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

class MyPagePresenter:MypageContract.MypagePresenter {
    override val view: MypageContract.MypageView by lazy {
        viewObj!!
    }
    override var viewObj: MypageContract.MypageView?? =null

    override lateinit var activity: Activity
    private lateinit var adapter: NavigationAdpater

    override fun initTextSize() {
        view.getBindingObj().helpEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        view.getBindingObj().helpTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        view.getBindingObj().helpHoliyday.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(12f))
        view.getBindingObj().myPageHeader.navigationHeaderHello.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))
        view.getBindingObj().myPageHeader.navigationHeaderName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(30f))
    }

    override fun initMenuList() {
        val lst = mutableListOf<NavigationItem>()
        lst.add(NavigationItem(1,"설정",true))

        //빈 리스트("")를 넣은 이유는 UI상 간격이 필요하기 때문에 임의로 삽입.
        lst.add(NavigationItem(-1,"",false))
        lst.add(NavigationItem(1,"테이스트 저널",true))
        lst.add(NavigationItem(1,"내가 평가한 주류",false))
        lst.add(NavigationItem(1,"나의 주류 레벨",false))
        lst.add(NavigationItem(1,"내가 찜한 주류",false))
        lst.add(NavigationItem(-1,"",false))

        //로그인과 로그아웃의 여부에 따라서 설정되는 텍스트가 다르게 보여야하므로 핸들링
        GlobalApplication.userInfo.getProvider()?.let {
            lst.add(NavigationItem(1,"로그아웃",true))
        } ?: lst.add((NavigationItem(1,"로그인",true)))
        lst.add(NavigationItem(-1,"",false))
        lst.add(NavigationItem(1,"Contact Us",true))

        adapter = NavigationAdpater(activity,lst)

        view.getBindingObj().myPageRecyclerView.adapter = adapter
        view.getBindingObj().myPageRecyclerView.setHasFixedSize(true)
        view.getBindingObj().myPageRecyclerView.layoutManager= LinearLayoutManager(activity)
    }

    @SuppressLint("SetTextI18n")
    override fun checkLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                GlobalApplication.userInfo.getProvider()?.let {
                    //유저 프로필 설정하는 화면 필요함
                    val level = GlobalApplication.userInfo.getLevel()
                    view.getBindingObj().myPageHeader.navigationHeaderName.text = GlobalApplication.userInfo.nickName

                    //app 레벨에서 관리하는 getLevelName 메서드를 통해서 유저의 레벨네임 셋팅
                    view.getBindingObj().myPageHeader.navigationHeaderHello.text = "level.${level} ${GlobalApplication.instance.getLevelName(level-1)}"

                    //레벨 색상 변경
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.getBindingObj().myPageHeader.navigationHeaderHello.setTextColor(activity.resources.getColor(
                            R.color.orange,null))
                    }
                }
                GlobalApplication.userInfo.getProfile()?.let { lst->
                    if(lst.isNotEmpty() and !activity.isFinishing){
                        Glide.with(activity)
                            .load(lst[lst.size - 1].mediaResource?.small?.src.toString())
                            .apply(
                                RequestOptions()
                                    .signature(ObjectKey(System.currentTimeMillis()))
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .circleCrop()
                            )
                            .into(view.getBindingObj().myPageHeader.navigationHeaderProfile)
                    }
                }
            }
        }
    }

    override fun detach() {
        viewObj=null
    }
}