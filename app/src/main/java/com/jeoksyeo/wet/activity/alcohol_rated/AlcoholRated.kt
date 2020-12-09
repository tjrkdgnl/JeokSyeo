package com.jeoksyeo.wet.activity.alcohol_rated

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayout
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedBinding

class AlcoholRated :AppCompatActivity(), AlcoholRatedContact.BaseView
    ,TabLayout.OnTabSelectedListener,View.OnClickListener{
    private lateinit var binding:AlcoholRatedBinding
    private lateinit var presenter:Presenter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_rated)
        binding.lifecycleOwner =this

        window.statusBarColor = resources.getColor(R.color.orange,null)

        presenter = Presenter().apply {
            view=this@AlcoholRated
            context =this@AlcoholRated
        }
        presenter.setNetworkUtil()

        presenter.initProfile(GlobalApplication.userInfo.getProvider())
        presenter.initTabLayout(this)
        binding.ratedTablayout.addOnTabSelectedListener(this)

        val viewmodel = ViewModelProvider(this).get(RatedViewModel::class.java)


        viewmodel.reviewCount.observe(this, Observer {

           binding.profileHeader.ratedCountText.text = "총  ${it}개의 주류를 평가하셨습니다."
        })
    }

    override fun getView(): AlcoholRatedBinding {
        return binding
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.orange,null))
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.let {
            (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor,null))
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.rated_back-> {
                finish()
                overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
            }
        }
    }
}