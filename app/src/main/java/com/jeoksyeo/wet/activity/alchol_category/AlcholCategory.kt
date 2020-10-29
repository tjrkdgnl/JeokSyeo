package com.jeoksyeo.wet.activity.alchol_category

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adapter.alchol_category.GridViewPagerAdapter
import com.adapter.alchol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.fragment.alchol_category.Fragment_Grid
import com.fragment.alchol_category.Fragment_List
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.viewmodel.AlcholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholCategoryBinding

class AlcholCategory: FragmentActivity(), AlcholCategoryContact.BaseView, View.OnClickListener,
PopupMenu.OnMenuItemClickListener{
    private lateinit var binding:AlcholCategoryBinding
    private lateinit var presenter:Presenter
    private  var popupMenu: PopupMenu? =null
    private lateinit var viewModel: AlcholCategoryViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_category)
        binding.lifecycleOwner =this

        viewModel = ViewModelProvider(this).get(AlcholCategoryViewModel::class.java)

        presenter = Presenter().apply {
            view=this@AlcholCategory
        }

        presenter.inintTabLayout(this)
        intent?.let {
            val selectPosition = it.getIntExtra(GlobalApplication.MOVE_TYPE, 0)
            binding.viewPager2Container.currentItem = selectPosition
            viewModel.viewModelCurrentPosition.value = selectPosition
        }

        binding.tabLayoutAlcholList.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    presenter.checkSort(it.position,viewModel.currentSort)
                    viewModel.viewModelCurrentPosition.value = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewModel.viewModelCurrentPosition.observe(this, Observer {
            presenter.callTotalCount(it)
        })

        binding.imageViewArrowToggle.setOnClickListener(this)
        binding.imageViewListToggle.setOnClickListener(this)
        binding.imageViewViewToggle.setOnClickListener(this)
    }

    override fun getView(): AlcholCategoryBinding {
        return binding
    }

    override fun changeToggle(toggle: Boolean) {
        val offset = binding.viewPager2Container.currentItem
        if(toggle){
            binding.imageViewListToggle.setImageResource(R.mipmap.list_off)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_on)
            binding.viewPager2Container.adapter = GridViewPagerAdapter(this)
        }
        else{
            binding.imageViewListToggle.setImageResource(R.mipmap.list_on)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_off)
            binding.viewPager2Container.adapter = ListViewPagerAdapter(this)
        }
        binding.viewPager2Container.currentItem = offset
    }

    @SuppressLint("SetTextI18n")
    override fun setTotalCount(alcholCount: Int) {
        binding.textViewTotalProduct.text = "총 "+ alcholCount+"개의 주류가 있습니다."
    }


    //현재 보여지고 있는 fragment 갱신
    fun executeSorting(sort:String){
       val fragment = presenter.getFragement(binding.viewPager2Container.currentItem)
        viewModel.currentSort = sort

        if(fragment is Fragment_Grid){
            fragment.changeSort(sort)
        }
        else if (fragment is Fragment_List){
            fragment.changeSort(sort)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageView_listToggle -> { changeToggle(false) }

            R.id.imageView_viewToggle -> { changeToggle(true) }

            R.id.imageView_ArrowToggle -> {
                if (popupMenu == null) {
                    popupMenu = PopupMenu(applicationContext, binding.imageViewArrowToggle )
                    menuInflater.inflate(R.menu.order_menu, popupMenu!!.menu)
                    popupMenu!!.setOnMenuItemClickListener(this)
                }
                popupMenu!!.show()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.reviewOrder -> {
                executeSorting("review")
                binding.textViewCurrentOrdering.text = "리뷰순"
            }

            R.id.likeOrder -> {
                executeSorting("like")
                binding.textViewCurrentOrdering.text = "좋아요순"

            }

            R.id.degreeOrder -> {
                executeSorting("abv")
                binding.textViewCurrentOrdering.text = "도수순"
            }
        }
        return true
    }
}