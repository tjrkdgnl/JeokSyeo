package com.jeoksyeo.wet.activity.favorite

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayout
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

class FavoriteActivity: AppCompatActivity(), FavoriteContract.BaseView , View.OnClickListener {
    private lateinit var binding:FavoriteActivityBinding
    private lateinit var presenter:Presenter
    private lateinit var viewmodel:FavoriteViewModel
    val tabList = listOf("전체","전통주","맥주","와인","양주","사케")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.favorite_activity)
        binding.lifecycleOwner =this

        viewmodel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        presenter =Presenter().apply {
            view= this@FavoriteActivity
            context = this@FavoriteActivity
        }

        presenter.setNetworkUtil()

        binding.basicHeader.title.text = "내가 찜한 주류"

        presenter.initTabLayout()
        presenter.initProfile()

        binding.favoriteTablayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (tab?.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor,null))
                }else{
                    (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(this@FavoriteActivity,R.color.tabColor))
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewmodel.currentPosition.value = tab?.position
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (tab?.customView as? TextView)?.setTextColor(resources.getColor(R.color.orange,null))
                }else{
                    (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(this@FavoriteActivity,R.color.orange))
                }
            }
        })

        viewmodel.currentPosition.observe(this, Observer {
            if(it !=-1){
                binding.typeCountText.text = "총 ${viewmodel.alcoholTypeList[it]}개의 주류를 찜하셨습니다."
                binding.favoriteAlcoholTypeText.text = tabList[it]
            }
        })

        viewmodel.summaryCount.observe(this, Observer {
            binding.profileHeader.ratedCountText.text = "총 ${viewmodel.summaryCount.value}개의 주류를 찜하셨습니다."
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = FavoriteActivity::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun getBinding(): FavoriteActivityBinding {
        return binding
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.rated_back ->{
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