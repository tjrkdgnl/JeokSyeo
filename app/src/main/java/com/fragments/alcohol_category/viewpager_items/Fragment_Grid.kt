package com.fragments.alcohol_category.viewpager_items

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adapters.alcohol_category.GridAdapter
import com.base.BaseFragment
import com.model.alcohol_category.AlcoholList
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding

class Fragment_Grid: BaseFragment<FragmentAlcholCategoryGridBinding>(), Fg_AlcoholCategoryContact.BaseView {
    override val layoutResID: Int = R.layout.fragment_alchol_category_grid

    var position =0
    private lateinit var viewmodel:AlcoholCategoryViewModel
    private lateinit var gridAdapter:GridAdapter
    private lateinit var gridPresenter: GridPresenter

    companion object{
        fun newInstance(position:Int): Fragment_Grid {
            val fragment = Fragment_Grid()
            val bundle = Bundle()
            bundle.putInt("position",position)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position =  it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let {
            viewmodel = ViewModelProvider(it).get(AlcoholCategoryViewModel::class.java)

        }

        gridPresenter = GridPresenter().apply {
            this.view =this@Fragment_Grid
            gridLayoutManager =GridLayoutManager(requireActivity(), 2)
            position =this@Fragment_Grid.position
            viewModel = viewmodel
            sort = viewmodel.currentSort // 액티비티에서 변경된 sort로 정렬시키기 위함.
            context = this@Fragment_Grid.requireActivity()
        }

        gridPresenter.initRecyclerView(requireActivity())

    }

    fun changeSort(sort:String){
        gridPresenter.changeSort(sort)
    }

    override fun getbinding(): ViewDataBinding {
        return binding
    }

    override fun setAdapter(list: MutableList<AlcoholList>) {
        gridAdapter = GridAdapter(requireActivity(),list
            ,executeProgressBar = gridPresenter.executeProgressBar)
        binding.gridRecyclerView.adapter= gridAdapter
    }

    override fun updateList(list: MutableList<AlcoholList>) {
        gridAdapter.updateList(list)
    }

    override fun changeSort(list: MutableList<AlcoholList>) {
        gridPresenter.pageNum=1
        gridAdapter.changeSort(list)
    }

    override fun getLastAlcoholId(): String? {
        return gridAdapter.getLastAlcoholId()
    }

    override fun getSort(): String {
        return gridPresenter.sort
    }

    override fun moveTopPosition() {
        binding.gridRecyclerView.smoothScrollToPosition(0)
    }

}