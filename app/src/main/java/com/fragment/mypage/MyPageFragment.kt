package com.fragment.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.editprofile.EditProfile
import com.jeoksyeo.wet.activity.main.MainActivity
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MypageBinding

class MyPageFragment: Fragment(), MypageContract.BaseView {
    private lateinit var binding:MypageBinding
    private var bindObj:MypageBinding? =null

    private lateinit var presenter:MyPagePresenter
    private lateinit var activityContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindObj = DataBindingUtil.inflate(layoutInflater, R.layout.mypage,container,false)
        binding = bindObj!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MyPagePresenter().apply {
            baseView = this@MyPageFragment
            activity = requireActivity()
        }

        binding.myPageHeader.root.setOnClickListener {
            GlobalApplication.instance.moveActivity(requireContext(),EditProfile::class.java)
        }

        presenter.initRecyclerView()
        presenter.initTextSize()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkLogin()
    }

    override fun getViewBinding(): MypageBinding {
        return binding
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj =null
    }
}