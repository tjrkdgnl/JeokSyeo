package com.fragment.favorite

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.favorite.FavoriteAdapter
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

class Presenter :FavoriteContract.BasePresenter {
    override lateinit var view: FavoriteContract.BaseView
    override lateinit var viewModel: FavoriteViewModel
    override lateinit var context: Context
    var position: Int =0

    private val compositeDisposable = CompositeDisposable()
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var loading = false
    private var pageNum:Int = 1

    override fun getMyAlcohol() {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(
                GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(position), GlobalApplication.PAGINATION_SIZE,pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.pagingInfo?.page?.let { page->
                    pageNum = page.toInt() +1
                }

                it.data?.pagingInfo?.alcoholTotalCount?.let { total->
                    viewModel.alcoholTypeList[position] = total
                    viewModel.currentPosition.value = position
                }

                it.data?.summary?.alcoholLikeCount?.let {total->
                    if(viewModel.summaryCount.value != total){
                        viewModel.summaryCount.value = total
                    }
                }

                it.data?.alcoholList?.let {list->
                    if(list.isNotEmpty()){
                        view.setAdapter(FavoriteAdapter((list.toMutableList())))
                        initScrollListener()
                    }
                    else{
                        val alcohol = AlcoholList()
                        alcohol.type =-1
                        view.getBinding().favoriteRecyclerView.adapter = FavoriteAdapter(mutableListOf<AlcoholList>().apply {
                            this.add(alcohol)
                        })
                        view.getBinding().favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
                    }
                }

            },{t->
                Log.e(ErrorManager.FAVORITE,t.message.toString())

            }))
    }

    private fun initScrollListener(){
        view.getBinding().favoriteRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = view.getGridLayoutManager().childCount
                totalItemCount =  view.getGridLayoutManager().itemCount
                pastVisibleItem = view.getGridLayoutManager().findFirstVisibleItemPosition()

                if(dy >0){
                    if(!loading){
                        if(visibleItemCount + pastVisibleItem >= totalItemCount){
                            loading =true
                            pagination()
                        }
                    }

                }
            }
        })
    }

    private fun pagination(){
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(
                GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(position), GlobalApplication.PAGINATION_SIZE,pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.pagingInfo?.page?.let { page->
                    pageNum = page.toInt() +1
                }

                it.data?.alcoholList?.let {list->
                    if(list.isNotEmpty()){
                        loading=false
                        view.updateList(list.toMutableList())
                    }
                }

            },{t->
                Log.e(ErrorManager.FAVORITE,t.message.toString())
            }))
    }

    override fun detach() {
        compositeDisposable.dispose()
    }
}