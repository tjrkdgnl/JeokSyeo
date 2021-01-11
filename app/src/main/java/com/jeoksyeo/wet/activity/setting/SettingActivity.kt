package com.jeoksyeo.wet.activity.setting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SettingBinding

class SettingActivity: AppCompatActivity(), SettingContract.BaseView, View.OnClickListener {
    private lateinit var presenter:Presenter
    private lateinit var binding:SettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.setting)
        presenter = Presenter().apply {
            view =this@SettingActivity
        }

        binding.settingBasicHeader.basicHeaderWindowName.text = "설정"

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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.basicHeader_backButton ->{
                finish()
                overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

}