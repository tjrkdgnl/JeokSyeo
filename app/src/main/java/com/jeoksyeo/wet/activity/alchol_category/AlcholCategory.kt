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
PopupMenu.OnMenuItemClickListener, TabLayout.OnTabSelectedListener{
    private lateinit var binding:AlcholCategoryBinding
    private lateinit var presenter:Presenter
    private  var popupMenu: PopupMenu? =null
    private lateinit var viewModel: AlcholCategoryViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_category)
        binding.lifecycleOwner =this

        intent?.let {
            binding.viewPager2Container.currentItem = it.getIntExtra(GlobalApplication.MOVE_TYPE, 0)
        }

        viewModel = ViewModelProvider(this).get(AlcholCategoryViewModel::class.java)

        viewModel.alcholTotalCount.observe(this, Observer {
            binding.textViewTotalProduct.text = "총" + it.toString() + "개의 주류가 있습니다."
        })

        presenter = Presenter().apply {
            view=this@AlcholCategory
        }

        presenter.inintTabLayout(this)

        binding.tabLayoutAlcholList.addOnTabSelectedListener(this)
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

    fun executeSorting(sort:String){
       val fragment = presenter.getFragement(binding.viewPager2Container.currentItem)
        viewModel.currentSort = sort

        if(fragment is Fragment_Grid){
            fragment.changeSort(sort)
        }
        else if (fragment is Fragment_List){
            //
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.imageView_listToggle -> {
                changeToggle(false)
            }

            R.id.imageView_viewToggle -> {
                changeToggle(true)
            }

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

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val fragment = tab?.position?.let { presenter.getFragement(it) }
        if(fragment is Fragment_Grid){
            Log.e("안들어오나?","아니들어옴")
            if(fragment.getSort() != viewModel.currentSort){
                Log.e("fragment_sort",fragment.getSort())
                Log.e("viewmodel_sort",viewModel.currentSort)

                fragment.changeSort(viewModel.currentSort)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}