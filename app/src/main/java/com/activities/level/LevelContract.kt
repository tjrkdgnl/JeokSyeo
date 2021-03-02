package com.activities.level

import com.base.BasePresenter
import com.base.BaseView
import com.vuforia.engine.wet.databinding.LevelBinding

interface LevelContract {

    interface LevelView: BaseView<LevelBinding> {
        /**
         * 로티 실행 메서드
         * {@param level}에 따라 정해진 로티를 실행
         */
        fun settingMainAlcholGIF(level:Int)

        /**
         * 현재 유저의 주류 경험치를 표시하는 메서드.
         * {@param reviewCount}를 통해서 현재까지 진행된 경험치를 계산
         * {@param level}을 통해서 현재 레벨과 다음 레벨의 닉네임 셋팅
         */
        fun settingExperience(reviewCount:Int,level: Int)

        /**
         * 최종 단계 레벨에 도달했을 시 실행되는 메서드
         */
        fun finalLevel()


        fun setHeaderInit()

    }

    interface LevelPresenter:BasePresenter<LevelBinding>{
        /**
         * 액티비티로부터 구현된 LevelView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view:LevelView
        var viewObj:LevelView?

        /**
         * 서버로부터 유저의 레벨을 얻어오는 메서드
         */
        fun getMyLevel()

        /**
         * 경험치를 의미하는 미니 병의 이미지뷰 객체를 초기화하는 메서드
         */
        fun initMiniImageArray()

        /**
         * 메모리 할당해제
         */
        fun detach()
    }
}