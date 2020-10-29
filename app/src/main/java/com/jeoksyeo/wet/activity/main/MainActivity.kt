package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.alchol_category.AlcholCategory
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), MainContract.BaseView, View.OnClickListener {
    private lateinit var binding: MainBinding
    private lateinit var presenter: Presenter
    private var checkRefresh = false
    private lateinit var moveIntent:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main)
        binding.basicHeader.windowHeaderListCategory.setOnClickListener(this)
        binding.mainNavigation.imageViewCancel.setOnClickListener(this)

        moveIntent = Intent(this,AlcholCategory::class.java)

        presenter = Presenter().apply {
            view = this@MainActivity
        }
        binding.activityMainKoreanAlchol.setOnClickListener(this)
        binding.activityMainBeer.setOnClickListener(this)
        binding.activityMainWine.setOnClickListener(this)
        binding.activityMainWhisky.setOnClickListener(this)
        binding.activityMainSake.setOnClickListener(this)

        presenter.initCarouselViewPager(this)
        presenter.initRecommendViewPager(this)
        presenter.initNavigationItemSet(this, this, GlobalApplication.userInfo.getProvider())
        presenter.initAlcholRanking(this)

        presenter.checkLogin(this, GlobalApplication.userInfo.getProvider())

    }

    override fun getView(): MainBinding {
        return binding
    }

    override fun refresh() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_cancel -> {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.windowHeader_listCategory -> {
                if (!binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.mainDrawerLayout.openDrawer(GravityCompat.END)
                }
            }
            R.id.activityMain_koreanAlchol ->{
                moveIntent.putExtra(GlobalApplication.MOVE_TYPE,0)
                startActivity(moveIntent)}

            R.id.activityMain_beer ->{
                moveIntent.putExtra(GlobalApplication.MOVE_TYPE,1)
                    startActivity(moveIntent)}

            R.id.activityMain_wine ->{
                moveIntent.putExtra(GlobalApplication.MOVE_TYPE,2)
                    startActivity(moveIntent)}

            R.id.activityMain_whisky ->{
                moveIntent.putExtra(GlobalApplication.MOVE_TYPE,3)
                    startActivity(moveIntent)}

            R.id.activityMain_sake->{
                moveIntent.putExtra(GlobalApplication.MOVE_TYPE,4)
                    startActivity(moveIntent)}
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkRefresh) {
            checkRefresh = false
            refresh()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!checkRefresh) {
            checkRefresh = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}