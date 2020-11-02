package com.fragment.alchol_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alchol_category.ListAdapter
import com.model.alchol_category.AlcholList
import com.viewmodel.AlcholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryListBinding

class Fragment_List:Fragment(), Fg_AlcholCategoryContact.BaseView {
    private lateinit var binding:FragmentAlcholCategoryListBinding
    private var position = 0
    private lateinit var viewmodel:AlcholCategoryViewModel
    private lateinit var listAdapter:ListAdapter
    private lateinit var listPresenter:ListPresenter
    companion object{
        fun newInstance(position:Int):Fragment_List{
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alchol_category_list,container,false)
        binding.lifecycleOwner=this

        viewmodel = ViewModelProvider(requireActivity()).get(AlcholCategoryViewModel::class.java)

        listPresenter = ListPresenter().apply {
            view=this@Fragment_List
            position =this@Fragment_List.position
            linearLayoutManager= LinearLayoutManager(requireContext())
            sort=viewmodel.currentSort
        }

        listPresenter.initRecyclerView(requireContext())

        return binding.root
    }

    fun changeSort(sort:String){
        listPresenter.changeSort(sort)
    }

    override fun getbinding(): ViewDataBinding {
        return binding
    }

    override fun setAdapter(list: MutableList<AlcholList>) {
        listAdapter = ListAdapter(requireContext(),list,
            executeProgressBar = listPresenter.executeProgressBar)
        binding.listRecyclerView.adapter= listAdapter
    }

    override fun updateList(list: MutableList<AlcholList>) {
        listAdapter.updateList(list)
    }

    override fun changeSort(list: MutableList<AlcholList>) {
        listAdapter.changeSort(list)
    }

    override fun getLastAlcholId(): String? {
        return listAdapter.getLastAlcholId()
    }

    override fun getSort(): String {
        return listPresenter.sort
    }

    override fun setTotalCount(alcholCount: Int) {
        viewmodel.setCount(alcholCount)
    }
}