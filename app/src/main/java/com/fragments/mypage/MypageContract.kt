package com.fragments.mypage

import android.app.Activity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.MypageBinding

interface MypageContract {

    interface MypageView:BaseView<MypageBinding>

    interface MypagePresenter:BasePresenter<MypageBinding>{
        val view:MypageView
        var viewObj:MypageView?

        /**
         * 디바이스 크기에 대응되는 텍스트 사이즈 설정
         */
        fun initTextSize()

        /**
         * 유저의 개인정보, 설정목록 등의 메뉴들을 보여주는 리스트 초기화
         */
        fun initMenuList()

        /**
         * 로그인 여부를 체크하여 유저의 정보 셋팅
         */
        fun checkLogin()

        /**
         * 메모리 할당해제
         */
        fun detach()
    }
}