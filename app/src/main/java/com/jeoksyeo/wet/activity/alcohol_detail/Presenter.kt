package com.jeoksyeo.wet.activity.alcohol_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcoholdetail.AlcoholComponentAdapter
import com.adapter.alcoholdetail.AlcoholReviewAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.comment.Comment
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.AlcoholComponentData
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter : AlcoholDetailContract.BasePresenter {
    override lateinit var view: AlcoholDetailContract.BaseView
    override lateinit var context: Context
    override lateinit var intent: Intent
    var isLike = false
    var alchol: Alcohol? = null

    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcoholComponentData>()
    private val NUM_SIZE = 22f
    private val RECYCLERVIEW_TEXT_SIZE = 10f
    private val CHAR_SIZE = 20f
    private val TEMPERATURE_SIZE = 25f

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
        "CASK TYPE",
        "SAKE_TYPE"
    )

    override fun init() {
        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)

            alchol = bundle?.getParcelable(GlobalApplication.MOVE_ALCHOL)
            alchol?.let { alcholData -> //주류 상세화면으로 넘어왔을 때, alchol에 대한 정보를 번들에서 찾음
                view.getView().alcohol = alcholData

                initComponent(context) //주류 성분 표시 셋팅

                alcholData.isLiked?.let { like ->   //좋아요 여부 확인하여 셋팅
                    view.setLikeImage(like)
                    isLike = like
                }

                alcholData.likeCount?.let { likecount->
                    view.getView().alcoholdetailLikeCount.text = GlobalApplication.instance
                        .checkCount(likecount)
                }

                alcholData.alcoholId?.let { initReview(context) } //리뷰 셋팅
            }
        }
    }

    override fun executeLike() {
        val check = JWTUtil.settingUserInfo(false)

        if (check) {
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .alcoholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alchol!!.alcoholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.setLikeImage(true)
                        view.getView().alcoholdetailLikeCount.text =
                            GlobalApplication.instance.checkCount(
                                view.getView().alcoholdetailLikeCount.text.toString().toInt(), 1
                            )

                    }, { t -> Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString()) })
            )
        } else
            CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
    }

    override fun cancelAlcoholLike() {
        val check = JWTUtil.settingUserInfo(false)

        if (check) {
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .cancelAlcoholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alchol!!.alcoholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.setLikeImage(false)
                        view.getView().alcoholdetailLikeCount.text =
                            GlobalApplication.instance.checkCount(
                                view.getView().alcoholdetailLikeCount.text.toString().toInt(), -1
                            )

                    }, { t -> Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString()) })
            )
        } else
            CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
    }

    override fun initComponent(context: Context) {
        //SRM value에 따른 색 지정하기
        //SRM value가 무조건 정수는 아니기 때문에 try/catch로 parsing에러 잡아서 핸들링하기
        alchol!!.class_?.firstClass?.code?.let { type ->
            when (type) {
                "TR" -> {
                    setComponent(alchol!!, mutableListOf(0, 4, 1, 5, 3, 2))
                }
                "BE" -> {
                    setComponent(alchol!!, mutableListOf(9, 4, 8, 1, 7, 0, 3, 2))
                }
                "WI" -> {
                    setComponent(alchol!!, mutableListOf(5, 4, 6, 11, 10, 1, 0, 3, 2))
                }
                "SA" -> {
                    setComponent(alchol!!, mutableListOf(14, 4, 11, 6, 12, 0, 1, 3, 2))
                }
                "FO" -> {
                    setComponent(alchol!!, mutableListOf(7, 4, 0, 1, 2, 13))
                }
            }
        }

        view.getView().alcoholComponentRecyclerView.adapter =
            AlcoholComponentAdapter(settingComponentList)
        view.getView().alcoholComponentRecyclerView.setHasFixedSize(true)
        view.getView().alcoholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

        view.getView().alcoholComponentRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }


    //성분을 리스트로 주는 홉,몰트,첨가물
    private fun getComponent(compo: String, alcohol: Alcohol): AlcoholComponentData? {
        return when (compo) {
            "MALT" -> {
                alcohol.more?.malt?.let {
                    AlcoholComponentData(
                        "MALT",
                        "몰트"
                        ,
                        R.mipmap.malt,
                        it.toMutableList(),
                        RECYCLERVIEW_TEXT_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }

            "ADJUNCT" -> {
                alcohol.adjunct?.let {
                    AlcoholComponentData(
                        "ADJUNCT",
                        "첨가물"
                        ,
                        R.mipmap.adjunct,
                        it.toMutableList(),
                        RECYCLERVIEW_TEXT_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }
            "TEMPERATURE" -> {
                alcohol.more?.temperature?.let {
                    AlcoholComponentData(
                        "TEMPERATURE",
                        "음용 온도"
                        ,
                        R.mipmap.temperature,
                        it.toMutableList(),
                        TEMPERATURE_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }
            "BARREL AGED" -> {
                alcohol.barrelAged?.let {
                    AlcoholComponentData(
                        "BARREL",
                        "오크 숙성"
                        ,
                        R.mipmap.barrel,
                        it.toString(),
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "FILTERED" -> {
                alcohol.more?.filtered?.let {
                    AlcoholComponentData(
                        "FILTERED", "여과 여부"
                        , R.mipmap.filtered, it.toString(),
                        CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SRM" -> {
                alcohol.more?.srm?.let {
                    AlcoholComponentData(
                        "SRM", ""
                        , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_SRM
                    )
                }
            }
            "BODY TO HEAVY" -> {
                alcohol.more?.body?.let {
                    AlcoholComponentData(
                        "BODY TO HEAVY", "바디"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "ACIDIC" -> {
                alcohol.more?.acidic?.let {
                    AlcoholComponentData(
                        "ACIDIC", "산도"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "HOP" -> {
                alcohol.more?.hop?.let {
                    AlcoholComponentData(
                        "HOP",
                        "홉"
                        ,
                        R.mipmap.hop,
                        it.toMutableList(),
                        RECYCLERVIEW_TEXT_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }
            "IBU" -> {
                alcohol.more?.ibu?.let {
                    AlcoholComponentData(
                        "IBU", ""
                        , R.mipmap.ibu, it.toString(), NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "TANNIN" -> {
                alcohol.more?.tannin?.let {
                    AlcoholComponentData(
                        "TANNIN", "타닌"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "DRY TO SWEET" -> {
                alcohol.more?.sweet?.let {
                    AlcoholComponentData(
                        "DRY TO SWEET", "당도"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "POLISHING" -> {
                alcohol.more?.polishing?.let {
                    AlcoholComponentData(
                        "POLISHING", "정미율"
                        , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "CASK TYPE" -> {
                alcohol.more?.cask?.let {
                    AlcoholComponentData(
                        "CASK TYPE", "캐스트 종류"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }

            "SAKE_TYPE" -> {
                alcohol.more?.sake_type?.let {
                    AlcoholComponentData(
                        "SAKE TYPE", "사케 종류"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }

            else -> {
                AlcoholComponentData(
                    "", ""
                    , 0, "", 0f, GlobalApplication.COMPONENT_DEFAULT
                )
            }
        }
    }

    private fun setComponent(alchol: Alcohol, component: MutableList<Int>) {
        for (idx in component) {
            val compo = getComponent(componentList[idx], alchol)

            val data = compo?.contents

            if (data is String) {
                if (data != "")
                    data.let { settingComponentList.add(compo) }
            } else if (data is List<*>) {
                if (data.size != 0) {
                    if (data[0] != "")
                        settingComponentList.add(compo)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initReview(context: Context) {
        JWTUtil.settingUserInfo(false)

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholReivew(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    alchol!!.alcoholId, 1
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    result.data?.reviewList?.let { lst ->
                        //데이터가 없을 때,
                        if (lst.isEmpty()) {
                            //차트 여부 표시
                            view.getView().userIndicator.visibility = View.VISIBLE

                            lst.toMutableList().let { muLst ->
                                muLst.add(ReviewList().apply {
                                    checkMore = GlobalApplication.DETAIL_NO_REVIEW
                                })

                                view.getView().recyclerViewReviewList.adapter =
                                    AlcoholReviewAdapter(context, alchol!!.alcoholId, muLst)
                            }
                        } else {
                            //차트 여부 표시
                            view.getView().radarChart.visibility = View.VISIBLE
                            view.getView().userIndicator.visibility = View.INVISIBLE

                            lst.let {
                                //리뷰 끝에서 더보기가 나오게 하려면 반드시 필요함
                                lst.toMutableList().let { muLst ->
                                    muLst.add(ReviewList().apply {
                                        checkMore = GlobalApplication.DETAIL_MORE_REVIEW
                                    })
                                    view.getView().recyclerViewReviewList.adapter =
                                        AlcoholReviewAdapter(context, alchol!!.alcoholId, muLst)
                                }
                            }
                        }
                        //리뷰개수
                        view.getView().detailReviewCountTop.text = lst.size.toString()
                        view.getView().alcoholReviewSumCountText.text = lst.size.toString() + "개"
                    }

                    view.getView().recyclerViewReviewList.setHasFixedSize(true)
                    view.getView().recyclerViewReviewList.layoutManager =
                        LinearLayoutManager(context)

                    result.data?.reviewInfo?.let {
                        //점수 분포 및 seekbar
                        view.getView().alcoholDetailReviewRatingbar.rating = it.scoreAvg!!.toFloat()
                        view.getView().alcoholDetailScoreSeekbar.score1Seekbar.progress =
                            it.score1Count!!
                        view.getView().alcoholDetailScoreSeekbar.score2Seekbar.progress =
                            it.score2Count!!
                        view.getView().alcoholDetailScoreSeekbar.score3Seekbar.progress =
                            it.score3Count!!
                        view.getView().alcoholDetailScoreSeekbar.score4Seekbar.progress =
                            it.score4Count!!
                        view.getView().alcoholDetailScoreSeekbar.score5Seekbar.progress =
                            it.score5Count!!
                        view.getView().alcoholDetailScoreSeekbar.score5.text =
                            it.score5Count.toString()
                        view.getView().alcoholDetailScoreSeekbar.score4.text =
                            it.score4Count.toString()
                        view.getView().alcoholDetailScoreSeekbar.score3.text =
                            it.score3Count.toString()
                        view.getView().alcoholDetailScoreSeekbar.score2.text =
                            it.score2Count.toString()
                        view.getView().alcoholDetailScoreSeekbar.score1.text =
                            it.score1Count.toString()

                        //rating 점수
                        view.getView().alcoholDetailReviewTotalscore.text = it.scoreAvg.toString()
                        view.getView().detailIcRatringScore.text = it.scoreAvg.toString()
                        view.getView().detailIcRatringScore.text = it.scoreAvg.toString()
                    }
                }, { t -> Log.e(ErrorManager.REVIEW, t.message.toString()) })
        )
    }

    override fun expandableText() {
        if (view.getView().textViewAlcoholDescription.isExpanded) {
            view.getView().textViewAlcoholDescription.collapse()
            view.getView().detailArrow.setImageResource(R.mipmap.down_errow)
            view.getView().detailExpandableText.text = "더보기"
        } else {
            view.getView().textViewAlcoholDescription.expand()
            view.getView().detailArrow.setImageResource(R.mipmap.up_errow)
            view.getView().detailExpandableText.text = "접기"
        }
    }

    override fun checkReviewDuplicate(context: Context) {
        val check = JWTUtil.settingUserInfo(false)

        if (check) {
            compositeDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .checkReviewDuplicate(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alchol!!.alcoholId!!
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        result.data?.isExist?.let { exist ->
                            if (exist) {
                                Toast.makeText(
                                    context,
                                    "해당 주류에 대한 평가를 이미 하셨습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                //주류 코멘트 화면으로 이동
                                val bundle = Bundle()
                                bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, alchol)
                                GlobalApplication.instance.moveActivity(
                                    context, Comment::class.java, 0,
                                    bundle, GlobalApplication.ALCHOL_BUNDLE
                                )
                            }
                        }
                    }, { t -> Log.e(ErrorManager.REVIEW_DUPLICATE, t.message.toString()) })
            )
        } else {
            CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }
}