package com.fragment.alcohol_category

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
import com.adapter.alcohol_category.GridAdapter
import com.model.alcohol_category.AlcoholList
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding

class Fragment_Grid:Fragment(), Fg_AlcoholCategoryContact.BaseView {

    private lateinit var binding: FragmentAlcholCategoryGridBinding
    var position =0
    private lateinit var viewmodel:AlcoholCategoryViewModel
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

        viewmodel = ViewModelProvider(requireActivity()).get(AlcoholCategoryViewModel::class.java)


        gridPresenter = GridPresenter().apply {
            view =this@Fragment_Grid
            gridLayoutManager =GridLayoutManager(requireActivity(), 2)
            position =this@Fragment_Grid.position
            viewModel = viewmodel
            sort = viewmodel.currentSort // 액티비티에서 변경된 sort로 정렬시키기 위함.
            context = this@Fragment_Grid.requireActivity()
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        Log.e("프래그먼트 onPause","pause")
    }

    override fun onResume() {
        super.onResume()

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
}