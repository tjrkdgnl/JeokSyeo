package com.jeoksyeo.wet.activity.alchol_category

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
import com.adapter.alchol_category.GridViewPagerAdapter
import com.adapter.alchol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.fragment.alchol_category.Fragment_Grid
import com.fragment.alchol_category.Fragment_List
import com.google.android.material.tabs.TabLayout
import com.viewmodel.AlcholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholCategoryBinding

class AlcholCategory : FragmentActivity(), AlcholCategoryContact.BaseView, View.OnClickListener,
    PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: AlcholCategoryBinding
    private lateinit var presenter: Presenter
    private var popupMenu: PopupMenu? = null
    private lateinit var viewModel: AlcholCategoryViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_category)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(AlcholCategoryViewModel::class.java)

        presenter = Presenter().apply {
            view = this@AlcholCategory
        }
        presenter.inintTabLayout(this)


        if(intent.hasExtra(GlobalApplication.CATEGORY_BUNDLE)){
            val bundle = intent.getBundleExtra(GlobalApplication.CATEGORY_BUNDLE)
            val selectPosition = bundle?.getInt(GlobalApplication.MOVE_TYPE)
            selectPosition?.let {
                binding.viewPager2Container.currentItem = it
                viewModel.currentPosition.value = it
            }
        }

        //리스너를 onCreate에서 set한 이유는 implements를 하면 onCreate()를 불러오기도 전에
        //셋팅하는데 viewmodel은 onCreate 이후부터 호출할 수 있기 때문에 에러가 발생함.
       binding.tabLayoutAlcholList.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    Log.e("tab","in")
                    presenter.checkSort(it.position, viewModel.currentSort)
                    viewModel.currentPosition.value =it.position
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

        //현재 페이지의 아이템 총 개수를 observer
        viewModel.currentPosition.observe(this, Observer {position->
            Log.e("옵저버","in")
            if(viewModel.getCount(position) !=-1)
                setTotalCount(viewModel.getCount(position))
        })
    }

    override fun onStart() {
        super.onStart()
        presenter.initNavigationItemSet(this,this,GlobalApplication.userInfo.getProvider())
        presenter.checkLogin(this,GlobalApplication.userInfo.getProvider())
    }

    override fun getView(): AlcholCategoryBinding {
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
    override fun setTotalCount(alcholCount: Int) {
        binding.textViewTotalProduct.text = "총 " + alcholCount + "개의 주류가 있습니다."
    }


    //현재 보여지고 있는 fragment를 정한 sort방식으로 갱신
    fun executeSorting(sort: String) {
        val fragment = presenter.getFragement(binding.viewPager2Container.currentItem)
        viewModel.currentSort = sort

        if (fragment is Fragment_Grid) {
            fragment.changeSort(sort)
        } else if (fragment is Fragment_List) {
            fragment.changeSort(sort)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_cancel -> {
                if(binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.closeDrawer(GravityCompat.END)
            }

            R.id.windowHeader_listCategory -> {
                if (!binding.categoryDrawerLayout.isDrawerOpen(GravityCompat.END))
                    binding.categoryDrawerLayout.openDrawer(GravityCompat.END)
            }

            R.id.imageView_listToggle -> {
                changeToggle(false)
            }

            R.id.imageView_viewToggle -> {
                changeToggle(true)
            }

            R.id.sorting_constraintLayout -> {
                if (popupMenu == null) {
                    popupMenu = PopupMenu(applicationContext, binding.imageViewArrowToggle)
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
                binding.textViewCurrentOrdering.text = "리뷰순"
            }

            R.id.likeOrder -> {
                executeSorting("like")
                binding.textViewCurrentOrdering.text = "좋아요순"

            }

            R.id.degreeOrder_asc -> {
                executeSorting("abv-asc")
                binding.textViewCurrentOrdering.text = "도수 낮은순"
            }

            R.id.degreeOrder_desc -> {
                executeSorting("abv-desc")
                binding.textViewCurrentOrdering.text = "도수 높은순"
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

}