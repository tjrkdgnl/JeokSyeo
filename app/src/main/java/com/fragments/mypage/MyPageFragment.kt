package com.fragments.mypage

import android.os.Bundle
import android.view.View
import com.activities.editprofile.EditProfile
import com.application.GlobalApplication
import com.base.BaseFragment
import com.custom.CustomDialog
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MypageBinding

class MyPageFragment: BaseFragment<MypageBinding>(), MypageContract.MypageView {
    override val layoutResID: Int =R.layout.mypage
    private lateinit var presenter:MyPagePresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MyPagePresenter().apply {
            this.viewObj = this@MyPageFragment
            activity = requireActivity()
        }

        //프로필 헤더를 클릭하면 바로 '내 정보수정 화면'으로 이동
        binding.myPageHeader.root.setOnClickListener {
            GlobalApplication.userInfo.getProvider()?.let {
                GlobalApplication.instance.moveActivity(requireContext(),EditProfile::class.java)
            } ?: CustomDialog.loginDialog(requireContext(),0,false)
        }

        presenter.initMenuList()
        presenter.initTextSize()
    }

    //onResume에서 유저의 정보를 셋팅하는 이유는 '내 정보 수정'화면에서 변경된 데이터를 현재화면으로
    //돌아왔을 때 그대로 반영하기 위해서 resume 콜백에서 실행
    override fun onResume() {
        super.onResume()
        presenter.checkLogin()
    }

    override fun detachPresenter() {
        presenter.detach()
    }

    override fun getBindingObj(): MypageBinding {
        return binding
    }
}