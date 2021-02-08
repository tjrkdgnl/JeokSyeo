package com.fragment.alcohol_category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.adapter.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.fragment.alcohol_category.viewpager_items.Fragment_Grid
import com.fragment.alcohol_category.viewpager_items.Fragment_List
import com.fragment.main.MainFragment
import com.fragment.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.jeoksyeo.wet.activity.main.MainActivity
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlcoholCategoryFragment : Fragment(), AlcoholCategoryContact.BaseView,
    TabLayout.OnTabSelectedListener
    , View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: AlcoholCategoryBinding
    private lateinit var categoryPresenter: Presenter
    private lateinit var activityContext: Context
    private lateinit var viewModel: AlcoholCategoryViewModel
    private var currentItem: Int = 0
    private var popupMenu: PopupMenu? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentItem = it.getInt(GlobalApplication.MOVE_TYPE)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.alcohol_category, container, false)

        categoryPresenter = Presenter().apply {
            view = this@AlcoholCategoryFragment
            this.context = activityContext
        }

        binding.tabLayoutAlcoholList.addOnTabSelectedListener(this)
        binding.imageViewArrowToggle.setOnClickListener(this)
        binding.imageViewListToggle.setOnClickListener(this)
        binding.imageViewViewToggle.setOnClickListener(this)
        binding.windowHeader.windowHeaderSearchButton.setOnClickListener(this)
        binding.sortingConstraintLayout.setOnClickListener(this)
        binding.windowHeader.windowHeaderLogo.setOnClickListener(this)


        viewModel = ViewModelProvider(requireActivity()).get(AlcoholCategoryViewModel::class.java)

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뷰페이저 초기화
        categoryPresenter.inintTabLayout(activityContext, currentItem)

        binding.viewPager2Container.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItem = position
                binding.viewPager2Container.currentItem = position
                viewModel.changePosition.value = position
                categoryPresenter.checkSort(position, viewModel.currentSort)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(
                        resources.getColor(R.color.orange, null)
                    )
                } else {
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(
                        ContextCompat.getColor(activityContext, R.color.orange)
                    )
                }
            }
        })

        viewModel.changePosition.observe(requireActivity(), Observer {
            setTotalCount(viewModel.totalCountList[binding.viewPager2Container.currentItem]) }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        categoryPresenter.detach()
    }


    override fun getViewBinding(): AlcoholCategoryBinding {
        return binding
    }

    //현재 보여지고 있는 fragment를 정한 sort방식으로 갱신
    fun executeSorting(sort: String) {
        val fragment = categoryPresenter.getFragement(binding.viewPager2Container.currentItem)
        viewModel.currentSort = sort

        if (fragment is Fragment_Grid)
            fragment.changeSort(sort)
        else if (fragment is Fragment_List)
            fragment.changeSort(sort)
    }

    override fun changeToggle(toggle: Boolean) {
        val offset = binding.viewPager2Container.currentItem
        if (toggle) {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_off)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_on)

            activity?.let {
                binding.viewPager2Container.adapter = GridViewPagerAdapter(it)
            }

        } else {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_on)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_off)

            activity?.let {
                binding.viewPager2Container.adapter = ListViewPagerAdapter(it)
            }
        }
        binding.viewPager2Container.currentItem = offset
    }


    @SuppressLint("SetTextI18n")
    override fun setTotalCount(alcoholCount: Int) {
        binding.textViewTotalProduct.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(11f))
        binding.textViewTotalProduct.text = "총  ${alcoholCount}개의 주류가 있습니다."
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (tab?.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor, null))
        } else {
            (tab?.customView as? TextView)?.setTextColor(
                ContextCompat.getColor(
                    activityContext,
                    R.color.tabColor
                )
            )
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.windowHeader_SearchButton -> {
                (activity as? MainActivity)?.replaceFragment(
                    SearchFragment.newInstance(),
                    "search"
                )
            }

            R.id.windowHeader_logo -> {
                CoroutineScope(Dispatchers.IO).launch {
                    (activity as FragmentActivity).supportFragmentManager.popBackStack()
                }
            }

            R.id.imageView_listToggle -> {
                changeToggle(false)
            }

            R.id.imageView_viewToggle -> {
                changeToggle(true)
            }

            R.id.sorting_constraintLayout -> {
                if (popupMenu == null) {
                    popupMenu = PopupMenu(context, binding.sortingConstraintLayout)
                    activity?.menuInflater?.inflate(R.menu.order_menu, popupMenu!!.menu)
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
}