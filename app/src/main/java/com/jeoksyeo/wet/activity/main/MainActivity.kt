package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.jeoksyeo.wet.activity.agreement.Agreement
import com.jeoksyeo.wet.activity.alcohol_category.AlcoholCategory
import com.jeoksyeo.wet.activity.search.Search
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), MainContract.BaseView, View.OnClickListener {
    private lateinit var binding: MainBinding
    private lateinit var presenter: Presenter
    private val postionBundle:Bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main)
        binding.basicHeader.windowHeaderListCategory.setOnClickListener(this)
        binding.mainNavigation.imageViewCancel.setOnClickListener(this)

        presenter = Presenter().apply {
            view = this@MainActivity
            activity = this@MainActivity
        }

        //링크를 통해 앱을 실행했을 때, 0.6초 뒤에 주류 상세정보로 이동
       with(Handler(Looper.getMainLooper())){
            postDelayed({
                presenter.handleDeepLink()
            },300)
        }

        //네트워크 상태확인
        presenter.setNetworkUtil()

        presenter.initBanner(this)
        presenter.autoSlide()

        binding.mainNavigation.navigationHeader.root.setOnClickListener(this)

        binding.mainBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bannerCount.text = "${position % presenter.bannerItem + 1} / ${presenter.bannerItem}"
            }
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = MainActivity::class.java
        presenter.initNavigationItemSet(this, this)
        presenter.checkLogin(this)

        if(binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.mainDrawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)

        //네트워크가 다시 연결 됐을 때,
        presenter.initRecommendViewPager(this)
        presenter.initAlcoholRanking(this)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun getView(): MainBinding {
        return binding
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton -> {
                GlobalApplication.instance.moveActivity(this, Search::class.java)
            }

            R.id.imageView_cancel -> {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.windowHeader_listCategory -> {
                if (!binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.mainDrawerLayout.openDrawer(GravityCompat.END)
                }
            }

            R.id.navigation_header ->{
                if(GlobalApplication.userInfo.getProvider() ==null){
                    CustomDialog.loginDialog(this,0,false)
                }
            }

            R.id.activityMain_koreanAlcohol -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 0)
                GlobalApplication.instance.moveActivity(
                    this, AlcoholCategory::class.java,
                    0, postionBundle, GlobalApplication.CATEGORY_BUNDLE
                )
            }

            R.id.activityMain_beer -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 1)
                GlobalApplication.instance.moveActivity(
                    this, AlcoholCategory::class.java,
                    0, postionBundle, GlobalApplication.CATEGORY_BUNDLE
                )
            }

            R.id.activityMain_wine -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 2)
                GlobalApplication.instance.moveActivity(
                    this, AlcoholCategory::class.java,
                    0, postionBundle, GlobalApplication.CATEGORY_BUNDLE
                )
            }

            R.id.activityMain_whisky -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 3)
                GlobalApplication.instance.moveActivity(
                    this, AlcoholCategory::class.java,
                    0, postionBundle, GlobalApplication.CATEGORY_BUNDLE
                )
            }

            R.id.activityMain_sake -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 4)
                GlobalApplication.instance.moveActivity(
                    this, AlcoholCategory::class.java,
                    0, postionBundle, GlobalApplication.CATEGORY_BUNDLE
                )
            }

            R.id.activityMain_termsOfService ->{
                val  bundle =Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                GlobalApplication.instance.moveActivity(this,
                    Agreement::class.java,0,bundle,GlobalApplication.AGREEMENT,1)
            }

            R.id.activityMain_PrivacyPolicyText->{
                val  bundle =Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,1)
                GlobalApplication.instance.moveActivity(this,Agreement::class.java,0,bundle,GlobalApplication.AGREEMENT,1)
            }

        }
    }

    override fun onBackPressed() {
        if(binding.mainDrawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.mainDrawerLayout.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}