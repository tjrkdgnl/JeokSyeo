package com.fragments.alcohol_category

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.activities.main.MainActivity
import com.adapters.alcohol_category.GridViewPagerAdapter
import com.adapters.alcohol_category.ListViewPagerAdapter
import com.application.GlobalApplication
import com.base.BaseFragment
import com.fragments.search.SearchFragment
import com.google.android.material.tabs.TabLayout
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlcoholCategoryFragment : BaseFragment<AlcoholCategoryBinding>(),
    AlcoholCategoryContact.AlcoholCategoryView,
    TabLayout.OnTabSelectedListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    override val layoutResID: Int = R.layout.alcohol_category
    private lateinit var categoryPresenter: Presenter
    private lateinit var viewModel: AlcoholCategoryViewModel
    private var currentItem: Int = 0
    private var popupMenu: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentItem = it.getInt(GlobalApplication.MOVE_TYPE)
        }

        viewModel = ViewModelProvider(this).get(AlcoholCategoryViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryPresenter = Presenter().apply {
            viewObj = this@AlcoholCategoryFragment
            activity = requireActivity()
            this.viewmodel = this@AlcoholCategoryFragment.viewModel
        }

        //리스너 셋팅 및 텍스트 크기 지정
        init()

        //주류 타입이 변경될 때마다 토탈개수를 실시간으로 표시
        viewModel.changePosition.observe(requireActivity(), Observer {
            binding.textViewTotalProduct.text =
                "총  ${viewModel.totalCountList[binding.viewPager2Container.currentItem]}개의 주류가 있습니다."
        })

        //뷰페이저 초기화
        categoryPresenter.inintTabLayout(this, currentItem)

    }

    override fun detachPresenter() {
        categoryPresenter.detach()
    }

    override fun getBindingObj(): AlcoholCategoryBinding {
        return binding
    }

    override fun init() {

        //프래그먼트는 xml에서 클릭리스너를 전달해주지 못하기 때문에 수동으로 셋팅해줘야 한다.
        binding.tabLayoutAlcoholList.addOnTabSelectedListener(this)
        binding.imageViewArrowToggle.setOnClickListener(this)
        binding.imageViewListToggle.setOnClickListener(this)
        binding.imageViewViewToggle.setOnClickListener(this)
        binding.windowHeader.windowHeaderSearchButton.setOnClickListener(this)
        binding.sortingConstraintLayout.setOnClickListener(this)
        binding.windowHeader.windowHeaderLogo.setOnClickListener(this)

        //토탈 개수 디바이스 사이즈로 핸들링
        binding.textViewTotalProduct.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            GlobalApplication.instance.getCalculatorTextSize(11f)
        )

        binding.viewPager2Container.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                //실시간으로 변경되는 주류 타입의 개수를 표시하기위해서 포지션을 뷰모델에 저장
                viewModel.changePosition.value = position

                //선택된 탭의 텍스트 색 변경 -> 버전분기처리
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(
                        resources.getColor(R.color.orange, null)
                    )
                } else {
                    (binding.tabLayoutAlcoholList.getTabAt(position)?.customView as TextView).setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.orange)
                    )
                }
            }
        })
    }

    override fun changeToggle(toggle: Boolean) {
        val offset = binding.viewPager2Container.currentItem
        if (toggle) {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_off)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_on)

            activity?.let {
                binding.viewPager2Container.adapter = GridViewPagerAdapter(this)
            }

        } else {
            binding.imageViewListToggle.setImageResource(R.mipmap.list_on)
            binding.imageViewViewToggle.setImageResource(R.mipmap.grid_off)

            activity?.let {
                binding.viewPager2Container.adapter = ListViewPagerAdapter(this)
            }
        }
        binding.viewPager2Container.currentItem = offset
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (tab?.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor, null))
        } else {
            (tab?.customView as? TextView)?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
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
                (activity as? MainActivity)?.addToFragment(
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
                categoryPresenter.executeSorting("review")
                binding.textViewCurrentOrdering.text = "리뷰순"
            }
            R.id.likeOrder -> {
                categoryPresenter.executeSorting("like")
                binding.textViewCurrentOrdering.text = "좋아요순"
            }
            R.id.degreeOrder_asc -> {
                categoryPresenter.executeSorting("abv-asc")
                binding.textViewCurrentOrdering.text = "도수 낮은순"
            }
            R.id.degreeOrder_desc -> {
                categoryPresenter.executeSorting("abv-desc")
                binding.textViewCurrentOrdering.text = "도수 높은순"
            }
        }
        return true
    }
}