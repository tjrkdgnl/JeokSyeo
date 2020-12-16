package com.jeoksyeo.wet.activity.agreement

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.CustomAgreementBinding

class Agreement : AppCompatActivity(), View.OnClickListener {


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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.basicHeader_backButton -> {
                finish()
                overridePendingTransition(R.anim.current_to_current, R.anim.current_to_right)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.current_to_current, R.anim.current_to_right)
    }
}

