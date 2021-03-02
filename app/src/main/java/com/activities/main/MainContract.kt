package com.activities.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.MainBinding
import com.vuforia.engine.wet.databinding.RealMainActivityBinding

interface MainContract {

    interface MainView:BaseView<RealMainActivityBinding>{

        /**
         * 프래그먼트 전환 메서드
         * {@param name}을 통해서 프래그먼트가 생성될 때, name이란 백스택을 같이 생성한다.
         * 이를 통해 프래그먼트가 commit 될 시, 스택에 저장이 된다.
         */
        fun replaceFragment(fragment: Fragment,name:String)

        /**
         * 'journey box' 탭에서 로그인 관련 공지 토스트가 애니메이션으로 작동한다.
         * 하지만 탭이 전환되면 보이지 않아야 하므로 해당 애니메이션을 취소시켜야한다.
         * 이 작업을 거치지 않으면 애니메이션 객체가 해당 값을 갖고 있게 되면서 ui적으로
         * 변화가 먹히지 않기 때문이다.
         */
        fun cancelTheJourneyLoginToast()

        /**
         * 저니박스 로그인 관련 공지 토스트를 실행하는 메서드
         */
        fun showTheJourneyLoginToast()

        /**
         * 키패드가 올라올 시, 바텀네비게이션도 같이 올라온다. 때문에 이를 사라지게 만드는 메서드이다.
         * 반면에 키패드가 사라졌을 시, 다시 바텀네비게이션을 보이도록한다. 이 작업을 {@param check}를
         * 통해서 작동한다.
         */
        fun bottomNavigationVisiblity(check:Int)

    }

    interface MainPresenter: BasePresenter<RealMainActivityBinding>{
        /**
         * 액티비티로부터 구현된 MainView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view:MainView
        var viewObj:MainView?

        /**
         * 메모리 할당 해제
         */
        fun detachView()

        /**
         * 주류 정보를 서버로부터 받아오는 메서드이다.
         * {@param alcoholId}는 api parameter로서, 주류 정보를 얻어올 수 있는 키이다. 해당 값을 통해서
         * 유저가 선택한 주류의 정보를 얻어온다.
         */
        fun getAlcohol(alcoholId: String)

        /**
         * QR code를 통해 앱이 실행됐을 때를 핸들링한 메서드이다.
         * QR code의 url부분 중, query부분을 통해서 alcoholId를 얻으며 이를 통해서 getAlcohol 메서드를
         * 실행시켜 정보를 얻어온다. 그리고 해당 정보를 intent를 통해서 화면을 전환시킨다.
         */
        fun handleDeepLink()

    }
}