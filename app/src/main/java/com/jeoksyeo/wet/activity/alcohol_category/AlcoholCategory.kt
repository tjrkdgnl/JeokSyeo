package com.jeoksyeo.wet.activity.alcohol_category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.adapter.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.fragment.alcohol_category.Fragment_Grid
import com.fragment.alcohol_category.Fragment_List
import com.google.android.material.tabs.TabLayout
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.search.Search
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding
import org.w3c.dom.Text

class AlcoholCategory : FragmentActivity(), AlcoholCategoryContact.BaseView, View.OnClickListener,
    PopupMenu.OnMenuItemClickListener,TabLayout.OnTabSelectedListener {
    private lateinit var binding: AlcoholCategoryBinding
    private lateinit var presenter: Presenter
    private var popupMenu: PopupMenu? = null
    private lateinit var viewModel: AlcoholCategoryViewModel
    private var currentItem =0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_category)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(AlcoholCategoryViewModel::class.java)

        presenter = Presenter().apply {
            view = this@AlcoholCategory
            context = this@AlcoholCategory
        }

        if(intent.hasExtra(GlobalApplication.CATEGORY_BUNDLE)){
            val bundle = intent.getBundleExtra(GlobalApplication.CATEGORY_BUNDLE)
            val selectPosition = bundle?.getInt(GlobalApplication.MOVE_TYPE)
            selectPosition?.let {
                currentItem = it
            }
        }
        //뷰페이저 초기화
        presenter.inintTabLayout(this,currentItem)

        //네트워크 감지 셋팅
        presenter.setNetworkUtil()

        binding.categoryNavigation.navigationHeader.root.setOnClickListener(this)

        binding.tabLayoutAlcoholList.addOnTabSelectedListener(this)
        binding.viewPager2Container.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItem = position
                binding.viewPager2Container.currentItem = position
                viewModel.changePosition.value = position
                presenter.checkSort(position, viewModel.currentSort)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(resources.getColor(R.color.orange,null))
                }
                else{
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(ContextCompat.getColor(this@AlcoholCategory,R.color.orange))
                }
            }
        })

        binding.textViewTotalProduct.setTextSize(TypedValue.COMPLEX_UNIT_DIP,(11f/360f)*(GlobalApplication.instance.device_width))
        viewModel.changePosition.observe(this, Observer {
            binding.textViewTotalProduct.text = "총 "+ viewModel.totalCountList[binding.viewPager2Container.currentItem].toString()+
                    "개의 상품이 있습니다."
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = AlcoholCategory::class.java

        presenter.initNavigationItemSet(this,this)
        presenter.checkLogin(this)

        if(binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.categoryDrawerLayout.closeDrawer(GravityCompat.END)
        }
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }

    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun getView(): AlcoholCategoryBinding {
        return binding
    }

    override fun changeToggle(toggle: Boolean) {
        val offset = binding.viewPager2Container.currentItem
        if (toggle) {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_off)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_on)
            binding.viewPager2Container.adapter = GridViewPagerAdapter(this)
        } else {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_on)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_off)
            binding.viewPager2Container.adapter = ListViewPagerAdapter(this)
        }
        binding.viewPager2Container.currentItem = offset
    }

    @SuppressLint("SetTextI18n")
    override fun setTotalCount(alcoholCount: Int) {
        binding.textViewTotalProduct.text = "총 " + alcoholCount + "개의 주류가 있습니다."
    }

    //현재 보여지고 있는 fragment를 정한 sort방식으로 갱신
    fun executeSorting(sort: String) {
        val fragment = presenter.getFragement(binding.viewPager2Container.currentItem)
        viewModel.currentSort = sort

        if (fragment is Fragment_Grid)
            fragment.changeSort(sort)
        else if (fragment is Fragment_List)
            fragment.changeSort(sort)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton ->{
                GlobalApplication.instance.moveActivity(this, Search::class.java) }

            R.id.windowHeader_logo ->{
                finish()
                val intent = Intent(this,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            R.id.imageView_cancel -> {
                if(binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.closeDrawer(GravityCompat.END) }

            R.id.windowHeader_listCategory -> {
                if (!binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.openDrawer(GravityCompat.END) }

            R.id.navigation_header->{
                if(GlobalApplication.userInfo.getProvider() ==null){
                    CustomDialog.loginDialog(this,2,false)
                }
            }

            R.id.imageView_listToggle -> { changeToggle(false) }

            R.id.imageView_viewToggle -> { changeToggle(true) }

            R.id.sorting_constraintLayout -> {
                if (popupMenu == null) {
                    popupMenu = PopupMenu(applicationContext, binding.sortingConstraintLayout)
                    menuInflater.inflate(R.menu.order_menu, popupMenu!!.menu)
                    popupMenu!!.setOnMenuItemClickListener(this)
                }
                popupMenu!!.show()
            }
        }
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.reviewOrder -> {
                executeSorting("review")
                binding.textViewCurrentOrdering.text = "리뷰순" }
            R.id.likeOrder -> {
                executeSorting("like")
                binding.textViewCurrentOrdering.text = "좋아요순" }
            R.id.degreeOrder_asc -> {
                executeSorting("abv-asc")
                binding.textViewCurrentOrdering.text = "도수 낮은순" }
            R.id.degreeOrder_desc -> {
                executeSorting("abv-desc")
                binding.textViewCurrentOrdering.text = "도수 높은순" }
        }
        return true
    }

    override fun onBackPressed() {
        if(binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END)){
            binding.categoryDrawerLayout.closeDrawer(GravityCompat.END)
        }
        else{
            super.onBackPressed()
            overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (tab?.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor,null))
        }
        else{
            (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(this,R.color.tabColor))
        }

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }
}