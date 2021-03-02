package com.activities.editprofile

import android.net.Uri
import com.base.BasePresenter
import com.base.BaseView
import com.model.edit_profile.ProfileData
import com.vuforia.engine.wet.databinding.EditProfileBinding
import java.io.File

interface EditProfileContract {

    interface EditProfileView:BaseView<EditProfileBinding>{
        /**
         * 성별을 남자로 셋팅
         */
        fun setGender_Man()

        /**
         * 성별을 여자로 셋팅
         */
        fun setGender_Woman()

        /**
         * 생년월일을 그대로 셋팅
         */
        fun setBirthDay()

        /**
         * 닉네임 중복 체크 api의 결과값을 통해서 변경 가능한 닉네임인지 체크
         * {@param confirm}에 따라서 닉네임이 중복인지 아닌지 분기처리
         */
        fun confirmNickname(confirm: Boolean)

        /**
         * 회원정보 수정 확인 버튼 활성화 여부 판단
         */
        fun checkOkButton()

        /**
         * 배경에 맞는 상태바 색상변경
         */
        fun setStatusBarInit()
    }

    interface EditProfilePresenter:BasePresenter<EditProfileBinding>{
        /**
         * 액티비티로부터 구현된 EditProfileView 인터페이스를 presenter가 얻기 위함
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드 사용 가능
         */
        val view: EditProfileView
        var viewObj:EditProfileView?

        /**
         * 닉네임 중복 여부 체크
         */
        var enableToNickName :Boolean

        /**
         * 프로필사진 존재 여부 체크
         */
        var imageData: ProfileData?

        /**
         * 변경한 사항을 저장하기 위해서 api를 호출하는 메서드
         * {@param name} 수정한 유저의 닉네임
         * {@param gender} 수정한 유저의 성별
         * {@param birthday} 수정한 유저의 생년월일
         */
        fun executeEditProfile(name: String, gender: String, birthday: String)

        /**
         * URI를 생성하기 위해서 기본적으로 만들어놓은 path를 갖는 파일 생성
         */
        fun createImageFile(): File?

        /**
         * 유저가 변경하려는 닉네임이 규칙에 부합하는지 또는 중복이 아닌지 체크
         */
        fun checkNickName()

        /**
         * 액티비티 진입 시 처음 시작하는 init 메서드
         */
        fun initUserInfo(provider:String?)

        /**
         * 유저가 앨범에서 사진을 선택할 시, 선택한 사진을 API를 통해 서버로 전송하기 위한 메서드
         * 임시로 만든 file의 path로 가지고서 URI가 만들어지며
         */
        fun imageUpload(imageUri: Uri?)

        /**
         * 변경된 유저의 정보 업데이트
         */
        fun updateUserInfo()

        /**
         * 메모리 할당해제
         */
        fun detach()

    }
}