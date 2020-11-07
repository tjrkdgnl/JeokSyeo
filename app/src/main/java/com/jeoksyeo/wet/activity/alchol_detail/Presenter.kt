package com.jeoksyeo.wet.activity.alchol_detail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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

class Presenter : AlcholDetailContract.BasePresenter {
    override lateinit var view: AlcholDetailContract.BaseView
    override lateinit var context: Context
    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcholComponentData>()
    private val NUM_SIZE = 30f
    private val RECYCLERVIEW_TEXT_SIZE = 10f
    private val CHAR_SIZE = 20f
    private val TEMPERATURE_SIZE =25f


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
        val loginCheck = GlobalApplication.userInfo.getAccessToken() != null
        var check = JWTUtil.settingUserInfo(false)

        if (check) {
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
        } else {
            CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }

    override fun cancelAlcholLike(alcholId: String) {
        var check = JWTUtil.settingUserInfo(false)

        if (check) {
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
        } else
            CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)

    }

    override fun initComponent(context: Context, alchol: Alchol, position: Int) {
        //SRM value에 따른 색 지정하기
        //SRM value가 무조건 정수는 아니기 때문에 try/catch로 parsing에러 잡아서 핸들링하기
        alchol.class_?.firstClass?.code?.let { type ->
            when (type) {
                "TR" -> {
                    setComponent(alchol, mutableListOf(10, 5, 9, 1, 8, 0, 3, 2))
                }
                "BE" -> {
                    setComponent(alchol, mutableListOf(0, 1, 6, 3, 2))
                }
                "WI" -> {
                    setComponent(alchol, mutableListOf(6, 7, 12, 11, 1, 0, 3, 2))
                }
                "SA" -> {
                    setComponent(alchol, mutableListOf(13, 7, 0, 1, 3, 2))
                }
                "FO" -> {
                    setComponent(alchol, mutableListOf(8, 0, 1, 2, 14))
                }
            }
        }

        view.getView().alcholComponentRecyclerView.adapter =
            AlcholComponentAdapter(settingComponentList)
        view.getView().alcholComponentRecyclerView.setHasFixedSize(true)
        view.getView().alcholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

        view.getView().alcholComponentRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }


    //성분을 리스트로 주는 홉,몰트,첨가물
    private fun getComponent(compo: String, alchol: Alchol): AlcholComponentData? {
        return when (compo) {
            "MALT" -> {
                alchol.adjunct?.let {
                    AlcholComponentData("MALT","첨가물"
                        ,R.mipmap.malt, mutableListOf(it),RECYCLERVIEW_TEXT_SIZE,GlobalApplication.COMPONENT_RECYCLERVIEW)} }

            "ADJUNCT" -> {
                alchol.adjunct?.let {
                    AlcholComponentData("ADJUNCT","첨가물"
                        ,R.mipmap.adjunct, mutableListOf(it),RECYCLERVIEW_TEXT_SIZE,GlobalApplication.COMPONENT_RECYCLERVIEW)} }
            "TEMPERATURE" -> {
                alchol.temperature?.let {
                    AlcholComponentData("TEMPERATURE","음용 온도"
                        ,R.mipmap.temperature, mutableListOf(it),TEMPERATURE_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "BARREL AGED" -> {
                alchol.barrelAged?.let {
                    AlcholComponentData("BARREL","오크 숙성"
                        ,R.mipmap.barrel, mutableListOf(it.toString().toUpperCase()),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "FILTERED" -> {
                alchol.more?.filtered?.let {
                    AlcholComponentData("FILTERED","여과 여부"
                        ,R.mipmap.filtered, mutableListOf(it.toString().toUpperCase()),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "SRM" -> {
                alchol.more?.srm?.let {
                    AlcholComponentData("SRM",""
                        ,R.mipmap.adjunct, mutableListOf(it),NUM_SIZE,GlobalApplication.COMPONENT_SRM)} }
            "BODY TO HEAVY" -> {
                alchol.more?.body?.let {
                    AlcholComponentData("BODY TO HEAVY","바디"
                        ,R.mipmap.adjunct, mutableListOf(it),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "ACIDIC" -> {
                alchol.more?.acidic?.let {
                    AlcholComponentData("ACIDIC","산도"
                        ,R.mipmap.adjunct, mutableListOf(it),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "HOP" -> {
                alchol.more?.hop?.let {
                    AlcholComponentData("HOP","홉"
                        ,R.mipmap.hop, mutableListOf(it),RECYCLERVIEW_TEXT_SIZE,GlobalApplication.COMPONENT_RECYCLERVIEW)} }
            "IBU" -> {
                alchol.more?.ibu?.let {
                    AlcholComponentData("IBU",""
                        ,R.mipmap.ibu, mutableListOf(it),NUM_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "TANNIN" -> {
                alchol.more?.tannin?.let {
                    AlcholComponentData("TANNIN","타닌"
                        ,R.mipmap.adjunct, mutableListOf(it),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "DRY TO SWEET" -> {
                alchol.more?.sweet?.let {
                    AlcholComponentData("DRY TO SWEET","당도"
                        ,R.mipmap.adjunct, mutableListOf(it),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "POLISHING" -> {
                alchol.more?.polishing?.let {
                    AlcholComponentData("POLISHING","정미율"
                        ,R.mipmap.adjunct, mutableListOf(it),NUM_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }
            "CASK TYPE" -> {
                alchol.more?.cask_type?.let {
                    AlcholComponentData("CASK TYPE","캐스트 종류"
                        ,R.mipmap.adjunct, mutableListOf(it.toString()),CHAR_SIZE,GlobalApplication.COMPONENT_DEFAULT)} }

            else->{  AlcholComponentData("",""
                ,R.mipmap.adjunct, mutableListOf(""),0f,GlobalApplication.COMPONENT_DEFAULT)}
        }
    }

    private fun setComponent(alchol: Alchol, component: MutableList<Int>) {
        for (idx in component) {
            val compo = getComponent(componentList[idx],alchol)
            compo?.contents?.let {lst->
                if(lst.get(0) != ""){
                    settingComponentList.add(compo)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initReview(context: Context, alcholId: String?) {
        JWTUtil.settingUserInfo(false)

        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholReivew(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    alcholId,1)
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
                                    checkMore=GlobalApplication.DETAIL_REVIEW_ITEM_0 })

                                view.getView().recyclerViewReviewList.adapter =
                                    AlcholReviewAdapter(context, alcholId, muLst)
                            }
                        } else {
                            //차트 여부 표시
                            view.getView().radarChart.visibility = View.VISIBLE
                            view.getView().userIndicator.visibility = View.INVISIBLE

                            lst.let {
                                //리뷰 끝에서 더보기가 나오게 하려면 반드시 필요함
                                lst.toMutableList().let {muLst->
                                    muLst.add(ReviewList().apply {
                                        checkMore =GlobalApplication.DETAIL_REVIEW_ITEM_2
                                    })
                                    view.getView().recyclerViewReviewList.adapter =
                                        AlcholReviewAdapter(context, alcholId, muLst)
                                }
                            }
                        }
                        //리뷰개수
                        view.getView().detailReviewCountTop.text = lst.size.toString()
                        view.getView().alcholReviewSumCountText.text = lst.size.toString() + "개"
                    }

                    view.getView().recyclerViewReviewList.setHasFixedSize(true)
                    view.getView().recyclerViewReviewList.layoutManager = LinearLayoutManager(context)

                    result.data?.reviewInfo?.let {
                        //점수 분포 및 seekbar
                        view.getView().alcholDetailReviewRatingbar.rating = it.scoreAvg!!.toFloat()
                        view.getView().alcholDetailScoreSeekbar.score1Seekbar.progress =
                            it.score1Count!!
                        view.getView().alcholDetailScoreSeekbar.score2Seekbar.progress =
                            it.score2Count!!
                        view.getView().alcholDetailScoreSeekbar.score3Seekbar.progress =
                            it.score3Count!!
                        view.getView().alcholDetailScoreSeekbar.score4Seekbar.progress =
                            it.score4Count!!
                        view.getView().alcholDetailScoreSeekbar.score5Seekbar.progress =
                            it.score5Count!!
                        view.getView().alcholDetailScoreSeekbar.score5.text =
                            it.score5Count.toString()
                        view.getView().alcholDetailScoreSeekbar.score4.text =
                            it.score4Count.toString()
                        view.getView().alcholDetailScoreSeekbar.score3.text =
                            it.score3Count.toString()
                        view.getView().alcholDetailScoreSeekbar.score2.text =
                            it.score2Count.toString()
                        view.getView().alcholDetailScoreSeekbar.score1.text =
                            it.score1Count.toString()

                        //rating 점수
                        view.getView().alcholDetailReviewTotalscore.text = it.scoreAvg.toString()
                        view.getView().detailIcRatringScore.text = it.scoreAvg.toString()
                        view.getView().detailIcRatringScore.text = it.scoreAvg.toString()
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
        var check = JWTUtil.settingUserInfo(false)

        Log.e("alchol_Id",alchol?.alcholId.toString())

        if (check) {
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .checkReviewDuplicate(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alchol?.alcholId!!)
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
                            }
                            else {
                                GlobalApplication.userInfo.getProvider()?.let {
                                    alchol.let { alchol ->
                                        val bundle = Bundle()
                                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, alchol)
                                        GlobalApplication.instance.moveActivity(
                                            context, Comment::class.java, 0,
                                            bundle, GlobalApplication.ALCHOL_BUNDLE)
                                    }
                                } ?: CustomDialog.loginDialog(
                                    context,
                                    GlobalApplication.ACTIVITY_HANDLING_COMMENT
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