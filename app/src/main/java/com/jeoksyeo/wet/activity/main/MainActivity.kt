package com.jeoksyeo.wet.activity.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.alchol_category.AlcholCategory
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
        }
        binding.activityMainKoreanAlchol.setOnClickListener(this)
        binding.activityMainBeer.setOnClickListener(this)
        binding.activityMainWine.setOnClickListener(this)
        binding.activityMainWhisky.setOnClickListener(this)
        binding.activityMainSake.setOnClickListener(this)

        presenter.initBanner(this)
        presenter.initRecommendViewPager(this)
        presenter.initAlcholRanking(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.initNavigationItemSet(this, this)
        presenter.checkLogin(this)
    }

    override fun getView(): MainBinding {
        return binding
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton ->{
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
            R.id.activityMain_koreanAlchol -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE,0)
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,
                0,postionBundle,GlobalApplication.CATEGORY_BUNDLE)}

            R.id.activityMain_beer -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE,1)
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,
                    0,postionBundle,GlobalApplication.CATEGORY_BUNDLE) }

            R.id.activityMain_wine -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE,2)
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,
                    0,postionBundle,GlobalApplication.CATEGORY_BUNDLE) }

            R.id.activityMain_whisky -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE,3)
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,
                    0,postionBundle,GlobalApplication.CATEGORY_BUNDLE) }

            R.id.activityMain_sake -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE,4)
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,
                    0,postionBundle,GlobalApplication.CATEGORY_BUNDLE) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}