package com.activities.alcohol_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.activities.comment.CommentActivity
import com.adapters.alcoholdetail.AlcoholComponentAdapter
import com.adapters.alcoholdetail.AlcoholReviewAdapter
import com.adapters.alcoholdetail.NoAlcoholReviewAdapter
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.GridSpacingItemDecoration
import com.custom.OneClickListener
import com.error.ErrorManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.AlcoholComponentData
import com.model.alcohol_detail.Color
import com.model.alcohol_detail.Srm
import com.model.review.EvaluateIndicator
import com.model.review.GetReviewData
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter : AlcoholDetailContract.AlcoholDetailPresenter {
    //5f로 설정하면 web line이 겉에 하나 더 생기게 되어 6줄이 되므로, 4.9f로 설정하여 최대 5개의 웹라인을 지정
    private val WEB_LINE_MAX = 4.9f

    override var viewObj: AlcoholDetailContract.AlcoholDetailView? = null
    override val view: AlcoholDetailContract.AlcoholDetailView by lazy {
        viewObj!!
    }

    override lateinit var activity: Activity
    override lateinit var alcohol: Alcohol

    var isLike = false
    private var componentAdapter: AlcoholComponentAdapter? = null
    private var toggle = true
    private val typeface by lazy {
        Typeface.createFromAsset(activity.baseContext.assets, "apple_sd_gothic_neo_sb.ttf")
    }

    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcoholComponentData>()
    private val NUM_SIZE = 30f
    private val RECYCLERVIEW_TEXT_SIZE = 10f
    private val CHAR_SIZE = 22f
    private val TEMPERATURE_SIZE = 25f


    private val componentList = listOf(
        "ADJUNCT",
        "TEMPERATURE",
        "BARREL AGED",
        "FILTERED",
        "SRM",
        "BODY",
        "ACIDIC",
        "MALT",
        "HOP",
        "IBU",
        "TANNIN",
        "SWEET",
        "POLISHING",
        "CASK",
        "SAKE_TYPE",
        "GRAPE",
        "RPR",
        "SMV",
        "COLOR",
        "AGED_YEAR"
    )


    @SuppressLint("SetTextI18n")
    override fun init() {
        alcohol.let { alcholData -> //주류 상세화면으로 넘어왔을 때, alchol에 대한 정보를 찾음
            initComponent(activity)

            alcholData.likeCount?.let { //찜한수 체크
                view.getBindingObj().detailAlcoholinfo.alcoholdetailLikeCount.text =
                    GlobalApplication.instance
                        .checkCount(it)
            }

            alcholData.abv?.let {
                view.getBindingObj().detailAlcoholinfo.textViewDosu.text = "${it}%"
            }

            alcholData.viewCount?.let {  //조회수 체크
                Log.e("viewCount", it.toString())
                view.getBindingObj().detailAlcoholinfo.detailEyeCount.text =
                    GlobalApplication.instance.checkCount(it)
            }

            //seekbar를 손으로 조절하지 못하게 막아야함.
            view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.isEnabled =
                false
            view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.isEnabled =
                false
            view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.isEnabled =
                false
            view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.isEnabled =
                false
            view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.isEnabled =
                false
        }
    }

    override fun moveReview() {
        try {
            //첫 번째 리뷰가 보이도록 스크롤을 이동시키기 위해서 부모의 height와 리뷰 리싸이클러뷰의 height을 계산하여 스크롤의 위치를 정합니다.
            val recyclerviewHeight =
                view.getBindingObj().detailReview.recyclerViewReviewList.layoutManager?.height!!
            val nestedScrollViewHeight =
                view.getBindingObj().detailNestedScrollView.getChildAt(0).height

            view.getBindingObj().detailNestedScrollView.smoothScrollTo(
                0,
                (nestedScrollViewHeight - recyclerviewHeight),
                1000
            )
        } catch (e: Exception) {
            Log.e("Calculate Error", e.message.toString())
        }
    }

    override fun CheckLike() {
        CoroutineScope(Dispatchers.IO).launch {
            //모든 api 통신은 엑세스토큰이 유효한지 검사 후 진행
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    //주류를 좋아요한 여부를 얻기위해서 다시 주류 정보를 얻어와야함
                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .getAlcoholDetail(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(),
                                alcohol.alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                it.data?.alcohol?.isLiked?.let { isLike ->
                                    this@Presenter.isLike = isLike
                                    view.setLikeImage(isLike)
                                }
                            }, { t ->
                                CustomDialog.networkErrorDialog(activity)
                                Log.e(ErrorManager.ALCHOL_DETAIL, t.message.toString())
                            })
                    )
                }
            }
        }
    }

    override fun checkScriptLine() {
        view.getBindingObj().detailDescription.textViewAlcoholDescription.post {
            val lineCount =
                view.getBindingObj().detailDescription.textViewAlcoholDescription.lineCount

            if (lineCount <= 5) {
                view.settingExpandableText(false)
            }
        }
    }

    override fun executeLike() {
        view.getBindingObj().detailAlcoholinfo.AlcoholDetailSelectedByMe.setOneClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkAccessToken()

                withContext(Dispatchers.Main) {
                    if (check) {
                        if (!isLike) { //찜하기
                            compositeDisposable.add(
                                ApiGenerator.retrofit.create(ApiService::class.java)
                                    .alcoholLike(
                                        GlobalApplication.userBuilder.createUUID,
                                        GlobalApplication.userInfo.getAccessToken(),
                                        alcohol.alcoholId
                                    )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        view.setLikeImage(true)
                                        //찜한 수 +1
                                        view.getBindingObj().detailAlcoholinfo.alcoholdetailLikeCount.text =
                                            GlobalApplication.instance.checkCount(
                                                view.getBindingObj().detailAlcoholinfo.alcoholdetailLikeCount.text.toString()
                                                    .toInt(), 1
                                            )
                                    }, { t ->
                                        CustomDialog.networkErrorDialog(activity)
                                        Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString())
                                    })
                            )
                        } else { //찜하기 취소
                            compositeDisposable.add(
                                ApiGenerator.retrofit.create(ApiService::class.java)
                                    .cancelAlcoholLike(
                                        GlobalApplication.userBuilder.createUUID,
                                        GlobalApplication.userInfo.getAccessToken(),
                                        alcohol.alcoholId
                                    )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        view.setLikeImage(false)
                                        //찜한 수 -1
                                        view.getBindingObj().detailAlcoholinfo.alcoholdetailLikeCount.text =
                                            GlobalApplication.instance.checkCount(
                                                view.getBindingObj().detailAlcoholinfo.alcoholdetailLikeCount.text.toString()
                                                    .toInt(),
                                                -1
                                            )
                                    }, { t ->
                                        CustomDialog.networkErrorDialog(activity)
                                        Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString())
                                    })
                            )
                        }
                    } else {
                        CustomDialog.loginDialog(
                            activity,
                            GlobalApplication.ACTIVITY_HANDLING_DETAIL, false
                        )
                    }
                }
            }
        }
    }

    override fun initComponent(context: Context) {
        //타입별로 필요한 주류 컴포넌트를 순서를 정해서 리스트에 저장
        alcohol.class_?.firstClass?.code?.let { type ->
            when (type) {
                "TR" -> {
                    setComponent(alcohol, mutableListOf(0, 4, 18, 1, 5, 3, 2))
                }
                "BE" -> {
                    setComponent(alcohol, mutableListOf(9, 4, 8, 1, 7, 0, 3, 2))
                }
                "WI" -> {
                    setComponent(alcohol, mutableListOf(5, 4, 18, 6, 11, 10, 1, 0, 3, 2, 15))
                }
                "SA" -> {
                    setComponent(alcohol, mutableListOf(14, 4, 18, 11, 6, 0, 1, 3, 2, 16, 17))
                }
                "FO" -> {
                    setComponent(alcohol, mutableListOf(7, 4, 18, 0, 1, 19, 5, 2, 13, 3))
                }
            }
        }


        val halfLst = mutableListOf<AlcoholComponentData>()

        if (settingComponentList.size > 4) {
            //보여질 컴포넌트가 4개 이상이라면 '더 보기' 클릭 전에 보여야하는 컴포넌트는 기본적으로 4개로 제한한다.
            for (item in settingComponentList.withIndex()) {
                if (item.index < 4) {
                    halfLst.add(item.value)
                }
            }
            componentAdapter = AlcoholComponentAdapter(halfLst)
        } else {
            view.getBindingObj().detailComponent.componentToggle.visibility = View.GONE
            componentAdapter = AlcoholComponentAdapter(settingComponentList)
        }

        view.getBindingObj().detailComponent.alcoholComponentRecyclerView.adapter = componentAdapter

        //리싸이클러뷰를 접고 펼칠 수 있기 때문에 고정 사이즈 사용하지않음.
        view.getBindingObj().detailComponent.alcoholComponentRecyclerView.setHasFixedSize(false)
        view.getBindingObj().detailComponent.alcoholComponentRecyclerView.layoutManager =
            GridLayoutManager(context, 2)
        view.getBindingObj().detailComponent.alcoholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

    }

    fun commponentToggle() {
        if (toggle) {//펼쳤을 때,
            toggle = false
            view.getBindingObj().detailComponent.componentArrow.setImageResource(R.mipmap.up_errow_orange)
            view.getBindingObj().detailComponent.componentToggleText.text = "주류정보 접기"
            val lst = mutableListOf<AlcoholComponentData>()

            for (idx in 4 until settingComponentList.size) {
                lst.add(settingComponentList[idx])
            }
            componentAdapter?.addComponent(lst)
            componentAdapter?.notifyItemChanged(3, settingComponentList.size)

        } else {//접을 때
            toggle = true
            view.getBindingObj().detailComponent.componentArrow.setImageResource(R.mipmap.down_errow_orange)
            view.getBindingObj().detailComponent.componentToggleText.text = "주류정보 펼치기"
            componentAdapter?.deleteComponent()
        }
    }


    //성분값을 리턴
    private fun getComponent(compo: String, alcohol: Alcohol): AlcoholComponentData? {
        return when (compo) {
            "MALT" -> {
                alcohol.more?.malt?.let {
                    AlcoholComponentData(
                        "Malt",
                        "몰트",
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
                        "Adjunct",
                        "첨가물",
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
                        "Temperature",
                        "음용 온도",
                        R.mipmap.temperature,
                        it.toMutableList(),
                        TEMPERATURE_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }
            "GRAPE" -> {
                alcohol.more?.grape?.let {
                    AlcoholComponentData(
                        "Grape",
                        "포도",
                        R.mipmap.adjunct,
                        it.toMutableList(),
                        TEMPERATURE_SIZE,
                        GlobalApplication.COMPONENT_RECYCLERVIEW
                    )
                }
            }
            "BARREL AGED" -> {
                alcohol.barrelAged?.let {
                    AlcoholComponentData(
                        "Barrel",
                        "오크 숙성",
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
                        "Filtered", "여과 여부", R.mipmap.filtered, it.toString(),
                        CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SRM" -> {
                alcohol.more?.srm?.let {
                    AlcoholComponentData(
                        "SRM", "색", R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_SRM
                    )
                }
            }
            "COLOR" -> {
                alcohol.more?.color?.let {
                    if (it.name != "") {
                        AlcoholComponentData(
                            "Color",
                            "색",
                            R.mipmap.adjunct,
                            it,
                            NUM_SIZE,
                            GlobalApplication.COMPONENT_SRM
                        )
                    } else { //텅 빈값으로 필터링할 때 걸러짐
                        AlcoholComponentData(
                            "", "", 0, "", 0f, GlobalApplication.COMPONENT_DEFAULT
                        )
                    }
                }
            }
            "BODY" -> {
                alcohol.more?.body?.let {
                    AlcoholComponentData(
                        "Body",
                        "바디",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "ACIDIC" -> {
                alcohol.more?.acidity?.let {
                    AlcoholComponentData(
                        "Acidic",
                        "산도",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "HOP" -> {
                alcohol.more?.hop?.let {
                    AlcoholComponentData(
                        "Hop",
                        "홉",
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
                        "IBU",
                        "쓴맛 지표",
                        R.mipmap.ibu,
                        it.toString(),
                        NUM_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "TANNIN" -> {
                alcohol.more?.tannin?.let {
                    AlcoholComponentData(
                        "Tannin",
                        "타닌",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SWEET" -> {
                alcohol.more?.sweet?.let {
                    AlcoholComponentData(
                        "Sweet",
                        "당도",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "CASK" -> {
                alcohol.more?.cask_type?.let {
                    AlcoholComponentData(
                        "Cask Type",
                        "캐스트 종류",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }

            "SAKE_TYPE" -> {
                alcohol.more?.sake_type?.let {
                    AlcoholComponentData(
                        "Sake Type",
                        "사케 종류",
                        R.mipmap.adjunct,
                        it,
                        CHAR_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "RPR" -> {
                alcohol.more?.rpr?.let {
                    AlcoholComponentData(
                        "RPR",
                        "정미율",
                        R.mipmap.adjunct,
                        it,
                        NUM_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SMV" -> {
                alcohol.more?.smv?.let {
                    AlcoholComponentData(
                        "SMV",
                        "당도",
                        R.mipmap.adjunct,
                        it,
                        NUM_SIZE,
                        GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "AGED_YEAR" -> {
                alcohol.more?.aged_year?.let {
                    AlcoholComponentData(
                        "Aged Year", "숙성기간",
                        R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }

            else -> {
                AlcoholComponentData(
                    "", "", 0, "", 0f, GlobalApplication.COMPONENT_DEFAULT
                )
            }
        }
    }

    //서버에서 얻어온 주류의 컴포넌트 중 값이 있는 컴포넌트만을 선별하는 메서드
    private fun setComponent(alchol: Alcohol, component: MutableList<Int>) {
        for (idx in component) {
            val compo = getComponent(componentList[idx], alchol)

            val data = compo?.contents

            if (data is Int) {
                settingComponentList.add(compo)
            } else if (data is Color) {
                settingComponentList.add(compo)
            } else if (data is Float) {
                settingComponentList.add(compo)
            } else if (data is String) { //DEFAULT 형식 셋팅
                settingComponentList.add(compo)
            } else if (data is List<*>) { //LIST 형식 셋팅
                if (data.size != 0) {
                    settingComponentList.add(compo)
                }
            } else if (data is Srm) {//SRM 형식 셋팅
                settingComponentList.add(compo)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initReview(context: Context) {
        compositeDisposable.add(
            ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholReivew(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(),
                    alcohol.alcoholId, 1
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    //차트 확인
                    confirmChart(result)

                    //리뷰 레이팅바 확인
                    confirmReviewRatingBar(result)

                }, { t ->
                    CustomDialog.networkErrorDialog(context)
                    Log.e(ErrorManager.REVIEW, t.message.toString())
                })
        )
    }

    private fun confirmChart(result: GetReviewData) {
        result.data?.reviewList?.let { lst ->
            //데이터가 없을 때,
            if (lst.isEmpty()) {
                //기본 차트 표시
                view.getBindingObj().detailChart.userIndicator.visibility = View.VISIBLE

                //리뷰가 없다는 화면을 출력하기 위해서 'DETAIL_NO_REVIEW' 체크값을 어댑터에 전달
                lst.toMutableList().let { muLst ->
                    muLst.add(ReviewList().apply {
                        checkMore = GlobalApplication.DETAIL_NO_REVIEW
                    })

                    view.getBindingObj().detailReview.recyclerViewReviewList.adapter =
                        NoAlcoholReviewAdapter(muLst)
                    view.getBindingObj().detailReview.recyclerViewReviewList.setHasFixedSize(false)
                    view.getBindingObj().detailReview.recyclerViewReviewList.layoutManager =
                        LinearLayoutManager(activity)
                }
            } else {
                //차트 여부 표시
                result.data?.userAssessment?.let {
                    initRadarChart(it)
                }

                view.getBindingObj().detailChart.radarChart.visibility = View.VISIBLE
                view.getBindingObj().detailChart.userIndicator.visibility = View.INVISIBLE

                //리뷰 끝에서 '더 보기'버튼 여부 확인
                lst.toMutableList().let { muLst ->
                    result.data?.pageInfo?.next?.let { next ->
                        if (next) { //표시되어야하는 리뷰가 더 있는 경우에만 버튼 추가
                            muLst.add(ReviewList().apply {
                                checkMore = GlobalApplication.DETAIL_MORE_REVIEW
                            })
                        }
                    }
                    view.getBindingObj().detailReview.recyclerViewReviewList.adapter =
                        AlcoholReviewAdapter(activity, alcohol.alcoholId, muLst)
                    view.getBindingObj().detailReview.recyclerViewReviewList.setHasFixedSize(false)
                    view.getBindingObj().detailReview.recyclerViewReviewList.layoutManager =
                        LinearLayoutManager(activity)
                }
            }
        }
    }

    private fun confirmReviewRatingBar(result: GetReviewData) {
        result.data?.reviewInfo?.let {
            //점수 분포 및 seekbar
            view.getBindingObj().detailReview.alcoholDetailReviewRatingbar.rating =
                it.scoreAvg!!.toFloat()

            it.reviewTotalCount?.let { total ->
                //리뷰개수
                view.getBindingObj().detailAlcoholinfo.detailReviewCountTop.text =
                    GlobalApplication.instance.checkCount(total)

                //점수 상태 바 셋팅
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.max =
                    total
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.max =
                    total
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.max =
                    total
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.max =
                    total
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.max =
                    total
            }
            //레이팅 프로그래스 및 점수 셋
            it.score1Count?.let { score1 ->
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.progress =
                    score1
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score1.text =
                    score1.toString()
            }
            it.score2Count?.let { score2 ->
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.progress =
                    score2
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score2.text =
                    score2.toString()
            }
            it.score3Count?.let { score3 ->
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.progress =
                    score3
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score3.text =
                    score3.toString()
            }
            it.score4Count?.let { score4 ->
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.progress =
                    score4
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score4.text =
                    score4.toString()
            }
            it.score5Count?.let { score5 ->
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.progress =
                    score5
                view.getBindingObj().detailReview.alcoholDetailScoreSeekbar.score5.text =
                    score5.toString()
            }

            //rating 점수
            view.getBindingObj().detailReview.alcoholDetailReviewTotalscore.text =
                it.scoreAvg.toString()
            view.getBindingObj().detailAlcoholinfo.detailIcRatringScore.text =
                it.scoreAvg.toString()
        }
    }


    override fun checkReviewDuplicate(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    view.settingProgressBar(true)
                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .checkReviewDuplicate(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(),
                                alcohol.alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result ->
                                view.settingProgressBar(false)

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

                                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, alcohol)

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            val intent =
                                                Intent(context, CommentActivity::class.java)
                                            intent.putExtra(GlobalApplication.ALCHOL_BUNDLE, bundle)
                                            val pair = Pair.create(
                                                view.getBindingObj().detailMainImg as View,
                                                view.getBindingObj().detailMainImg.transitionName
                                            )

                                            val optionCompat =
                                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                                    (context as Activity), pair
                                                )

                                            context.startActivity(intent, optionCompat.toBundle())
                                        } else {
                                            GlobalApplication.instance.moveActivity(
                                                context, CommentActivity::class.java, 0,
                                                bundle, GlobalApplication.ALCHOL_BUNDLE
                                            )
                                        }
                                    }
                                }
                            }, { t ->
                                CustomDialog.networkErrorDialog(context)
                                view.settingProgressBar(false)
                                Log.e(ErrorManager.REVIEW_DUPLICATE, t.message.toString())
                            })
                    )
                } else {
                    CustomDialog.loginDialog(
                        context,
                        GlobalApplication.ACTIVITY_HANDLING_DETAIL,
                        false
                    )
                }
            }
        }
    }


    override fun initRadarChart(review: EvaluateIndicator) {
        view.getBindingObj().detailChart.radarChart.scaleX = 1.27f
        view.getBindingObj().detailChart.radarChart.scaleY = 1.27f

        view.getBindingObj().detailChart.radarChart.isRotationEnabled = false //차트 회전
        view.getBindingObj().detailChart.radarChart.description.isEnabled = false // 범례 값 설명
        view.getBindingObj().detailChart.radarChart.legend.isEnabled = false //범례 값
        view.getBindingObj().detailChart.radarChart.webLineWidth = 0f //대각선 두께
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 대각선 색
            view.getBindingObj().detailChart.radarChart.webColor =
                activity.resources.getColor(R.color.white, null)
        } else {
            view.getBindingObj().detailChart.radarChart.webColor =
                ContextCompat.getColor(activity, R.color.white)
        }
        view.getBindingObj().detailChart.radarChart.webLineWidthInner = 0.75f //내부선 두께
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //내부선 색
            view.getBindingObj().detailChart.radarChart.webColorInner =
                activity.resources.getColor(R.color.light_grey4, null)
        } else {
            view.getBindingObj().detailChart.radarChart.webColorInner =
                ContextCompat.getColor(activity, R.color.light_grey4)
        }

        view.getBindingObj().detailChart.radarChart.webAlpha =
            200 //내부선 투명도 , 255 - opaque , 0 - transparent

        val xAxis = view.getBindingObj().detailChart.radarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.typeface = typeface
        xAxis.textSize = 11f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            private val name = listOf("아로마", "마우스필", "어울림", "시각적특징", "테이스트")

            override fun getFormattedValue(value: Float): String {
                return name[value.toInt() % name.size]
            }
        }

        val yAxis = view.getBindingObj().detailChart.radarChart.yAxis
        yAxis.setLabelCount(5, false)
        yAxis.xOffset = 0f
        yAxis.yOffset = 0f
        yAxis.axisMinimum = 0f// 최소 웹 라인의 개수
        yAxis.axisMaximum = WEB_LINE_MAX //최대 웹 라인의 개수 지정
        yAxis.setDrawLabels(false) //수직으로 표시되는 수치값
        setRadarChartData(review)
    }

    private fun setRadarChartData(review: EvaluateIndicator) {
        val DataSetList = mutableListOf<IRadarDataSet>()
        val backgroundEntry = mutableListOf<RadarEntry>()
        val dataEntry = mutableListOf<RadarEntry>()



        review.let { info ->
            //차트 회색 배경을 만들기 위해서 full value로 된 entry 생성
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))

            //실제 유저들이 평가한 종합 점수 entry 생성
            info.aroma?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.mouthfeel?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.overall?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.appearance?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }

            info.taste?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
        }

        //dataSet으로 만들기
        val backgroundSet = RadarDataSet(backgroundEntry, "backgroundColor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//데이터 셋 바깥 line color
            backgroundSet.color = activity.resources.getColor(R.color.light_grey3, null)
        } else {
            backgroundSet.color = ContextCompat.getColor(activity, R.color.light_grey3)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 데이터 셋 내부 color
            backgroundSet.fillColor = activity.resources.getColor(R.color.light_grey3, null)
        } else {
            backgroundSet.fillColor = ContextCompat.getColor(activity, R.color.light_grey3)
        }

        //dataSet으로 만들기
        val dataSet = RadarDataSet(dataEntry, "data")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.color = activity.resources.getColor(R.color.orange, null)
        } else {
            dataSet.color = ContextCompat.getColor(activity, R.color.orange)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.fillColor = activity.resources.getColor(R.color.orange, null)
        } else {
            dataSet.fillColor = ContextCompat.getColor(activity, R.color.orange)
        }

        backgroundSet.setDrawFilled(true) //도형 내부 색칠여부
        backgroundSet.fillAlpha = 200 //도형 알파값
        backgroundSet.isDrawHighlightCircleEnabled = true
        backgroundSet.setDrawHighlightIndicators(false)

        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 200
        dataSet.isDrawHighlightCircleEnabled = true
        dataSet.setDrawHighlightIndicators(false)

        //set list에 저장
        DataSetList.add(backgroundSet)
        DataSetList.add(dataSet)

        val data = RadarData(DataSetList)
        data.setDrawValues(false)
        view.getBindingObj().detailChart.radarChart.data = data
        view.getBindingObj().detailChart.radarChart.invalidate()
    }

    //서버 과부하를 막기위해서 연속으로 클릭되어 api 호출이 되지 않도록 확장함수 생성
    private fun View.setOneClickListener(onClick: (View) -> Unit) {
        val oneClick = OneClickListener {
            onClick(it)
        }
        setOnClickListener(oneClick)

    }

    override fun detach() {
        //subscribe가 끝난 객체들을 메모리에서 할당해제
        compositeDisposable.dispose()
        viewObj = null
    }
}