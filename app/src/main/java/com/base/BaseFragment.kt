package com.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    abstract val layoutResID: Int
    private  var bindingObj:T? = null
    protected val binding by lazy {
        bindingObj!!
    }

    abstract fun detachPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingObj = DataBindingUtil.inflate(layoutInflater, layoutResID, container, false)
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        detachPresenter()
        bindingObj =null
    }
}