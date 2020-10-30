package com.fragment.alchol_category

import android.content.Context
import android.util.Log
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


    override fun initRecyclerView(context: Context, lastAlcholId: String?) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, lastAlcholId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //주류 총 개수
                    it.data?.pagingInfo?.alcholTotalCount?.let { total ->
                        Log.e("total","in")
                       view.setTotalCount(total)
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
                    GlobalApplication.userInfo.getAccessToken(), type, 20, sort, alcholId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.next?.let { next ->
                        if (next) {
                            it.data?.alcholList?.toMutableList()?.let { list ->
                                view.updateList(list.toMutableList())
                                loading = false
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
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.changeSort(it.data?.alcholList?.toMutableList()!!)
                }, { t -> Log.e(ErrorManager.PAGINATION_CHANGE, t.message.toString()) })
        )
    }

    override fun setSortValue(sort: String) {
        this.sort = sort
    }
}