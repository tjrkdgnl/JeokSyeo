package com.jeoksyeo.wet.activity.search

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
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

    private var pageNum = 1

    private var loading = false

    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0

    @SuppressLint("SetTextI18n")
    override fun setSearchResult(keyword: String?) {
        pageNum = 1 //항상 페이지 초기화
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


                    it.data?.pagingInfo?.let { info ->
                        info.page?.let { page ->
                            pageNum = page.toInt() + 1
                        }
                        info.alcoholTotalCount?.let { total ->
                            checkKeywordText(true,keyword!!,total)

                        }
                    }
                    it.data?.alcoholList?.let { lst ->
                        if (lst.isNotEmpty()) {
                            view.getView().editTextSearch.setText(keyword)
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

    @SuppressLint("SetTextI18n")
    private fun checkKeywordText(show:Boolean, keyword:String ="", total:Int =0){
        if(show){
            view.getView().searchKeyword.text ="\"$keyword"
            view.getView().keywordCount.text ="\"관련해서 총 ${total}개의 상품을 찾았습니다."
            view.getView().keywordResult.visibility =View.VISIBLE
            view.getView().textViewRecentSearch.visibility =View.INVISIBLE

        }
        else{
            view.getView().textViewRecentSearch.text = keyword
            view.getView().keywordResult.visibility =View.INVISIBLE
            view.getView().textViewRecentSearch.visibility =View.VISIBLE
        }
    }

    override fun setRelativeSearch(keyword: String) {
        var alcholKeyword = if (keyword.isEmpty()) "-1" else keyword

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
                            checkKeywordText(false,"연관검색어")
                            view.updateRelativeList(
                                lst.toMutableList(),
                                R.mipmap.relative_search_btn
                            )
                        } else if (alcholKeyword == "-1") {//검색어가 없을 때
                            Log.e("alcochol keyword", alcholKeyword)
                            checkKeywordText(false,"최근검색어")

                            GlobalApplication.userDataBase.getKeywordList()?.let { lst ->
                                if (lst.size != 0) {
                                    view.updateRelativeList(lst)
                                }
                            } ?: view.updateRelativeList(mutableListOf<String>().apply {
                                this.add("-1")



                            })
                        } else { //연관 검색어가 없을 때
                            checkKeywordText(false,"연관검색어")
                            view.updateRelativeList(
                                mutableListOf<String>("2"),
                                R.mipmap.relative_search_btn
                            )
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