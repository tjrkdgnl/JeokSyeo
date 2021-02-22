package com.fragment.alcohol_category.viewpager_items

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcohol_category.ListAdapter
import com.base.BaseFragment
import com.model.alcohol_category.AlcoholList
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryListBinding

class Fragment_List: BaseFragment<FragmentAlcholCategoryListBinding>(), Fg_AlcoholCategoryContact.BaseView {
    override val layoutResID: Int =  R.layout.fragment_alchol_category_list
    private var position = 0
    private lateinit var viewmodel:AlcoholCategoryViewModel
    private lateinit var listAdapter:ListAdapter
    private lateinit var listPresenter: ListPresenter

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
            position = it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let {
            viewmodel = ViewModelProvider(it).get(AlcoholCategoryViewModel::class.java)

        }

        listPresenter = ListPresenter().apply {
            this.view=this@Fragment_List
            position =this@Fragment_List.position
            linearLayoutManager= LinearLayoutManager(requireContext())
            sort=viewmodel.currentSort
            viewModel = this@Fragment_List.viewmodel
            context =this@Fragment_List.requireActivity()
        }

        listPresenter.initRecyclerView(requireContext())

    }

    fun changeSort(sort:String){
        listPresenter.changeSort(sort)
    }

    override fun getbinding(): ViewDataBinding {
        return binding
    }

    override fun setAdapter(list: MutableList<AlcoholList>) {
        listAdapter = ListAdapter(requireContext(),list,
            executeProgressBar = listPresenter.executeProgressBar)
        binding.listRecyclerView.adapter= listAdapter
    }

    override fun updateList(list: MutableList<AlcoholList>) {
        listAdapter.updateList(list)
    }

    override fun changeSort(list: MutableList<AlcoholList>) {
        listPresenter.pageNum=1
        listAdapter.changeSort(list)
    }

    override fun getLastAlcoholId(): String? {
        return listAdapter.getLastAlcoholId()
    }

    override fun getSort(): String {
        return listPresenter.sort
    }

    override fun moveTopPosition() {
        binding.listRecyclerView.smoothScrollToPosition(0)
    }

}