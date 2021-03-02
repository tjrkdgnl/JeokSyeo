package com.activities.splash

import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.SplashBinding

interface SplashContract {

    interface SplashView:BaseView<SplashBinding>

    interface SplashPresenter :BasePresenter<SplashBinding> {
        val view: SplashView
        var viewObj: SplashView?

        /**
         * 서버에 저장되어져 있는 유저의 정보를 셋팅한다. 이때 비동기 통신이 끝날 때까지 기다려야하며,
         * 유저 정보 셋팅이 완료된 이후, 다음 로직이 실행될 수 있도록 한다.
         */
        suspend fun setUserInfo():Boolean

        /**
         * 액티비티 전환
         */
        fun moveActivity()

        /**
         * 메모리 할당해제
         */
        fun detach()

        /**
         * 앱 버전확인 메서드.
         * 현재 유저가 다운로드 받은 앱의 버전과 서버에서 관리하는 가장 최근 버전이 다른지 체크한다.
         * 이를 통해서 앱이 업데이트 되면 유저는 앱 진입 전에 업데이트 알림을 받게되어 스토어로 이동하게 된다.
         * 또한 앱 업데이트가 진행되지 않으면 메인화면으로 진입하지 않는다.
         */
        suspend fun versionCheck(): Boolean
    }

}