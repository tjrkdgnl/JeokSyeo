package com.fragment.alcohol_rated

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.alcohol_rated.AlcoholRatedAdapter
import com.application.GlobalApplication
import com.custom.CenterLayoutManager
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.rated.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.viewmodel.RatedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter :FragmentRated_Contract.BasePresenter {
    override var position: Int =0
    override lateinit var activity: Activity
    override lateinit var view: FragmentRated_Contract.BaseView
    override lateinit var smoothScrollListener: Fragment_alcoholRated.SmoothScrollListener
    override lateinit var viewmodel: RatedViewModel

    private val compositeDisposable = CompositeDisposable()
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var pastVisibleItem = 0
    private var pageNum:Int =1
    private var loading =false
    private lateinit var alcoholRatedAdapter: AlcoholRatedAdapter


    override fun initRatedList(context: Context) {
        try {
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .getMyRatedList(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    GlobalApplication.instance.getRatedType(position),GlobalApplication.PAGINATION_SIZE,pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.pagingInfo?.page?.let {
                        pageNum =it.toInt() +1
                    }

                    it.data?.summary?.let {summary->
                        viewmodel.reviewCount.value = summary.reviewCount
                        viewmodel.level.value = summary.level
                    }

                    it.data?.reviewList?.let { lst->
                        if(lst.isEmpty()){
                            lst.toMutableList().let { mlst->
                                mlst.add(ReviewList())
                                alcoholRatedAdapter = AlcoholRatedAdapter(context,mlst,smoothScrollListener,progressbar = settingProgressbar)
                                view.getBinding().ratedRecyclerView.adapter =alcoholRatedAdapter
                            }
                        }
                        else {
                            alcoholRatedAdapter = AlcoholRatedAdapter(context,lst.toMutableList(),smoothScrollListener,progressbar = settingProgressbar)
                            view.getBinding().ratedRecyclerView.adapter =alcoholRatedAdapter
                            initScrollListener()
                        }
                    }
                    view.getBinding().ratedRecyclerView.setHasFixedSize(true)
                    view.getBinding().ratedRecyclerView.layoutManager = CenterLayoutManager(context)
                }, { t->
                    Log.e(ErrorManager.MY_RATED_LIST,t.message.toString())}))
        }
        catch (e:RuntimeException){
            Log.e("내가 평가한 주류 조회",e.message.toString())
        }
    }

    private fun initScrollListener(){
        view.getBinding().ratedRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
                GlobalApplication.instance.getRatedType(position),GlobalApplication.PAGINATION_SIZE,pageNum)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.pagingInfo?.page?.let {
                    pageNum = it.toInt()+1

                }
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
            view.getBinding().progressbar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            view.getBinding().progressbar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}