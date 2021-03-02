package com.activities.alcohol_rated

import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedBinding

class AlcoholRated :BaseActivity<AlcoholRatedBinding>(), AlcoholRatedContact.RatedView
    ,TabLayout.OnTabSelectedListener{

    override val layoutResID: Int = R.layout.alcohol_rated

    private lateinit var presenter:Presenter

    override fun setOnCreate() {
        presenter = Presenter().apply {
            viewObj=this@AlcoholRated
            activity =this@AlcoholRated
        }

        setHeaderinit()

        presenter.initProfile(GlobalApplication.userInfo.getProvider())
        presenter.initTabLayout(this)
        binding.ratedTablayout.addOnTabSelectedListener(this)

        binding.ratedHeader.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))


        val viewmodel = ViewModelProvider(this).get(RatedViewModel::class.java)

        viewmodel.reviewCount.observe(this, Observer {
            binding.profileHeader.ratedCountText.text = "총  ${it}개의 주류를 평가하셨습니다."
        })
    }

    private fun setHeaderinit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.orange,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
            }
        }
    }

    override fun destroyPresenter() {
        presenter.detach()
    }

    override fun getBindingObj(): AlcoholRatedBinding {
        return binding
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.orange,null))
            } else {
                (tab.customView as? TextView)?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor,null))
            } else {
                (tab.customView as? TextView)?.setTextColor(ContextCompat.getColor(this,R.color.tabColor))
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }
}