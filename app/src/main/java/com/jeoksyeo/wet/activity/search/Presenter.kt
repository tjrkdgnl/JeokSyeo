package com.jeoksyeo.wet.activity.search

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter : SearchContract.BasePresenter {

    override lateinit var view: SearchContract.BaseVIew

    override lateinit var layoutManager: LinearLayoutManager

    private val compositeDisposable = CompositeDisposable()

    var pageNum = 1

    private var loading = false

    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0

    override fun setSearchResult(keyword: String?) {
        exeuteProgressBar(true)
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .searchAlcohol(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    keyword,
                    GlobalApplication.PAGINATION_SIZE,
                    "like",
                    pageNum
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    exeuteProgressBar(false)

                    it.data?.pagingInfo?.page?.let { page ->
                        pageNum = page.toInt() + 1
                    }
                    it.data?.alcoholList?.let { lst ->
                        if(lst.isNotEmpty()){
                            view.noSearchItem(false)
                            view.setSearchList(lst.toMutableList())
                            setListener(keyword)
                        }
                        else{
                            view.noSearchItem(true)
                        }
                    }
                },
                    { t ->
                        view.noSearchItem(true)
                        exeuteProgressBar(false)
                        Log.e(ErrorManager.SEARCH, t.message.toString())
                    })
        )
    }

    private fun setListener(keyword: String?) {
        view.getView().recyclerViewSearchlist.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = layoutManager.childCount
                totalItemCount = layoutManager.itemCount
                pastVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (dy > 0) {
                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            loading = true
                            pagination(keyword)
                        }
                    }
                }
            }
        })
    }

    private fun pagination(keyword: String?) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .searchAlcohol(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    keyword,
                    GlobalApplication.PAGINATION_SIZE,
                    "like",
                    pageNum
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.page?.let { page ->
                        pageNum = page.toInt() + 1
                    }
                    it.data?.alcoholList?.let { lst ->
                        if (lst.isNotEmpty()) {
                            loading = false
                            view.updatePaging(lst.toMutableList())
                        }
                    }
                },
                    { t ->
                        Log.e(ErrorManager.SEARCH, t.message.toString())
                    })
        )
    }

    override fun setRelativeSearch(keyword: String?) {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getRelativeKeyword(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(), keyword
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.alcoholList?.let { lst ->
                    view.updateRelativeList(lst.toMutableList())
                }
            }, { t ->
                Log.e(ErrorManager.RELATIVE_SEARCH, t.message.toString())
            })
        )
    }

    override fun detach() {
        compositeDisposable.dispose()
    }

    val exeuteProgressBar: (check: Boolean) -> Unit = { check ->
        if (check) {
            view.getView().progressbar.root.visibility = View.VISIBLE
        } else
            view.getView().progressbar.root.visibility = View.INVISIBLE
    }
}