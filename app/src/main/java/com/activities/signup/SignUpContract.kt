package com.activities.signup

import android.widget.Button
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.ActivitySignupBinding

interface SignUpContract {

    interface SignUpView:BaseView<ActivitySignupBinding>{
        /**
         * 유저가 입력한 정보를 저장 후, 다음 화면으로 이동하는 메서드
         */
        fun nextView()

        /**
         * header 객체 재사용
         */
        fun setStatusBarInit()
    }

    interface SignUpPresenter:BasePresenter<ActivitySignupBinding>{
        val view:SignUpView
        var viewObj:SignUpView?

        /**
         * 유저가 엔터 클릭 시, 키패드가 숨기는 메서드
         */
        fun hideKeypad(buttonName: Button)

        /**
         * 최종적으로 회원가입에 필요한 정보를 서버로 전송하는 메서드
         * 회원가입 절차를 통해서 저장한 데이터들을 객체화한다. 다만 해당 API 결과 값에서 토큰 정보들을 추가하여 객체화한다.
         *
         * 이때 받은 토큰은 JWT이기 때문에 토큰 안에 만료시간이 저장되어져 있다. 때문에 JWT 토큰을 파싱하여
         * 만료시간을 구해서 내장디비에 저장해야한다.
         *
         * 추후 푸시알람을 위해 유저의 디바이스 정보를 같이 전송
         */
        fun signUp()

        /**
         * 해당 메서드에서는 유저로부터 필요한 정보를 더 얻어야하는지 확인한다.
         * 예를 들어, 소셜 로그인을 통해 생년월일을 얻어왔다면, 이는 유저로부터 추가적으로 기입받지 않아야한다.
         * 따라서 해당 페이지는 제거되고 필요한 부분만 기입받을 수 있도록 확인하여 초기화한다.
         */
        fun initViewpager()

        /**
         * 메모리 할당해제
         */
        fun detachView()
    }
}