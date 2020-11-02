package com.fragment.alchol_category

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GridPresenter : Fg_AlcholCategoryContact.BasePresenter {
    override lateinit var view: Fg_AlcholCategoryContact.BaseView
    private val binding by lazy {
        view.getbinding() as FragmentAlcholCategoryGridBinding
    }

    lateinit var gridLayoutManager: GridLayoutManager
    val type: String by lazy {
        GlobalApplication.instance.getAlcholType(position)
    }
    lateinit var sort: String
    var position = 0
    private val compositeDisposable = CompositeDisposable()
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading = false
    private var pageNum:Int = 1

    override fun initRecyclerView(context: Context) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, 1.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //주류 총 개수
                    it.data?.pagingInfo?.let { info ->
                        info.alcholTotalCount?.let {total ->
                            view.setTotalCount(total)
                        }
                        info.page?.let {pageNumber->
                            pageNum = pageNumber.toInt()
                        }
                    }

                    it.data?.alcholList?.let { list ->
                        //어댑터 셋팅
                        view.setAdapter(list.toMutableList())

                        //리싸이클러뷰 셋팅
                        binding.gridRecyclerView.setHasFixedSize(true)
                        val spacingPixcel = context.resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
                        binding.gridRecyclerView.addItemDecoration(
                            GridSpacingItemDecoration(2, spacingPixcel, true, 0)
                        )
                        binding.gridRecyclerView.layoutManager = gridLayoutManager

                        initScrollListener()
                    }
                }, { t -> Log.e(ErrorManager.ALCHOL_CATEGORY, t.message.toString()) })
        )
    }

    override fun initScrollListener() {
        binding.gridRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = gridLayoutManager.childCount
                totalItemCount = gridLayoutManager.itemCount
                pastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            loading = true
                            pagination(view.getLastAlcholId())
                        }
                    }
                }
            }
        })
    }

    override fun pagination(alcholId: String?) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholCategory(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(), type
                    , GlobalApplication.PAGINATION_SIZE, sort, (pageNum+1).toString()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.let { info ->
                        info.page?.let { pageNumber-> pageNum = pageNumber.toInt() }
                        info.next?.let { next->
                            if (next) {
                                it.data?.alcholList?.toMutableList()?.let { list ->
                                    view.updateList(list.toMutableList())
                                    loading = false
                                }
                            }
                        }
                    }
                }, { t ->
                    loading = false
                    Log.e(ErrorManager.PAGINATION, t.message.toString())
                })
        )
    }

    override fun changeSort(sort: String) {
        setSortValue(sort)
        executeProgressBar(true)
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.changeSort(it.data?.alcholList?.toMutableList()!!)
                    executeProgressBar(false)
                }, { t ->
                    Log.e(ErrorManager.PAGINATION_CHANGE, t.message.toString())
                    executeProgressBar(false)
                })
        )
    }

    override fun setSortValue(sort: String) {
        this.sort = sort
    }

     val executeProgressBar:(Boolean)->Unit = {execute->
        if(execute){
            binding.gridProgressBar.root.visibility = View.VISIBLE

        }
        else{
            binding.gridProgressBar.root.visibility = View.INVISIBLE
     }

    }
}