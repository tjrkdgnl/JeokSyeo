package com.fragments.alcohol_category.viewpager_items

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryListBinding

class Fragment_List: BaseFragment<FragmentAlcholCategoryListBinding>()
    , ViewPagerCategoryContact.CategoryBaseView<FragmentAlcholCategoryListBinding> {
    override val layoutResID: Int =  R.layout.fragment_alchol_category_list
    private var typePosition = 0
    private lateinit var viewmodel:AlcoholCategoryViewModel
    lateinit var listPresenter: ListPresenter

    companion object{
        fun newInstance(position:Int): Fragment_List {
            val fragment = Fragment_List()
            val bundle = Bundle()
            bundle.putInt("position",position)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            typePosition = it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let {
            viewmodel = ViewModelProvider(it).get(AlcoholCategoryViewModel::class.java)

        }

        listPresenter = ListPresenter().apply {
            this.viewObj=this@Fragment_List
            typePosition =this@Fragment_List.typePosition
            viewModel = this@Fragment_List.viewmodel
            activity =this@Fragment_List.requireActivity()
        }

        listPresenter.initRecyclerView(requireContext())

        viewmodel.currentSort.observe(viewLifecycleOwner, Observer {
            changeSort(it)
        })
    }

    override fun detachPresenter() {
        listPresenter.detach()
    }

    fun changeSort(sort:String){
        listPresenter.changeSort(sort)
    }

    override fun getbinding(): FragmentAlcholCategoryListBinding {
        return binding
    }
}