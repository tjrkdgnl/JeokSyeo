package com.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.application.GlobalApplication

abstract class BaseActivity<T:ViewDataBinding> :AppCompatActivity() {
    abstract val layoutResID:Int

    private var bindingObj: T? =null

    protected val binding by lazy {
        bindingObj!!
    }

    abstract fun setOnCreate()

    abstract fun destroyPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this,layoutResID)
        binding.lifecycleOwner =this

        setOnCreate()
    }

    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(false)

    }

    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalApplication.instance.activityClass=null
        destroyPresenter()
        bindingObj=null
    }
}