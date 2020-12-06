package com.fragment.alcohol_category

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.viewmodel.AlcoholCategoryViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholCategoryGridBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GridPresenter : Fg_AlcoholCategoryContact.BasePresenter {
    override lateinit var view: Fg_AlcoholCategoryContact.BaseView
    override lateinit var context: Context
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var viewModel: AlcoholCategoryViewModel
    lateinit var sort:String
    var position = 0
    private val compositeDisposable = CompositeDisposable()

    //페이징을 위한 변수들
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading = false
    private var pageNum:Int = 1

    private val binding by lazy {
        view.getbinding() as FragmentAlcholCategoryGridBinding
    }
    val type: String by lazy {
        GlobalApplication.instance.getAlcoholType(position)
    }

    override fun initRecyclerView(context: Context) {
        JWTUtil.settingUserInfo()

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, 1.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewModel.changePosition.value = position

                    //주류 총 개수
                    it.data?.pagingInfo?.let { info ->
                        info.alcoholTotalCount?.let {total ->
                            viewModel.totalCountList[position] = total
                        }

                        info.page?.let {pageNumber->
                            pageNum = pageNumber.toInt() }
                    }

                    it.data?.alcoholList?.let { list ->
                        //어댑터 셋팅
                        view.setAdapter(list.toMutableList())

                        //리싸이클러뷰 셋팅
                        binding.gridRecyclerView.setHasFixedSize(false)
                        val spacingPixcel = context.resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
                        binding.gridRecyclerView.addItemDecoration(
                            GridSpacingItemDecoration(2, spacingPixcel, true, 0))
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
                            pagination(view.getLastAlcoholId())
                        }
                    }
                }
            }
        })
    }

    override fun pagination(alcoholId: String?) {
        JWTUtil.settingUserInfo()

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(), type
                    , GlobalApplication.PAGINATION_SIZE, sort, (pageNum+1).toString()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.let { info ->
                        info.page?.let {
                            pageNumber-> pageNum = pageNumber.toInt()
                        }

                        it.data?.alcoholList?.toMutableList()?.let { list ->
                            if(list.isNotEmpty()){
                                loading = false
                                view.updateList(list.toMutableList())
                            }
                            else{
                                Toast.makeText(context,"더 이상 주류가 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
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
        JWTUtil.settingUserInfo()

        setSortValue(sort)
        executeProgressBar(true)
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholCategory(
                    GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                    type, GlobalApplication.PAGINATION_SIZE, sort, "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.changeSort(it.data?.alcoholList?.toMutableList()!!)
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