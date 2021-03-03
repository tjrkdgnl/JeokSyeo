package com.fragments.alcohol_category.viewpager_items

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.alcohol_category.GridAdapter
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.model.alcohol_category.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GridPresenter : ViewPagerCategoryContact.BasePresenter<FragmentAlcholCategoryGridBinding> {
    override lateinit var view: ViewPagerCategoryContact.CategoryBaseView<FragmentAlcholCategoryGridBinding>
    override lateinit var activity: Activity
    private lateinit var gridLayoutManager: GridLayoutManager
    lateinit var viewModel: AlcoholCategoryViewModel
    private lateinit var gridAdapter: GridAdapter
    var typePosition = 0
    private val compositeDisposable = CompositeDisposable()

    //페이징을 위한 변수들
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading = false
    var pageNum: Int = 1

    val type: String by lazy {
        GlobalApplication.instance.getAlcoholType(typePosition)
    }

    override fun initRecyclerView(context: Context) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    type,
                    GlobalApplication.PAGINATION_SIZE,
                    viewModel.currentSort.value!!,
                    pageNum.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    //주류 총 개수
                    it.data?.pagingInfo?.let { info ->
                        info.alcoholTotalCount?.let { total ->
                            //서버로부터 데이터가 셋팅되는 딜레이가 존재하기 때문에
                            //AlcoholCategoryFragment에서 총 개수를 표시하는 옵저버에 걸리지 않는다.
                            //때문에 서버에서 받아온 데이터 셋팅이 끝나면 다시 옵저버가 호출될 수 있도록 하기위해서
                            //liveData의 값을 변경한다.
                            viewModel.totalCountList[typePosition] = total
                            viewModel.changePosition.value = typePosition
                        }

                        //페이지네이션을 통해 데이터를 정크단위로 받아오기 때문에 pageNumber를 기억하고 있어야한다.
                        info.page?.let { pageNumber ->
                            pageNum = pageNumber.toInt()
                        }
                    }

                    it.data?.alcoholList?.let { list ->
                        //어댑터 셋팅
                        setAdapter(list.toMutableList())

                        //페이지네이션 호출을 위한 리스너 셋팅
                        initScrollListener()
                    }

                }, { t ->
                    Log.e(ErrorManager.ALCHOL_CATEGORY, t.message.toString())
                })
        )
    }

    override fun initScrollListener() {
        view.getbinding().gridRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = gridLayoutManager.childCount
                totalItemCount = gridLayoutManager.itemCount
                pastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if (!loading) {
                        //유저가 일정부분 스크롤을 하면 페이지네이션을 통해 데이터 추가
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            loading = true
                            pagination()
                        }
                    }
                }
            }
        })
    }

    override fun pagination() {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    type
                    ,
                    GlobalApplication.PAGINATION_SIZE,
                    viewModel.currentSort.value!!,
                    (pageNum + 1).toString()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.let { info ->
                        info.page?.let { pageNumber ->
                            pageNum = pageNumber.toInt()
                        }

                        it.data?.alcoholList?.toMutableList()?.let { list ->
                            loading = false
                            if (list.isNotEmpty()) {

                                updateList(list.toMutableList())
                            } else {
                                Toast.makeText(activity, "더 이상 주류가 존재하지 않습니다.", Toast.LENGTH_SHORT)
                                    .show()
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
        executeProgressBar(true)
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    type,
                    GlobalApplication.PAGINATION_SIZE,
                    sort,
                    "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    changeSortedList(it.data?.alcoholList?.toMutableList()!!)
                    executeProgressBar(false)
                }, { t ->
                    Log.e(ErrorManager.PAGINATION_CHANGE, t.message.toString())
                    executeProgressBar(false)
                })
        )
    }

    override fun setAdapter(list: MutableList<AlcoholList>) {
        gridAdapter = GridAdapter(activity, list.toMutableList(), executeProgressBar)
        gridLayoutManager = GridLayoutManager(activity, 2)

        view.getbinding().gridRecyclerView.adapter = gridAdapter
        view.getbinding().gridRecyclerView.setHasFixedSize(false)
        view.getbinding().gridRecyclerView.layoutManager = gridLayoutManager

        view.getbinding().gridRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                activity.resources.getDimensionPixelSize(R.dimen.grid_layout_margin),
                true,
                0
            )
        )
    }

    override fun updateList(list: MutableList<AlcoholList>) {
        gridAdapter.updateList(list)
    }

    override fun changeSortedList(list: MutableList<AlcoholList>) {
        gridAdapter.changeSort(list)
    }

    override fun moveTopPosition() {
        view.getbinding().gridRecyclerView.smoothScrollToPosition(0)
    }

    val executeProgressBar: (Boolean) -> Unit = { execute ->
        if (execute) {
            view.getbinding().gridProgressBar.root.visibility = View.VISIBLE

        } else {
            view.getbinding().gridProgressBar.root.visibility = View.INVISIBLE
        }
    }
}