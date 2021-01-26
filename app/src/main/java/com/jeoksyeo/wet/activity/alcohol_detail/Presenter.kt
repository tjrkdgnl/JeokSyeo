package com.jeoksyeo.wet.activity.alcohol_detail

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
import com.adapter.alcoholdetail.AlcoholComponentAdapter
import com.adapter.alcoholdetail.AlcoholReviewAdapter
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
import com.jeoksyeo.wet.activity.comment.CommentActivity
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.AlcoholComponentData
import com.model.alcohol_detail.Color
import com.model.alcohol_detail.Srm
import com.model.review.EvaluateIndicator
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class Presenter : AlcoholDetailContract.BasePresenter {
    //5f로 설정하면 web line이 겉에 하나 더 생기게 되어 6줄이 되므로, 4.9f로 설정하여 최대 5개의 웹라인을 지정
    private val WEB_LINE_MAX = 4.9f
    override lateinit var view: AlcoholDetailContract.BaseView
    override lateinit var context: Context
    override lateinit var intent: Intent
    var isLike = false
    lateinit var networkUtil: NetworkUtil
    override lateinit var alcohol: Alcohol
    private var componentAdapter: AlcoholComponentAdapter? = null
    private var toggle = true
    private val typeface by lazy {
        Typeface.createFromAsset(context.assets, "apple_sd_gothic_neo_sb.ttf")
    }

    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcoholComponentData>()
    private val NUM_SIZE = 30f
    private val RECYCLERVIEW_TEXT_SIZE = 10f
    private val CHAR_SIZE = 22f
    private val TEMPERATURE_SIZE = 25f


    private val componentList = listOf<String>(
        "ADJUNCT", //0
        "TEMPERATURE",
        "BARREL AGED",
        "FILTERED",
        "SRM",
        "BODY",//5
        "ACIDIC",
        "MALT",
        "HOP",
        "IBU",
        "TANNIN",//10
        "SWEET",
        "POLISHING",
        "CASK",
        "SAKE_TYPE",
        "GRAPE",//15
        "RPR",
        "SMV",
        "COLOR",
        "AGED_YEAR"
    )

    override fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(context)
            networkUtil.register()
        }
    }

    override fun init() {
        alcohol.let { alcholData -> //주류 상세화면으로 넘어왔을 때, alchol에 대한 정보를 번들에서 찾음
            initComponent(context)

            alcholData.likeCount?.let { //찜한수 체크
                view.getView().detailAlcoholinfo.alcoholdetailLikeCount.text = GlobalApplication.instance
                    .checkCount(it)
            }

            alcholData.isLiked?.let { isLike ->
                this.isLike = isLike
                view.setLikeImage(isLike)
            }

            alcholData.viewCount?.let {  //조회수 체크
                Log.e("viewCount", it.toString())
                view.getView().detailAlcoholinfo.detailEyeCount.text = GlobalApplication.instance.checkCount(it)
            }

            //seekbar를 손으로 조절하지 못하게 막아야함.
            view.getView().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.isEnabled = false
            view.getView().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.isEnabled = false
            view.getView().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.isEnabled = false
            view.getView().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.isEnabled = false
            view.getView().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.isEnabled = false
        }
    }

    override fun moveReview() {

        try {
            val recyclerviewHeight= view.getView().detailReview.recyclerViewReviewList.layoutManager?.height!!
            val nestedScrollViewHeight = view.getView().detailNestedScrollView.getChildAt(0).height

            view.getView().detailNestedScrollView.smoothScrollTo(0,(nestedScrollViewHeight-recyclerviewHeight),1000)
        }
        catch (e:Exception){
            Log.e("Calculate Error",e.message.toString())
        }
    }

    override fun refreshIsLike() {
        GlobalApplication.userInfo.getProvider()?.let {
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
                            this.isLike = isLike
                            view.setLikeImage(isLike)
                        }
                    }, { t ->
                        CustomDialog.networkErrorDialog(context)
                        Log.e(ErrorManager.ALCHOL_DETAIL, t.message.toString())
                    })
            )
        }
    }

    override fun checkCountLine() {
        view.getView().detailDescription.textViewAlcoholDescription.post(Runnable {
            val lineCount = view.getView().detailDescription.textViewAlcoholDescription.lineCount

            if (lineCount <= 5) {
                view.settingExpandableText(false)
            }
        })
    }

    override fun executeLike() {
        view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe.setOneClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val check = JWTUtil.checkToken()
                Log.e("check", check.toString())

                withContext(Dispatchers.Main) {
                    if (check) {
                        if (!isLike) {
                            settingLikeButtonEnabled(
                                view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe,
                                false
                            )
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
                                        settingLikeButtonEnabled(
                                            view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe,
                                            true
                                        )

                                        view.setLikeImage(true)
                                        view.getView().detailAlcoholinfo.alcoholdetailLikeCount.text =
                                            GlobalApplication.instance.checkCount(
                                                view.getView().detailAlcoholinfo.alcoholdetailLikeCount.text.toString()
                                                    .toInt(), 1
                                            )
                                    }, { t ->
                                        CustomDialog.networkErrorDialog(context)
                                        settingLikeButtonEnabled(view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe, true)
                                        Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString())
                                    })
                            )
                        } else {
                            settingLikeButtonEnabled(
                                view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe,
                                false
                            )
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
                                        settingLikeButtonEnabled(
                                            view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe,
                                            true
                                        )
                                        view.setLikeImage(false)
                                        view.getView().detailAlcoholinfo.alcoholdetailLikeCount.text =
                                            GlobalApplication.instance.checkCount(
                                                view.getView().detailAlcoholinfo.alcoholdetailLikeCount.text.toString()
                                                    .toInt(),
                                                -1
                                            )
                                    }, { t ->
                                        CustomDialog.networkErrorDialog(context)
                                        settingLikeButtonEnabled(view.getView().detailAlcoholinfo.AlcoholDetailSelectedByMe, true)
                                        Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString())
                                    })
                            )
                        }
                    } else {
                        CustomDialog.loginDialog(
                            context,
                            GlobalApplication.ACTIVITY_HANDLING_DETAIL
                        )
                    }
                }
            }
        }
    }

    private fun settingLikeButtonEnabled(view: View, setting: Boolean) {
        view.isEnabled = setting
    }

    override fun initComponent(context: Context) {
        //SRM value에 따른 색 지정하기
        //SRM value가 무조건 정수는 아니기 때문에 try/catch로 parsing에러 잡아서 핸들링하기
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

        if (settingComponentList.size > 4) {//컴포넌트가 4개만 표시
            for (item in settingComponentList.withIndex()) {
                if (item.index < 4) {
                    halfLst.add(item.value)
                }
            }
            componentAdapter = AlcoholComponentAdapter(halfLst)
        } else {
            view.getView().detailComponent.componentToggle.visibility = View.GONE
            componentAdapter = AlcoholComponentAdapter(settingComponentList)
        }

        view.getView().detailComponent.alcoholComponentRecyclerView.adapter = componentAdapter

        //리싸이클러뷰를 접고 펼칠 수 있기 때문에 고정 사이즈 사용하지않음.
        view.getView().detailComponent.alcoholComponentRecyclerView.setHasFixedSize(false)
        view.getView().detailComponent.alcoholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

        view.getView().detailComponent.alcoholComponentRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    fun commponentToggle() {
        if (toggle) {//펼쳤을 때,
            toggle = false
            view.getView().detailComponent.componentArrow.setImageResource(R.mipmap.up_errow_orange)
            view.getView().detailComponent.componentToggleText.text = "주류정보 접기"
            val lst = mutableListOf<AlcoholComponentData>()

            for (idx in 4 until settingComponentList.size) {
                lst.add(settingComponentList[idx])
            }
            componentAdapter?.addComponent(lst)
            componentAdapter?.notifyItemChanged(3, settingComponentList.size)

        } else {//접을 때
            toggle = true
            view.getView().detailComponent.componentArrow.setImageResource(R.mipmap.down_errow_orange)
            view.getView().detailComponent.componentToggleText.text = "주류정보 펼치기"
            componentAdapter?.deleteComponent()
        }
    }


    //성분을 리스트로 주는 홉,몰트,첨가물
    private fun getComponent(compo: String, alcohol: Alcohol): AlcoholComponentData? {
        return when (compo) {
            "MALT" -> {
                alcohol.more?.malt?.let {
                    AlcoholComponentData(
                        "Malt",
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
                        "Adjunct",
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
                        "Filtered", "여과 여부"
                        , R.mipmap.filtered, it.toString(),
                        CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SRM" -> {
                alcohol.more?.srm?.let {
                    AlcoholComponentData(
                        "SRM", "색"
                        , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_SRM
                    )
                }
            }
            "COLOR" -> {
                alcohol.more?.color?.let {
                    if (it.name != "") {
                        AlcoholComponentData(
                            "Color", "색"
                            , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_SRM
                        )
                    } else { //텅 빈값으로 필터링할 때 걸러짐
                        AlcoholComponentData(
                            "", ""
                            , 0, "", 0f, GlobalApplication.COMPONENT_DEFAULT
                        )
                    }
                }
            }
            "BODY" -> {
                alcohol.more?.body?.let {
                    AlcoholComponentData(
                        "Body", "바디"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "ACIDIC" -> {
                alcohol.more?.acidity?.let {
                    AlcoholComponentData(
                        "Acidic", "산도"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "HOP" -> {
                alcohol.more?.hop?.let {
                    AlcoholComponentData(
                        "Hop",
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
                        "IBU", "쓴맛 지표"
                        , R.mipmap.ibu, it.toString(), NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "TANNIN" -> {
                alcohol.more?.tannin?.let {
                    AlcoholComponentData(
                        "Tannin", "타닌"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SWEET" -> {
                alcohol.more?.sweet?.let {
                    AlcoholComponentData(
                        "Sweet", "당도"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "CASK" -> {
                alcohol.more?.cask_type?.let {
                    AlcoholComponentData(
                        "Cask Type", "캐스트 종류"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }

            "SAKE_TYPE" -> {
                alcohol.more?.sake_type?.let {
                    AlcoholComponentData(
                        "Sake Type", "사케 종류"
                        , R.mipmap.adjunct, it, CHAR_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "RPR" -> {
                alcohol.more?.rpr?.let {
                    AlcoholComponentData(
                        "RPR", "정미율"
                        , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
                    )
                }
            }
            "SMV" -> {
                alcohol.more?.smv?.let {
                    AlcoholComponentData(
                        "SMV", "당도"
                        , R.mipmap.adjunct, it, NUM_SIZE, GlobalApplication.COMPONENT_DEFAULT
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

            if (data is Int) {
                data.let { settingComponentList.add(compo) }
            } else if (data is Color) {
                data.let { settingComponentList.add(compo) }
            } else if (data is Float) {
                data.let { settingComponentList.add(compo) }
            } else if (data is String) { //DEFAULT 형식 셋팅
                if (data != "")
                    data.let { settingComponentList.add(compo) }
            } else if (data is List<*>) { //LIST 형식 셋팅
                if (data.size != 0) {
                    if (data[0] != "")
                        settingComponentList.add(compo)
                }
            } else if (data is Srm) {//SRM 형식 셋팅
                settingComponentList.add(compo)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initReview(context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            JWTUtil.checkToken()

            withContext(Dispatchers.Main) {
                compositeDisposable.add(
                    ApiGenerator.retrofit.create(ApiService::class.java)
                        .getAlcoholReivew(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken(),
                            alcohol.alcoholId, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            result.data?.reviewList?.let { lst ->
                                //데이터가 없을 때,
                                if (lst.isEmpty()) {
                                    //차트 여부 표시
                                    view.getView().detailChart.userIndicator.visibility = View.VISIBLE

                                    lst.toMutableList().let { muLst ->
                                        muLst.add(ReviewList().apply {
                                            checkMore = GlobalApplication.DETAIL_NO_REVIEW
                                        })

                                        view.getView().detailReview.recyclerViewReviewList.adapter =
                                            AlcoholReviewAdapter(context, alcohol.alcoholId, muLst)
                                    }
                                } else {
                                    //차트 여부 표시
                                    result.data?.userAssessment?.let {
                                        initRadarChart(it)
                                    }

                                    view.getView().detailChart.radarChart.visibility = View.VISIBLE
                                    view.getView().detailChart.userIndicator.visibility = View.INVISIBLE

                                    //리뷰 끝에서 더보기가 나오게 하려면 반드시 필요함
                                    lst.toMutableList().let { muLst ->
                                        result.data?.pageInfo?.next?.let { next ->
                                            if (next) {
                                                muLst.add(ReviewList().apply {
                                                    checkMore = GlobalApplication.DETAIL_MORE_REVIEW
                                                })
                                            }
                                        }
                                        view.getView().detailReview.recyclerViewReviewList.adapter =
                                            AlcoholReviewAdapter(context, alcohol.alcoholId, muLst)
                                    }
                                }

                                view.getView().detailReview.recyclerViewReviewList.setHasFixedSize(false)
                                view.getView().detailReview.recyclerViewReviewList.layoutManager = LinearLayoutManager(context)
                            }

                            result.data?.reviewInfo?.let {
                                //점수 분포 및 seekbar
                                view.getView().detailReview.alcoholDetailReviewRatingbar.rating =
                                    it.scoreAvg!!.toFloat()

                                it.reviewTotalCount?.let { total ->
                                    //리뷰개수
                                    view.getView().detailAlcoholinfo.detailReviewCountTop.text =
                                        GlobalApplication.instance.checkCount(total)

                                    //점수 상태 바 셋팅
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.max =
                                        total
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.max =
                                        total
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.max =
                                        total
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.max =
                                        total
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.max =
                                        total
                                }
                                it.score1Count?.let { score1 ->
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score1Seekbar.progress =
                                        score1
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score1.text =
                                        score1.toString()
                                }
                                it.score2Count?.let { score2 ->
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score2Seekbar.progress =
                                        score2
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score2.text =
                                        score2.toString()
                                }
                                it.score3Count?.let { score3 ->
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score3Seekbar.progress =
                                        score3
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score3.text =
                                        score3.toString()
                                }
                                it.score4Count?.let { score4 ->
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score4Seekbar.progress =
                                        score4
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score4.text =
                                        score4.toString()
                                }
                                it.score5Count?.let { score5 ->
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score5Seekbar.progress =
                                        score5
                                    view.getView().detailReview.alcoholDetailScoreSeekbar.score5.text =
                                        score5.toString()
                                }

                                //rating 점수
                                view.getView().detailReview.alcoholDetailReviewTotalscore.text =
                                    it.scoreAvg.toString()
                                view.getView().detailAlcoholinfo.detailIcRatringScore.text = it.scoreAvg.toString()
                            }
                        }, { t ->
                            CustomDialog.networkErrorDialog(context)
                            Log.e(ErrorManager.REVIEW, t.message.toString()) })
                )
            }
        }
    }

    override fun expandableText() {
        if (view.getView().detailDescription.textViewAlcoholDescription.isExpanded) {
            view.getView().detailDescription.textViewAlcoholDescription.collapse()
            view.getView().detailDescription.detailArrow.setImageResource(R.mipmap.down_errow)
            view.getView().detailDescription.detailExpandableText.text = "더보기"
        } else {
            view.getView().detailDescription.textViewAlcoholDescription.expand()
            view.getView().detailDescription.detailArrow.setImageResource(R.mipmap.up_errow)
            view.getView().detailDescription.detailExpandableText.text = "접기"
        }
    }

    override fun checkReviewDuplicate(context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkToken()

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

                                        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                                            val intent = Intent(context,CommentActivity::class.java)
                                            intent.putExtra(GlobalApplication.ALCHOL_BUNDLE,bundle)
                                            val pair = Pair.create(
                                                view.getView().detailMainImg as View, view.getView().detailMainImg.transitionName)

                                            val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                                (context as Activity), pair)

                                            context.startActivity(intent, optionCompat.toBundle())
                                        }
                                        else{
                                            GlobalApplication.instance.moveActivity(
                                                context, CommentActivity::class.java, 0,
                                                bundle, GlobalApplication.ALCHOL_BUNDLE)
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
                    CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }


    override fun initRadarChart(review: EvaluateIndicator) {
        view.getView().detailChart.radarChart.scaleX = 1.27f
        view.getView().detailChart.radarChart.scaleY = 1.27f

        view.getView().detailChart.radarChart.isRotationEnabled = false //차트 회전
        view.getView().detailChart.radarChart.description.isEnabled = false // 범례 값 설명
        view.getView().detailChart.radarChart.legend.isEnabled = false //범례 값
        view.getView().detailChart.radarChart.webLineWidth = 0f //대각선 두께
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 대각선 색
            view.getView().detailChart.radarChart.webColor =
                context.resources.getColor(R.color.white, null)
        } else{
            view.getView().detailChart.radarChart.webColor =
                ContextCompat.getColor(context,R.color.white)
        }
        view.getView().detailChart.radarChart.webLineWidthInner = 0.75f //내부선 두께
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //내부선 색
            view.getView().detailChart.radarChart.webColorInner =
                context.resources.getColor(R.color.light_grey4, null)
        }else{
            view.getView().detailChart.radarChart.webColorInner =
                ContextCompat.getColor(context,R.color.light_grey4)
        }

        view.getView().detailChart.radarChart.webAlpha = 200 //내부선 투명도 , 255 - opaque , 0 - transparent

        val xAxis = view.getView().detailChart.radarChart.xAxis
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

        val yAxis = view.getView().detailChart.radarChart.yAxis
        yAxis.setLabelCount(5, false)
        yAxis.xOffset = 0f
        yAxis.yOffset = 0f
        yAxis.axisMinimum = 0f// 최소 웹 라인의 개수
        yAxis.axisMaximum = WEB_LINE_MAX //최대 웹 라인의 개수 지정
        yAxis.setDrawLabels(false) //수직으로 표시되는 수치값
        setRadarChartData(review)
    }

    private fun setRadarChartData(review: EvaluateIndicator) {
        val chartDataSetList = mutableListOf<IRadarDataSet>()
        val backgroundEntry = mutableListOf<RadarEntry>()
        val dataEntry = mutableListOf<RadarEntry>()


        review.let { info ->
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))

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

        val bacgroundSet = RadarDataSet(backgroundEntry, "backgroundColor")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//데이터 셋 바깥 line color
            bacgroundSet.color =
                context.resources.getColor(R.color.light_grey3, null)
        } else{
            bacgroundSet.color =
                ContextCompat.getColor(context,R.color.light_grey3)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 데이터 셋 내부 color
            bacgroundSet.fillColor =
                context.resources.getColor(R.color.light_grey3, null)
        } else{
            bacgroundSet.fillColor =
               ContextCompat.getColor(context,R.color.light_grey3)
        }

        bacgroundSet.setDrawFilled(true)
        bacgroundSet.fillAlpha = 200
        bacgroundSet.isDrawHighlightCircleEnabled = true
        bacgroundSet.setDrawHighlightIndicators(false)

        val dataSet = RadarDataSet(dataEntry, "data")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.color = context.resources.getColor(R.color.orange, null)
        }else{
            dataSet.color = ContextCompat.getColor(context,R.color.orange)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dataSet.fillColor = context.resources.getColor(R.color.orange, null)
        }else{
            dataSet.fillColor =ContextCompat.getColor(context,R.color.orange)
        }

        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 200
        dataSet.isDrawHighlightCircleEnabled = true
        dataSet.setDrawHighlightIndicators(false)


        chartDataSetList.add(bacgroundSet)
        chartDataSetList.add(dataSet)

        val data = RadarData(chartDataSetList)
        data.setDrawValues(false)
        view.getView().detailChart.radarChart.data = data
        view.getView().detailChart.radarChart.invalidate()
    }

    private fun View.setOneClickListener(onClick: (View) -> Unit) {
        val oneClick = OneClickListener {
            onClick(it)
        }
        setOnClickListener(oneClick)

    }

    override fun detach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
        compositeDisposable.dispose()
    }
}