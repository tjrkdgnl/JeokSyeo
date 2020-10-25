package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), MainContract.BaseView , View.OnClickListener {
    private lateinit var binding: MainBinding
    private lateinit var presenter:Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main)
        binding.basicHeader.windowHeaderListCategory.setOnClickListener(this)
        binding.mainNavigation.imageViewCancel.setOnClickListener(this)

        presenter = Presenter().apply {
            view = this@MainActivity
        }

        presenter.initCarouselViewPager(this)
        presenter.initRecommendViewPager(this)
        presenter.initNavigationItemSet(this)
        presenter.initAlcholRanking(this)
    }

    override fun getView():MainBinding {
        return binding
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageView_cancel -> {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.windowHeader_listCategory ->{
                if(!binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)){
                    binding.mainDrawerLayout.openDrawer(GravityCompat.END)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}