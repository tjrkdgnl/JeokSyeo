package com.fragments.favorite

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.favorite.FavoriteAdapter
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.model.favorite.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter : FavoriteContract.FavoritePresenter {
    override val view: FavoriteContract.FavoriteView by lazy {
        viewObj!!
    }
    override var viewObj: FavoriteContract.FavoriteView? = null
    override lateinit var activity: Activity
    override lateinit var viewModel: FavoriteViewModel
    var position: Int = 0
    private val compositeDisposable = CompositeDisposable()

    private lateinit var favoriteAdapter: FavoriteAdapter
    private val gridLayoutManager by lazy {
        GridLayoutManager(activity, 2)
    }

    //페이징 처리를 위한 변수들
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading = false
    private var pageNum: Int = 1



    private val setProgressBar: (Boolean) -> Unit = {
        if (it) {
            view.getBindingObj().favoriteProgressbar.root.visibility = View.VISIBLE
        } else {
            view.getBindingObj().favoriteProgressbar.root.visibility = View.INVISIBLE
        }
    }

    override fun getMyAlcohol() {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(position),
                GlobalApplication.PAGINATION_SIZE,
                pageNum
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                //다음 페이지의 아이템을 얻기 위해서 page 넘버를 저장한다.
                it.data?.pagingInfo?.page?.let { page ->
                    pageNum = page.toInt()
                }

                //뷰모델에 해당 타입의 총 찜한 주류 개수를 저장한다.
                it.data?.pagingInfo?.alcoholTotalCount?.let { total ->
                    viewModel.alcoholTypeList[position] = total
                    viewModel.setPosition(position)
                }

                //모든 타입별로 찜한 개수 저장
                //이는 type별로 찜한 주류를 호출할 때마다 셋팅되므로 한번 셋팅되면 더이상 셋팅 될 필요가
                //없기 때문에 분기처리를 한다.
                it.data?.summary?.alcoholLikeCount?.let { total ->
                    if (viewModel.summaryCount.value != total) {
                        viewModel.summaryCount.value = total
                    }
                }

                it.data?.alcoholList?.let { list ->
                    if (list.isNotEmpty()) {
                        favoriteAdapter = FavoriteAdapter(list.toMutableList(), setProgressBar)

                        setAdapter(
                            list.toMutableList(), gridLayoutManager, GridSpacingItemDecoration(
                                2,
                                activity.resources.getDimensionPixelSize(R.dimen.grid_layout_margin),
                                true,
                                0
                            )
                        )
                        initScrollListener()

                    } else {
                        val alcohol = AlcoholList()
                        alcohol.type = -1

                        setAdapter(mutableListOf<AlcoholList>().apply {
                            this.add(alcohol)
                        }, LinearLayoutManager(activity),null)
                    }
                }

            }, { t ->
                Log.e(ErrorManager.FAVORITE, t.message.toString())

            })
        )
    }

    private fun setAdapter(
        list: MutableList<AlcoholList>,
        layoutManager: RecyclerView.LayoutManager,
        decoration: GridSpacingItemDecoration?
    ) {
        favoriteAdapter = FavoriteAdapter(list, setProgressBar)

        view.getBindingObj().favoriteRecyclerView.apply {
            adapter =favoriteAdapter
            setHasFixedSize(false)
            this.layoutManager = layoutManager

            decoration?.let {
                addItemDecoration(it)
            }
        }
    }


    //다음 페이지의 정보를 얻기위해서 먼저 유저의 스크롤 위치를 계산하는 메서드
    private fun initScrollListener() {
        view.getBindingObj().favoriteRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = gridLayoutManager.childCount
                totalItemCount = gridLayoutManager.itemCount
                pastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if (!loading) {
                        //스크롤이 일정 범위로 밑에 도달할 시, pagination 호출
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            loading = true
                            pagination()
                        }
                    }

                }
            }
        })
    }

    private fun pagination() {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(position),
                GlobalApplication.PAGINATION_SIZE,
                pageNum + 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.pagingInfo?.page?.let { page ->
                    pageNum = page.toInt()
                }

                it.data?.alcoholList?.let { list ->
                    if (list.isNotEmpty()) {
                        loading = false
                        favoriteAdapter.pageUpdate(list.toMutableList())
                    }
                }

            }, { t ->
                loading = false
                Log.e(ErrorManager.FAVORITE, t.message.toString())
            })
        )
    }

    override fun detach() {
        compositeDisposable.dispose()
        viewObj = null
    }
}