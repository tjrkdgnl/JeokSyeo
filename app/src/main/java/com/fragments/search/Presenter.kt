package com.fragments.search

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.search.SearchAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter : SearchContract.BasePresenter {

    override lateinit var activity: Activity
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
        pageNum = 1 //항상 첫 페이지의 값부터 검색결과를 가져오기 위해서 초기화를 진행한다.
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

                    //다음 페이지를 얻기 위해서 페이지 정보를 저장.
                    it.data?.pagingInfo?.let { info ->
                        info.page?.let { page ->
                            pageNum = page.toInt() + 1
                        }
                        info.alcoholTotalCount?.let { total ->
                            view.checkKeywordListCount(true,keyword!!,total)

                        }
                    }
                    it.data?.alcoholList?.let { lst ->
                        if (lst.isNotEmpty()) {
                            //검색창에 키워드 값을 셋팅함으로써 textWatcher리스너를 호출하기 위함
                            view.getViewBinding().editTextSearch.setText(keyword)

                            view.checkSearchItem(false,keyword)

                            view.setSearchList(lst.toMutableList())
                            setListener(keyword)
                        } else {
                            view.checkSearchItem(true,keyword)
                        }
                    }
                },
                    { t ->
                        CustomDialog.networkErrorDialog(activity)
                        view.checkSearchItem(true,keyword)
                        exeuteProgressBar(false)
                        Log.e(ErrorManager.SEARCH, t.message.toString())
                    })
        )
    }

    //페이징을 위해서 리싸이클러뷰에 스크롤리스너 셋팅
    private fun setListener(keyword: String?) {
        view.getViewBinding().recyclerViewSearchlist.addOnScrollListener(object :
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
                    //다음 번 페이지를 얻어오기 위해서 페이지 넘버 셋팅
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



    override fun setRelativeSearch(keyword: String) {
        var alcholKeyword = if (keyword.isEmpty()) "${SearchAdapter.NO_SEARCH}" else keyword

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getRelativeKeyword(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(), alcholKeyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.alcoholList?.let { lst ->
                        when {
                            lst.isNotEmpty() -> { //사용자가 검색한 주류를 업데이트
                                //검색 목록의 주제 설정
                                view.checkKeywordListCount(false,"연관 검색어")
                                view.updateRelativeList(
                                    lst.toMutableList(),
                                    R.mipmap.relative_search_btn)

                            }
                            alcholKeyword == SearchAdapter.NO_SEARCH.toString() -> { //검색어가 없을 때
                                //검색 목록의 주제 설정
                                view.checkKeywordListCount(false,"최근 검색어")

                                //검색어 리스트 유무에 따라서 화면 설정
                                GlobalApplication.userDataBase.getKeywordList()?.let { lst ->
                                    if (lst.size != 0) {
                                        view.updateRelativeList(lst)
                                    }
                                } ?: view.updateRelativeList(mutableListOf<String>().apply {
                                    this.add("${SearchAdapter.NO_SEARCH}")
                                })
                            }
                            else -> { //연관된 검색어가 없을 때
                                view.checkKeywordListCount(false,"연관 검색어")
                                view.updateRelativeList(
                                    mutableListOf("${SearchAdapter.NO_RELATIVE}"),
                                    R.mipmap.relative_search_btn)
                            }
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
            view.getViewBinding().progressbar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else{
            view.getViewBinding().progressbar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}