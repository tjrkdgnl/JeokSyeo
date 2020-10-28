package com.fragment.alchol_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.alchol_category.ListAdapter
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.AlcholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryListBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Fragment_List:Fragment() {
    private lateinit var binding:FragmentAlcholCategoryListBinding
    private var position = 0
    private lateinit var viewmodel:AlcholCategoryViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var listAdapter:ListAdapter
    private var visibleItemCount =0
    private var  totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading =false
    private var previousSort:String = "like"
    private lateinit var mLinearLayoutManager:LinearLayoutManager

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




        initRecyclerView(GlobalApplication.instance.getAlcholType(position),viewmodel.currentSort,null)

        return binding.root
    }

    fun initRecyclerView(type:String, sort:String, lastAlcholId:String?){
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
            .getAlcholCategory(
                GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                type,20,sort,lastAlcholId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewmodel.alcholTotalCount.value = it.data?.pagingInfo?.alcholTotalCount

                it.data?.alcholList?.let { list->
                    listAdapter = ListAdapter(list.toMutableList())
                    binding.listRecyclerView.adapter = listAdapter
                    binding.listRecyclerView.setHasFixedSize(true)
                    mLinearLayoutManager = LinearLayoutManager(requireContext())
                    binding.listRecyclerView.layoutManager = mLinearLayoutManager

                    initScrollListener()
                }
            },{t -> Log.e(ErrorManager.ALCHOL_CATEGORY,t.message.toString())}))
    }

    private fun initScrollListener(){
        binding.listRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = mLinearLayoutManager.childCount
                totalItemCount = mLinearLayoutManager.itemCount
                pastVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

                if(!loading){
                    if((visibleItemCount + pastVisibleItem) >= totalItemCount){
                        loading=true
                        pagination(listAdapter.getLastAlcholId())
                    }
                }
            }
        })
    }

    private fun pagination(lastAlcholId:String?){
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getAlcholCategory(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
            GlobalApplication.instance.getAlcholType(position),20,viewmodel.currentSort,lastAlcholId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.pagingInfo?.next?.let {next ->
                    if(next){
                        it.data?.alcholList?.toMutableList()?.let { list -> listAdapter.updateList(list)
                            loading=false
                        }
                    }
                }
            },{t->
                loading=false
                Log.e(ErrorManager.PAGINATION,t.message.toString())}))
    }

}