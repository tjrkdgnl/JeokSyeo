package com.jeoksyeo.wet.activity.setting

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingBinding

class SettingActivity: AppCompatActivity(), SettingContract.BaseView{
    private lateinit var presenter:Presenter
    private lateinit var binding:SettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.setting)
        presenter = Presenter().apply {
            view =this@SettingActivity
        }

        setStatusBarInit()

        presenter.initItem(this,this)
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = SettingActivity::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun getView(): SettingBinding {
        return binding
    }

    override fun setStatusBarInit() {
        binding.settingBasicHeader.basicHeaderWindowName.text = "설정"
        binding.settingBasicHeader.basicHeaderWindowName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))

        //status bar 배경변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.white,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            }
        }

        //status bar의 icon 색상 변경
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or decor.systemUiVisibility
        }else{
            decor.systemUiVisibility =0
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

}