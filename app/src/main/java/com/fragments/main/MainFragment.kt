package com.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.activities.agreement.Agreement
import com.activities.main.MainActivity
import com.application.GlobalApplication
import com.base.BaseFragment
import com.fragments.alcohol_category.AlcoholCategoryFragment
import com.fragments.search.SearchFragment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

class MainFragment : BaseFragment<MainBinding>(), MainContract.MainView, View.OnClickListener {
    override val layoutResID = R.layout.main

    lateinit var mainPresenter: MainPresenter
    private val postionBundle: Bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.activityMainKoreanAlcohol.setOnClickListener(this)
        binding.activityMainBeer.setOnClickListener(this)
        binding.activityMainWine.setOnClickListener(this)
        binding.activityMainWhisky.setOnClickListener(this)
        binding.activityMainSake.setOnClickListener(this)
        binding.basicHeader.windowHeaderSearchButton.setOnClickListener(this)
        binding.businessInfo.activityMainTermsOfService.setOnClickListener(this)
        binding.businessInfo.activityMainPrivacyPolicyText.setOnClickListener(this)

        mainPresenter = MainPresenter().apply {
            this.viewObj = this@MainFragment
            activity = requireActivity()
        }

        mainPresenter.initBanner()
        mainPresenter.autoSlide()

        mainPresenter.initRecommendViewPager()
        mainPresenter.initAlcoholRanking()


        //배너 자동으로 넘겨지면 현재 쪽수 갱신
        binding.mainBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainPresenter.bannerNumber(position)
            }
        })
    }

    override fun getBindingObj(): MainBinding {
        return binding
    }

    fun initApi() {
        mainPresenter.initRecommendViewPager()
        mainPresenter.initAlcoholRanking()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            //검색 버튼 이미지 클릭
            R.id.windowHeader_SearchButton -> {
                (activity as? MainActivity)?.addToFragment(
                    SearchFragment.newInstance(),
                    "search"
                )
            }
            //이용약관 텍스트 클릭
            R.id.activityMain_termsOfService->{
                val intent = Intent(v.context,Agreement::class.java)
                val bundle = Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                intent.putExtra(GlobalApplication.AGREEMENT,bundle)

                startActivity(intent)
            }
            //개인정보처리방침 텍스트 클릭
            R.id.activityMain_PrivacyPolicyText->{
                val intent = Intent(v.context,Agreement::class.java)
                val bundle = Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,1)
                intent.putExtra(GlobalApplication.AGREEMENT,bundle)

                startActivity(intent)
            }
            //전통주 이미지 클릭
            R.id.activityMain_koreanAlcohol -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 0)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.addToFragment(fragment, "category")
            }
            //맥주 이미지 클릭
            R.id.activityMain_beer -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 1)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.addToFragment(fragment, "category")
            }
            //와인 이미지 클릭
            R.id.activityMain_wine -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 2)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.addToFragment(fragment, "category")
            }
            //위스키 이미지 클릭
            R.id.activityMain_whisky -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 3)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.addToFragment(fragment, "category")
            }
            //사케 이미지 클릭
            R.id.activityMain_sake -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 4)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.addToFragment(fragment, "category")
            }
        }
    }

    override fun detachPresenter() {
        mainPresenter.detachView()
    }
}