package com.fragment.favorite

import android.content.Context
import android.util.Log
import android.view.View
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

    private lateinit var favoriteAdapter: FavoriteAdapter

    private val gridLayoutManager by lazy {
        GridLayoutManager(context,2)

    }

    private val setProgressBar: (Boolean) -> Unit ={
        if(it){
            view.getBinding().favoriteProgressbar.root.visibility= View.VISIBLE
        }
        else{
            view.getBinding().favoriteProgressbar.root.visibility= View.INVISIBLE
        }
    }


    override fun getMyAlcohol() {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(
                GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(position), GlobalApplication.PAGINATION_SIZE,pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.pagingInfo?.page?.let { page->
                    pageNum = page.toInt()
                }

                it.data?.pagingInfo?.alcoholTotalCount?.let { total->
                    viewModel.alcoholTypeList[position] = total
                    viewModel.setPosition(position)
                }

                it.data?.summary?.alcoholLikeCount?.let {total->
                    if(viewModel.summaryCount.value != total){
                        viewModel.summaryCount.value = total
                    }
                }

                it.data?.alcoholList?.let {list->
                    if(list.isNotEmpty()){
                        favoriteAdapter = FavoriteAdapter(list.toMutableList(),setProgressBar)
                        view.getBinding().favoriteRecyclerView.adapter = favoriteAdapter
                        view.getBinding().favoriteRecyclerView.addItemDecoration(
                            GridSpacingItemDecoration(
                                2,
                                context.resources.getDimensionPixelSize(R.dimen.grid_layout_margin),
                                true,
                                0
                            )
                        )
                        view.getBinding().favoriteRecyclerView.layoutManager = gridLayoutManager
                        initScrollListener()

                    }
                    else{
                        val alcohol = AlcoholList()
                        alcohol.type =-1
                        view.getBinding().favoriteRecyclerView.adapter = FavoriteAdapter(mutableListOf<AlcoholList>().apply {
                            this.add(alcohol)
                        },setProgressBar)
                        view.getBinding().favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
                    }

                    view.getBinding().favoriteRecyclerView.setHasFixedSize(false)
                }

            },{t->
                Log.e(ErrorManager.FAVORITE,t.message.toString())

            }))
    }

    private fun initScrollListener(){
        view.getBinding().favoriteRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = gridLayoutManager.childCount
                totalItemCount =  gridLayoutManager.itemCount
                pastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition()

                if(dy >0){
                    if(!loading){
                        if((visibleItemCount + pastVisibleItem) >= totalItemCount){
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
                GlobalApplication.instance.getRatedType(position), GlobalApplication.PAGINATION_SIZE,pageNum+1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.pagingInfo?.page?.let { page->
                    pageNum = page.toInt()
                }

                it.data?.alcoholList?.let {list->
                    if(list.isNotEmpty()){
                        loading=false
                        favoriteAdapter.pageUpdate(list.toMutableList())
                        favoriteAdapter.notifyItemChanged(favoriteAdapter.getListSize(), list.size)
                    }
                }

            },{t->
                loading=false
                Log.e(ErrorManager.FAVORITE,t.message.toString())
            }))
    }

    override fun detach() {
        compositeDisposable.dispose()
    }
}