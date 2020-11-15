package com.jeoksyeo.wet.activity.favorite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

class FavoriteActivity: AppCompatActivity(), FavoriteContract.BaseView , View.OnClickListener {

    private lateinit var binding:FavoriteActivityBinding
    private lateinit var presenter:Presenter
    private lateinit var viewmodel:FavoriteViewModel
    private val tabList = listOf("전체","전통주","사케","맥주","와인","양")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.favorite_activity)
        binding.lifecycleOwner =this

        viewmodel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        presenter =Presenter().apply {
            view= this@FavoriteActivity
            context = this@FavoriteActivity
        }

        presenter.initTabLayout()
        presenter.initProfile()

        binding.favoriteTablayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewmodel.currentPosition.value = tab?.position
            }
        })


        viewmodel.currentPosition.observe(this, Observer {
            binding.profileHeader.ratedCountText.text = "총 ${viewmodel.alcoholTypeList[it]}개의 주류를 찜하셨습니다."
            binding.typeCountText.text = "총 ${viewmodel.alcoholTypeList[it]}개의 주류를 찜하셨습니다."
            binding.favoriteAlcoholTypeText.text = tabList[it]
        })

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