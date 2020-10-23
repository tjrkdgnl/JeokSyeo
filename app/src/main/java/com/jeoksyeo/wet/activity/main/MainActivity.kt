package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.adapter.main.BannerAdapter
import com.adapter.main.RecommendAlcholAdapter
import com.application.GlobalApplication
import com.custom.ViewPagerTransformer
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private lateinit var disposable: Disposable
    private lateinit var binding: MainBinding
    private val handler = Handler()
    private var slideRunnable = Runnable {
        binding.mainBanner.currentItem = binding.mainBanner.currentItem + 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main)

        initCarouselViewPager()
        initRecommendViewPager()
    }

    private fun initCarouselViewPager() {
        val lst = mutableListOf<Int>()
        lst.add(R.mipmap.nuggi)
        lst.add(R.mipmap.alchol_img)

        binding.mainBanner.adapter = BannerAdapter(this, lst, binding.mainBanner)
        binding.mainBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(slideRunnable)
                handler.postDelayed(slideRunnable, 4000)
                binding.bannerCount.text = "$position / ${lst.size}"
            }
        })
    }

    private fun initRecommendViewPager() {

        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .getRecommendAlchol(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.activityMainRecommendViewPager2.adapter = RecommendAlcholAdapter(this,it.data?.alcholList?.toMutableList())

            }, { t -> t.stackTrace })

        binding.activityMainRecommendViewPager2.setPageTransformer(ViewPagerTransformer(this))
        binding.activityMainRecommendViewPager2.offscreenPageLimit =3
        binding.activityMainRecommendViewPager2.clipToPadding =false
        binding.activityMainRecommendViewPager2.clipChildren =false
//        binding.activityMainRecommendViewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

//        val compositePageTransformer = CompositePageTransformer()
//        compositePageTransformer.addTransformer(MarginPageTransformer(90))
//        compositePageTransformer.addTransformer { page, position ->
//            val r = 1 - Math.abs(position)
//            page.scaleY = 0.85f + r * 0.15f
//        }


    }

    override fun onDestroy() {
        super.onDestroy()

    }
}