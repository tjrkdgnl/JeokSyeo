package com.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.application.GlobalApplication
import com.fragment.alcohol_category.AlcoholCategoryFragment
import com.fragment.search.SearchFragment
import com.jeoksyeo.wet.activity.main.MainActivity
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

class MainFragment: Fragment(), MainContract.BaseView,View.OnClickListener {
    lateinit var binding:MainBinding
    lateinit var mainPresenter: MainPresenter
    private val postionBundle:Bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main,container,false)

        binding.activityMainKoreanAlcohol.setOnClickListener(this)
        binding.activityMainBeer.setOnClickListener(this)
        binding.activityMainWine.setOnClickListener(this)
        binding.activityMainWhisky.setOnClickListener(this)
        binding.activityMainSake.setOnClickListener(this)
        binding.basicHeader.windowHeaderSearchButton.setOnClickListener(this)


        mainPresenter = MainPresenter().apply {
            view = this@MainFragment
            activity = requireActivity()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainPresenter.initBanner(view.context)
        mainPresenter.autoSlide()

        mainPresenter.initRecommendViewPager(view.context)
        mainPresenter.initAlcoholRanking(view.context)


        binding.mainBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bannerCount.text = "${position % mainPresenter.bannerItem + 1} / ${mainPresenter.bannerItem}"
            }
        })

    }

    override fun getViewBinding(): MainBinding {
        return binding
    }

    fun initApi(context: Context){
        mainPresenter.initRecommendViewPager(context)
        mainPresenter.initAlcoholRanking(context)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton -> {
                (activity as? MainActivity)?.replaceFragment(SearchFragment.newInstance("main"),"search")
            }

            R.id.activityMain_koreanAlcohol -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 0)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment,"category")
            }

            R.id.activityMain_beer -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 1)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment,"category")
            }

            R.id.activityMain_wine -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 2)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment,"category")
            }

            R.id.activityMain_whisky -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 3)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment,"category")
            }

            R.id.activityMain_sake -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 4)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment,"category")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.detachView()
    }
}