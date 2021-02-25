package com.base

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.application.GlobalApplication
import com.service.NetworkUtil

abstract class BaseActivity<T:ViewDataBinding> :AppCompatActivity() {
    private lateinit var networkUtil: NetworkUtil

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


        setNetworkUtil()

        setOnCreate()
    }

    fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(this)
            networkUtil.register()
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }

        GlobalApplication.instance.setCurrentActivity(null)
        destroyPresenter()
        bindingObj=null
    }
}