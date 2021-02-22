package com.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.application.GlobalApplication
import com.base.BaseFragment
import com.fragment.alcohol_category.AlcoholCategoryFragment
import com.fragment.search.SearchFragment
import com.jeoksyeo.wet.activity.agreement.Agreement
import com.jeoksyeo.wet.activity.main.MainActivity
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding

class MainFragment : BaseFragment<MainBinding>(), MainContract.BaseView, View.OnClickListener {

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
            this.view = this@MainFragment
            activity = requireActivity()
        }

        mainPresenter.initBanner(view.context)
        mainPresenter.autoSlide()

        mainPresenter.initRecommendViewPager(view.context)
        mainPresenter.initAlcoholRanking(view.context)


        binding.mainBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(mainPresenter.bannerItem !=0){
                    binding.bannerCount.text =
                        "${position % mainPresenter.bannerItem + 1} / ${mainPresenter.bannerItem}"
                }
            }
        })
    }

    override fun getViewBinding(): MainBinding {
        return binding
    }

    fun initApi(context: Context) {
        mainPresenter.initRecommendViewPager(context)
        mainPresenter.initAlcoholRanking(context)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton -> {
                (activity as? MainActivity)?.replaceFragment(
                    SearchFragment.newInstance(),
                    "search"
                )
            }

            R.id.activityMain_termsOfService->{
                val intent = Intent(v.context,Agreement::class.java)
                val bundle = Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                intent.putExtra(GlobalApplication.AGREEMENT,bundle)

                startActivity(intent)
            }

            R.id.activityMain_PrivacyPolicyText->{
                val intent = Intent(v.context,Agreement::class.java)
                val bundle = Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,1)
                intent.putExtra(GlobalApplication.AGREEMENT,bundle)

                startActivity(intent)
            }

            R.id.activityMain_koreanAlcohol -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 0)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment, "category")
            }

            R.id.activityMain_beer -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 1)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment, "category")
            }

            R.id.activityMain_wine -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 2)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment, "category")
            }

            R.id.activityMain_whisky -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 3)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment, "category")
            }

            R.id.activityMain_sake -> {
                postionBundle.putInt(GlobalApplication.MOVE_TYPE, 4)
                val fragment = AlcoholCategoryFragment()
                fragment.arguments = postionBundle

                (activity as? MainActivity)?.replaceFragment(fragment, "category")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.detachView()
    }
}