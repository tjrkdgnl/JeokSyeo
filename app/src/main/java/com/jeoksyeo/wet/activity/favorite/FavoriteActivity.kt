 package com.jeoksyeo.wet.activity.favorite

import android.annotation.SuppressLint
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

 class FavoriteActivity: BaseActivity<FavoriteActivityBinding>(), FavoriteContract.BaseView {
    private lateinit var presenter:Presenter
    private lateinit var viewmodel:FavoriteViewModel
    val tabList = listOf("전체","전통주","맥주","와인","양주","사케")

     override val layoutResID: Int =R.layout.favorite_activity

     @SuppressLint("SetTextI18n")
     override fun setOnCreate() {
         viewmodel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

         presenter =Presenter().apply {
             view= this@FavoriteActivity
             context = this@FavoriteActivity
         }

         presenter.setNetworkUtil()

         setHeaderInit()

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

         viewmodel.currentPosition.observe(this,  {
             if(it !=-1){
                 binding.typeCountText.text = "총 ${viewmodel.alcoholTypeList[it]}개의 주류를 찜하셨습니다."
                 binding.favoriteAlcoholTypeText.text = tabList[it]
             }
         })

         viewmodel.summaryCount.observe(this,  {
             binding.profileHeader.ratedCountText.text = "총 ${viewmodel.summaryCount.value}개의 주류를 찜하셨습니다."
         })
     }

     override fun destroyPresenter() {
         presenter.detach()
     }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = FavoriteActivity::class.java
    }

    override fun getBinding(): FavoriteActivityBinding {
        return binding
    }

    override fun setHeaderInit() {
        binding.basicHeader.title.text = "내가 찜한 주류"
        binding.basicHeader.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.orange,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }
}