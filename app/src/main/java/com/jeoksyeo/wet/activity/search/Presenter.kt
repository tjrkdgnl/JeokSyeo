package com.jeoksyeo.wet.activity.search

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
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

    @SuppressLint("SetTextI18n")
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
                        if (lst.isNotEmpty()) {
                            view.getView().textViewRecentSearch.text = "검색 결과"
                            view.getView().textViewRecentSearch.text = "\"${keyword}\" 관련해서 총 ${lst.size}개의 상품을 찾았습니다."
                            view.noSearchItem(false)
                            view.setSearchList(lst.toMutableList())
                            setListener(keyword)
                        } else {
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

        var alcholKeyword =" "

        if(keyword =="") //빈 값일 때는 사용자가 검색어를 모두 지웠을 때다. 따라서 최근 검색어가 표시되어야한다.
        {
            alcholKeyword = "-1"
        }
        else
            alcholKeyword = keyword!!

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getRelativeKeyword(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(), alcholKeyword
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.alcoholList?.let { lst ->

                        if (lst.isNotEmpty()) { //사용자가 검색한 주류를 업데이트
                            view.getView().textViewRecentSearch.text = "연관검색어"
                            view.updateRelativeList(lst.toMutableList(),R.mipmap.relative_search_btn)
                        }
                        else if(alcholKeyword =="-1") {//사용자가 검색어를 입력하지 않았거나, 검색어를 모두 지웠을 때

                            if(GlobalApplication.userDataBase.getKeywordList()?.size !=0){ // 최근 검색어 셋팅
                                GlobalApplication.userDataBase.getKeywordList()?.let { lst->
                                    view.updateRelativeList(lst)
                                    view.getView().textViewRecentSearch.text = "최근검색어"
                                }
                            }
                            else{ //최근 검색어 조차 없을
                                view.updateRelativeList(mutableListOf<String>().apply {
                                    this.add("-1")
                                })
                            }
                        }
                        else{ //연관 검색어가 없을 때
                            view.updateRelativeList(mutableListOf<String>("연관된 검색어가 존재하지 않습니다."),
                            R.mipmap.relative_search_btn)
                        }
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