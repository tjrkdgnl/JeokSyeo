package com.jeoksyeo.wet.activity.alcohol_category

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.adapter.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.fragment.alcohol_category.Fragment_Grid
import com.fragment.alcohol_category.Fragment_List
import com.google.android.material.tabs.TabLayout
import com.jeoksyeo.wet.activity.search.Search
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding

class AlcoholCategory : FragmentActivity(), AlcoholCategoryContact.BaseView, View.OnClickListener,
    PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: AlcoholCategoryBinding
    private lateinit var presenter: Presenter
    private var popupMenu: PopupMenu? = null
    private lateinit var viewModel: AlcoholCategoryViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_category)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(AlcoholCategoryViewModel::class.java)

        presenter = Presenter().apply {
            view = this@AlcoholCategory
        }
        presenter.inintTabLayout(this)

        if(intent.hasExtra(GlobalApplication.CATEGORY_BUNDLE)){
            val bundle = intent.getBundleExtra(GlobalApplication.CATEGORY_BUNDLE)
            val selectPosition = bundle?.getInt(GlobalApplication.MOVE_TYPE)
            selectPosition?.let {
                binding.viewPager2Container.currentItem = it
            }
        }

        //리스너를 onCreate에서 set한 이유는 implements를 하면 onCreate()를 불러오기도 전에
        //셋팅하는데 viewmodel은 onCreate 이후부터 호출할 수 있기 때문에 에러가 발생함.
       binding.tabLayoutAlcoholList.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.currentPosition.value = it.position
                    presenter.checkSort(it.position, viewModel.currentSort)
                    (it.customView as? TextView)?.let {
                        it.setTextColor(resources.getColor(R.color.orange,null))
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    (it.customView as? TextView)?.let {
                        it.setTextColor(resources.getColor(R.color.tabColor,null))
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewModel.currentPosition.observe(this, Observer {
            Log.e("현재 curr",binding.viewPager2Container.currentItem.toString())
            binding.textViewTotalProduct.text = "총 "+ viewModel.totalCountList[binding.viewPager2Container.currentItem].toString()+
                    "개의 상품이 있습니다."
        })
    }

    override fun onStart() {
        super.onStart()
        presenter.initNavigationItemSet(this,this)
        presenter.checkLogin(this)
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

            R.id.imageView_cancel -> {
                if(binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.closeDrawer(GravityCompat.END) }

            R.id.windowHeader_listCategory -> {
                if (!binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.openDrawer(GravityCompat.END) }

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
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }
}