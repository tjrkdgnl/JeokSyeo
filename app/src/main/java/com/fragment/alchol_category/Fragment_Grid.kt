package com.fragment.alchol_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.alchol_category.GridAdapter
import com.model.alchol_category.AlcholList
import com.viewmodel.AlcholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding as FragmentAlcholCategoryGridBinding

class Fragment_Grid:Fragment(), Fg_AlcholCategoryContact.BaseView {

    private lateinit var binding: FragmentAlcholCategoryGridBinding
    var position =0
    private lateinit var viewmodel:AlcholCategoryViewModel
    private lateinit var gridAdapter:GridAdapter
    private lateinit var gridPresenter: GridPresenter

    companion object{
        fun newInstance(position:Int):Fragment_Grid{
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alchol_category_grid,container,false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(AlcholCategoryViewModel::class.java)

        gridPresenter = GridPresenter().apply {
            view =this@Fragment_Grid
            gridLayoutManager =GridLayoutManager(context, 2)
            position =this@Fragment_Grid.position
            sort = viewmodel.currentSort // 액티비티에서 변경된 sort로 정렬시키기 위함.
        }

        gridPresenter.initRecyclerView(requireContext(),null)

        return binding.root
    }

    fun changeSort(sort:String){
        gridPresenter.changeSort(sort)
    }

    override fun getbinding(): ViewDataBinding {
        return binding
    }

    override fun setAdapter(list: MutableList<AlcholList>) {
        gridAdapter = GridAdapter(requireContext(),list)
        binding.gridRecyclerView.adapter= gridAdapter
    }

    override fun updateList(list: MutableList<AlcholList>) {
        gridAdapter.updateList(list)
    }


    override fun changeSort(list: MutableList<AlcholList>) {
        gridAdapter.changeSort(list)
    }

    override fun getLastAlcholId(): String? {
        return gridAdapter.getLastAlcholId()
    }

    override fun getSort(): String {
        return gridPresenter.sort
    }

    override fun setTotalCount(alcholCount: Int) {
        Log.e("fragment set","in")
        viewmodel.setCount(alcholCount)
    }
}