package com.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MypageBinding

class MyPageFragment: Fragment(), MypageContract.BaseView {
    private lateinit var binding:MypageBinding
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
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.mypage,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MyPagePresenter().apply {
            baseView = this@MyPageFragment
            activity = requireActivity()
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
}