package com.jeoksyeo.wet.activity.alchol_detail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcholdetail.AlcholComponentAdapter
import com.adapter.alcholdetail.AlcholReviewAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.comment.Comment
import com.model.alchol_detail.Alchol
import com.model.alchol_detail.AlcholComponentData
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException

class Presenter : AlcholDetailContract.BasePresenter {
    override lateinit var view: AlcholDetailContract.BaseView
    override lateinit var context: Context
    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcholComponentData>()

    private val componentList = listOf<String>(
        "ADJUNCT",
        "TEMPERATURE",
        "BARREL AGED",
        "FILTERED",
        "SRM",
        "BODY TO HEAVY",
        "ACIDIC",
        "MALT",
        "HOP",
        "IBU",
        "TANNIN",
        "DRY TO SWEET",
        "POLISHING",
        "CASK TYPE"
    )
    override fun executeLike(alcholId: String) {
        val loginCheck = GlobalApplication.userInfo.getAccessToken() !=null
        var check =JWTUtil.settingUserInfo(false,!loginCheck)

        if(check){
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .alcholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.setLike(true)
                    }, { t -> Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString()) })
            )
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }

    override fun cancelAlcholLike(alcholId: String) {
        val loginCheck = GlobalApplication.userInfo.getAccessToken() !=null
        var check =JWTUtil.settingUserInfo(false,!loginCheck)

        if(check){
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .cancelAlcholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.setLike(false)
                    }, { t -> Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString()) })
            )
        }
        else
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)

    }

    override fun initComponent(context: Context, alchol: Alchol, position: Int) {
        //SRM value에 따른 색 지정하기
        //SRM value가 무조건 정수는 아니기 때문에 try/catch로 parsing에러 잡아서 핸들링하기
       alchol.class_?.firstClass?.code?.let {type->
           when(type){
               "TR"->{ setComponent(alchol, mutableListOf(10, 5, 9, 1, 8, 0, 3, 2))}
               "BE"->{ setComponent(alchol, mutableListOf(0, 1, 6, 3, 2))}
               "WI"->{ setComponent(alchol, mutableListOf(6, 7, 12, 11, 1, 0, 3, 2))}
               "SA"->{ setComponent(alchol, mutableListOf(13, 7, 0, 1, 3, 2))}
               "FO"->{ setComponent(alchol, mutableListOf(8, 0, 1, 2, 14))}
           }
        }

        view.getView().alcholComponentRecyclerView.adapter = AlcholComponentAdapter(settingComponentList)
        view.getView().alcholComponentRecyclerView.setHasFixedSize(true)
        view.getView().alcholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

        view.getView().alcholComponentRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    private fun getComponent(compo: String, alchol: Alchol): String {
        when (compo) {
            "ADJUNCT" -> {
                alchol.adjunct?.let {
                    return it.toString()
                }
            }
            "TEMPERATURE" -> {
                alchol.temperature?.let {
                    return it.toString()
                }
            }
            "BARREL AGED" -> {
                alchol.barrelAged?.let {
                    return it.toString()
                }
            }
            "FILTERED" -> {
                alchol.more?.filtered?.let {
                return it.toString()
            } }
            "SRM" -> {
                alchol.more?.srm?.let {
                    return it.toString()
                }
            }
            "BODY TO HEAVY" -> {
                alchol.more?.body?.let {
                    return it.toString()
                }
            }
            "ACIDIC" -> {
                alchol.more?.acidic?.let {
                    return it.toString()
                }
            }
            "HOP" -> {
                alchol.more?.hop?.let {
                return it.toString()
            }}
            "IBU" -> {
                alchol.more?.ibu?.let {
                return it.toString()
            }}
            "TANNIN" -> {
                alchol.more?.tannin?.let {
                return it.toString()
            }}
            "DRY TO SWEET" -> {
                alchol.more?.sweet?.let {
                return it.toString()
            }}
            "POLISHING" -> {
                alchol.more?.polishing?.let {
                return it.toString()
            }}
            "CASK TYPE" -> {
                alchol.more?.cask_type?.let {
                return it.toString()
            }}
        }
        return ""
    }

    private fun setComponent(alchol: Alchol, component:MutableList<Int>) {
        for (idx in component) {
            if(getComponent(componentList.get(idx),alchol) !=""){
                settingComponentList.add(AlcholComponentData(componentList.get(idx),
                    getComponent(componentList[idx],alchol)))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initReview(context: Context, alcholId: String) {
        val loginCheck = GlobalApplication.userInfo.getAccessToken() !=null
        JWTUtil.settingUserInfo(false,!loginCheck)

            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcholReivew(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        result.data?.reviewList?.let { lst ->
                            if (lst.isEmpty()) {
                                //데이터가 없을 때,
                                lst.toMutableList().let { muLst ->
                                    muLst.add(ReviewList())
                                    view.getView().recyclerViewReviewList.adapter =
                                        AlcholReviewAdapter(context,alcholId,muLst)
                                }
                            } else {
                                lst.let {
                                    view.getView().recyclerViewReviewList.adapter =
                                        AlcholReviewAdapter(context,alcholId,it.toMutableList())
                                }
                            }
                        }
                        view.getView().recyclerViewReviewList.setHasFixedSize(true)
                        view.getView().recyclerViewReviewList.layoutManager =
                            LinearLayoutManager(context)

                        result.data?.reviewInfo?.let {
                            //점수 분포 및 seekbar
                            view.getView().alcholDetailReviewRatingbar.rating = it.scoreAvg!!.toFloat()
                            view.getView().detailTopRatingBar.rating = it.scoreAvg!!.toFloat()
                            view.getView().alcholDetailScoreSeekbar.score1Seekbar.progress = it.score1Count!!
                            view.getView().alcholDetailScoreSeekbar.score2Seekbar.progress = it.score2Count!!
                            view.getView().alcholDetailScoreSeekbar.score3Seekbar.progress = it.score3Count!!
                            view.getView().alcholDetailScoreSeekbar.score4Seekbar.progress = it.score4Count!!
                            view.getView().alcholDetailScoreSeekbar.score5Seekbar.progress = it.score5Count!!
                            view.getView().alcholDetailScoreSeekbar.score5.text = it.score5Count.toString()
                            view.getView().alcholDetailScoreSeekbar.score4.text = it.score4Count.toString()
                            view.getView().alcholDetailScoreSeekbar.score3.text = it.score3Count.toString()
                            view.getView().alcholDetailScoreSeekbar.score2.text = it.score2Count.toString()
                            view.getView().alcholDetailScoreSeekbar.score1.text = it.score1Count.toString()

                            //rating 점수
                            view.getView().detailReviewCountTop.text = it.scoreAvg.toString()
                            view.getView().alcholDetailReviewTotalscore.text = it.scoreAvg.toString()
                            view.getView().detailIcRatringScore.text = it.scoreAvg.toString()

                            //리뷰개수
                            view.getView().detailReviewCountTop.text = it.reviewTotalCount.toString()
                            view.getView().alcholReviewSumCountText.text = it.reviewTotalCount.toString() + "개"
                        }

                    }, { t -> Log.e(ErrorManager.REVIEW, t.message.toString()) })
            )
    }

    override fun expandableText() {
        if (view.getView().textViewAlcholDescription.isExpanded) {
            view.getView().textViewAlcholDescription.collapse()
            view.getView().detailArrow.setImageResource(R.mipmap.down_errow)
            view.getView().detailExpandableText.text = "더보기"
        } else {
            view.getView().textViewAlcholDescription.expand()
            view.getView().detailArrow.setImageResource(R.mipmap.up_errow)
            view.getView().detailExpandableText.text = "접기"
        }
    }

    override fun checkReviewDuplicate(context: Context, alchol: Alchol?) {
        val loginCheck = GlobalApplication.userInfo.getAccessToken() !=null
        var check =JWTUtil.settingUserInfo(false,!loginCheck)

        if(check){
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .checkReviewDuplicate(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alchol?.alcholId!!
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        result.data?.isExist?.let { exist ->
                            if (exist)
                                Toast.makeText(context, "해당 주류에 대한 평가를 이미 하셨습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            else {
                                GlobalApplication.userInfo.getProvider()?.let {
                                    alchol.let { alchol ->
                                        val bundle = Bundle()
                                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, alchol)
                                        GlobalApplication.instance.moveActivity(
                                            context,
                                            Comment::class.java,
                                            0,
                                            bundle,
                                            GlobalApplication.ALCHOL_BUNDLE
                                        )
                                    }
                                } ?: CustomDialog.loginDialog(
                                    context,
                                    GlobalApplication.ACTIVITY_HANDLING_COMMENT
                                )
                            }
                        }
                    }, { t -> Log.e(ErrorManager.REVIEW_DUPLICATE, t.message.toString()) })
            )
        }
       else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }
}