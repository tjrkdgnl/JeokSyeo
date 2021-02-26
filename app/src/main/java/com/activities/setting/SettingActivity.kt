package com.activities.setting

import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.application.GlobalApplication
import com.base.BaseActivity
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingBinding

class SettingActivity: BaseActivity<SettingBinding>(), SettingContract.SettingView{
    private lateinit var presenter:Presenter
    override val layoutResID: Int = R.layout.setting

    override fun setOnCreate() {
        presenter = Presenter().apply {
            view =this@SettingActivity
            activity =this@SettingActivity
        }

        setStatusBarInit()

        presenter.initItem(this,this)
    }

    override fun destroyPresenter() {
        presenter.detachView()
    }


    override fun getBindingObj(): SettingBinding {
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