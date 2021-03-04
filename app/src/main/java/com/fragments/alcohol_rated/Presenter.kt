package com.fragments.alcohol_rated

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.alcohol_rated.AlcoholRatedAdapter
import com.application.GlobalApplication
import com.error.ErrorManager
import com.model.rated.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.RatedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :FragmentRated_Contract.FragmentRatedPresenter {
    override var typePosition: Int =0
    override lateinit var activity: Activity
    override val view: FragmentRated_Contract.FragmentRatedView by lazy {
        viewObj!!
    }
    override  var viewObj: FragmentRated_Contract.FragmentRatedView? =null
    override lateinit var viewmodel: RatedViewModel
    private val compositeDisposable = CompositeDisposable()

    private lateinit var alcoholRatedAdapter: AlcoholRatedAdapter
    private val linearLayoutManager:LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }

    //페이징처리를 위한 계산 변수들
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var pageNum:Int =1
    private var loading =false

    override fun initRatedList() {
        try {
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .getMyRatedList(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    GlobalApplication.instance.getRatedType(typePosition),GlobalApplication.PAGINATION_SIZE,pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //다음 페이지의 정보를 읽기 위해서 페이지 값 저장
                    it.data?.pagingInfo?.page?.let {
                        pageNum =it.toInt() +1
                    }

                    it.data?.summary?.let {summary->
                        if(viewmodel.reviewCount.value ==0){
                            viewmodel.reviewCount.value = summary.reviewCount
                        }
                    }

                    it.data?.reviewList?.let { lst->
                        if(lst.isEmpty()){
                            //해당 타입에 평가한 리뷰가 없을 때, 평가한 주류가 없음을 나타내는 화면을 출력
                            lst.toMutableList().let { mlst->
                                mlst.add(ReviewList())
                                setAdapter(mlst)
                            }
                        }
                        else {
                            //해당 타입에 작성한 리뷰가 있다면, 셋팅
                            setAdapter(lst.toMutableList())
                            initScrollListener()
                        }
                    }

                }, { t->
                    Log.e(ErrorManager.MY_RATED_LIST,t.message.toString())}))
        }
        catch (e:RuntimeException){
            Log.e("내가 평가한 주류 조회",e.message.toString())
        }
    }

    private fun setAdapter(list: MutableList<ReviewList>){
        alcoholRatedAdapter = AlcoholRatedAdapter(activity,list,progressbar = settingProgressbar)

        view.getBindingObj().ratedRecyclerView.apply {
            adapter= alcoholRatedAdapter
            setHasFixedSize(false)
            layoutManager = linearLayoutManager

        }
    }

    //페이지네이션 호출을 위해서 유저가 스크롤한 위치값을 계산
    private fun initScrollListener(){
        view.getBindingObj().ratedRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                if(dy >0){
                    if(!loading){
                        if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                            loading = true
                            pagination()
                        }
                    }
                }
            }
        })
    }


    private fun pagination(){
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyRatedList(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                GlobalApplication.instance.getRatedType(typePosition),GlobalApplication.PAGINATION_SIZE,pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //다음 페이지 넘버를 통해 다음 정보를 얻어옴으로 페이지값 저장
                it.data?.pagingInfo?.page?.let {
                    pageNum = it.toInt()+1
                }

                //얻어온 페이지 정보 업데이트
                it.data?.reviewList?.let { lst->
                    if(lst.isNotEmpty()){
                        loading =false
                        alcoholRatedAdapter.updateItem(lst.toMutableList())
                    }
                }
            }, { t-> Log.e(ErrorManager.MY_RATED_LIST,t.message.toString())}))
    }

    val settingProgressbar :(Boolean)->Unit = {bool->
        if(bool){
            view.getBindingObj().progressbar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            view.getBindingObj().progressbar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun detach() {
        viewObj=null
        compositeDisposable.dispose()
    }
}