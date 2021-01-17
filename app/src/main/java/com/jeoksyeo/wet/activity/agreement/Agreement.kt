package com.jeoksyeo.wet.activity.agreement

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.CustomAgreementBinding

class Agreement : AppCompatActivity(){


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<CustomAgreementBinding>(this, R.layout.custom_agreement)

        //html파일도 허용
        val setting = binding.webView.settings
        setting.javaScriptEnabled =true

        if (intent.hasExtra(GlobalApplication.AGREEMENT)) {
            val bundle = intent.getBundleExtra(GlobalApplication.AGREEMENT)
            val code = bundle?.getInt(GlobalApplication.AGREEMENT_INFO)

            if (code == 0) {
                binding.activityHeader.basicHeaderWindowName.text = "이용약관"
                binding.webView.loadUrl(resources.getString(R.string.use_agreement))
            } else if (code == 1) {
                binding.activityHeader.basicHeaderWindowName.text = "개인정보 취급 방침"
                binding.webView.loadUrl(resources.getString(R.string.private_agreement))
            }

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
    }

    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }

    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.current_to_current, R.anim.current_to_right)
    }
}

