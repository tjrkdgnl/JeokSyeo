package com.fragments.alcohol_category.viewpager_items

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding

class Fragment_Grid: BaseFragment<FragmentAlcholCategoryGridBinding>()
    , ViewPagerCategoryContact.CategoryBaseView<FragmentAlcholCategoryGridBinding> {
    override val layoutResID: Int = R.layout.fragment_alchol_category_grid
    private var typePosition =0
    private lateinit var viewmodel:AlcoholCategoryViewModel
    lateinit var gridPresenter: GridPresenter

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
            typePosition =  it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let {
            viewmodel = ViewModelProvider(it).get(AlcoholCategoryViewModel::class.java)
        }

        gridPresenter = GridPresenter().apply {
            this.viewObj =this@Fragment_Grid
            typePosition =this@Fragment_Grid.typePosition
            viewModel = viewmodel
            activity = this@Fragment_Grid.requireActivity()
        }

        gridPresenter.initRecyclerView(requireActivity())

        viewmodel.currentSort.observe(viewLifecycleOwner, Observer {
            gridPresenter.changeSort(it)
        })
    }

    override fun detachPresenter() {
        gridPresenter.detach()
    }

    override fun getbinding(): FragmentAlcholCategoryGridBinding {
       return binding
    }
}