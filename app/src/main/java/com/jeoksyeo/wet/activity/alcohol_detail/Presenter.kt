package com.jeoksyeo.wet.activity.alcohol_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.opengl.Visibility
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.jeoksyeo.wet.activity.comment.Comment
import com.model.alcohol_detail.Alcohol
import com.model.alcohol_detail.AlcoholComponentData
import com.model.alcohol_detail.Review
import com.model.alcohol_detail.Srm
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Presenter : AlcoholDetailContract.BasePresenter {
    //5f로 설정하면 web line이 겉에 하나 더 생기게 되어 6줄이 되므로, 4.9f로 설정하여 최대 5개의 웹라인을 지정
    private val WEB_LINE_MAX = 4.9f
    override lateinit var view: AlcoholDetailContract.BaseView
    override lateinit var context: Context
    override lateinit var intent: Intent
    var isLike = false
    var alchol: Alcohol? = null
    private var componentAdapter:AlcoholComponentAdapter? = null
    private var toggle =true
    private val typeface by lazy {
        Typeface.createFromAsset(context.assets,"apple_sd_gothic_neo_sb.ttf")}

    private var compositeDisposable = CompositeDisposable()
    private var settingComponentList = mutableListOf<AlcoholComponentData>()
    private val NUM_SIZE = 30f
    private val RECYCLERVIEW_TEXT_SIZE = 10f
    private val CHAR_SIZE = 25f
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

                alcholData.likeCount?.let { //찜한수 체크
                    view.getView().alcoholdetailLikeCount.text = GlobalApplication.instance
                        .checkCount(it)
                }
                alcholData.viewCount?.let {  //조회수 체크
                    view.getView().detailEyeCount.text = GlobalApplication.instance.checkCount(it)
                }
            }
        }
    }

    override fun executeLike() {
        val check = JWTUtil.settingUserInfo(false)
        Log.e("check",check.toString())
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
                                view.getView().alcoholdetailLikeCount.text.toString().toInt(), 1)
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
                                view.getView().alcoholdetailLikeCount.text.toString().toInt(), -1)
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

        val halfLst = mutableListOf<AlcoholComponentData>()

        if(settingComponentList.size>4){//컴포넌트가 4개만 표시
            for(item in settingComponentList.withIndex()){
                if(item.index <4){
                    halfLst.add(item.value)
                }
            }
            componentAdapter = AlcoholComponentAdapter(halfLst)
        }
        else{
            view.getView().componentToggle.visibility =View.GONE
            componentAdapter = AlcoholComponentAdapter(settingComponentList)
        }

        view.getView().alcoholComponentRecyclerView.adapter = componentAdapter

        //리싸이클러뷰를 접고 펼칠 수 있기 때문에 고정 사이즈 사용하지않음.
        view.getView().alcoholComponentRecyclerView.setHasFixedSize(false)
        view.getView().alcoholComponentRecyclerView
            .addItemDecoration(GridSpacingItemDecoration(2, 4, true, 0))

        view.getView().alcoholComponentRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    fun commponentToggle(){
        if(toggle){//펼쳤을 때,
            toggle=false
            view.getView().componentToggleText.text = "주류정보 접기"
            val lst = mutableListOf<AlcoholComponentData>()

            for(idx in 4 until settingComponentList.size){
                lst.add(settingComponentList[idx])
            }
            componentAdapter?.addComponent(lst)
            componentAdapter?.notifyItemChanged(3,settingComponentList.size)
//            view.getView().alcoholComponentRecyclerView.layoutManager?.requestLayout()
        }
        else{//접을 때
            toggle=true
            view.getView().componentToggleText.text = "주류정보 펼치기"
            componentAdapter?.deleteComponent()
//            view.getView().alcoholComponentRecyclerView.layoutManager?.requestLayout()
        }
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
                        "IBU", "-1"
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

            if (data is String) { //DEFAULT 형식 셋팅
                if (data != "")
                    data.let { settingComponentList.add(compo) }
            }
            else if (data is List<*>) { //LIST 형식 셋팅
                if (data.size != 0) {
                    if (data[0] != "")
                        settingComponentList.add(compo)
                }
            }
            if(data is Srm){//SRM 형식 셋팅
                settingComponentList.add(compo)
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
                            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                                .getAlcoholDetail(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),alchol?.alcoholId!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    //화면 갱신을 위해서는 alcochol detail api에 있는 리뷰 데이터가 필요하므로 재호출
                                    it.data?.alcohol?.review?.let { review->
                                        initRadarChart(review)
                                    }
                                },{t->
                                    Log.e("차트 셋팅 에러",t.message.toString() )
                                }))

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
                        view.getView().detailReviewCountTop.text = GlobalApplication.instance.checkCount(result.data?.reviewList?.size!!)
                        view.getView().alcoholReviewSumCountText.text = lst.size.toString() + "개"
                    }

                    view.getView().recyclerViewReviewList.setHasFixedSize(false)
                    view.getView().recyclerViewReviewList.layoutManager = LinearLayoutManager(context)

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
                            GlobalApplication.instance.checkCount(it.score5Count!!)
                        view.getView().alcoholDetailScoreSeekbar.score4.text =
                            GlobalApplication.instance.checkCount(it.score4Count!!)
                        view.getView().alcoholDetailScoreSeekbar.score3.text =
                            GlobalApplication.instance.checkCount(it.score3Count!!)
                        view.getView().alcoholDetailScoreSeekbar.score2.text =
                            GlobalApplication.instance.checkCount(it.score2Count!!)
                        view.getView().alcoholDetailScoreSeekbar.score1.text =
                            GlobalApplication.instance.checkCount(it.score1Count!!)

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


    override fun initRadarChart(review:Review) {
        view.getView().radarChart.scaleX =1.27f
        view.getView().radarChart.scaleY =1.27f

        view.getView().radarChart.isRotationEnabled =false //차트 회전
        view.getView().radarChart.description.isEnabled =false // 범례 값 설명
        view.getView().radarChart.legend.isEnabled =false //범례 값
        view.getView().radarChart.webLineWidth =0f //대각선 두께
        view.getView().radarChart.webColor = context.resources.getColor(R.color.white,null) // 대각선 색
        view.getView().radarChart.webLineWidthInner =0.75f //내부선 두께
        view.getView().radarChart.webColorInner = context.resources.getColor(R.color.light_grey4,null) //내부선 색
        view.getView().radarChart.webAlpha = 200 //내부선 투명도 , 255 - opaque , 0 - transparent


        val xAxis = view.getView().radarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity =1f
        xAxis.typeface = typeface
        xAxis.textSize =11f
        xAxis.yOffset =0f
        xAxis.xOffset =0f
        xAxis.valueFormatter = object :IndexAxisValueFormatter(){
            private val name = listOf("아로마", "마우스필", "어울림", "시각적특징","테이스트")

            override fun getFormattedValue(value: Float): String {
                return name[value.toInt() % name.size]
            }
        }

        val yAxis = view.getView().radarChart.yAxis
        yAxis.setLabelCount(5,false)
        yAxis.xOffset =0f
        yAxis.yOffset =0f
        yAxis.axisMinimum =0f// 최소 웹 라인의 개수
        yAxis.axisMaximum =WEB_LINE_MAX //최대 웹 라인의 개수 지정
        yAxis.setDrawLabels(false) //수직으로 표시되는 수치값
        setRadarChartData(review)
    }

    private fun setRadarChartData(review:Review){
        val chartDataSetList = mutableListOf<IRadarDataSet>()
        val backgroundEntry = mutableListOf<RadarEntry>()
        val dataEntry = mutableListOf<RadarEntry>()

        review.let {info->
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))
            backgroundEntry.add(RadarEntry(5f))

            info.appearance?.let {
                Log.e("시각적특징",it.toString())
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.aroma?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.taste?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.mouthfeel?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
            info.overall?.let {
                dataEntry.add(RadarEntry(it.toFloat()))
            }
        }

        val bacgroundSet = RadarDataSet(backgroundEntry,"backgroundColor")
        bacgroundSet.color = context.resources.getColor(R.color.light_grey3,null) //데이터 셋 바깥 line color
        bacgroundSet.fillColor = context.resources.getColor(R.color.light_grey3,null) // 데이터 셋 내부 color
        bacgroundSet.setDrawFilled(true)
        bacgroundSet.fillAlpha =200
        bacgroundSet.isDrawHighlightCircleEnabled =true
        bacgroundSet.setDrawHighlightIndicators(false)

        val dataSet = RadarDataSet(dataEntry,"data")
        dataSet.color = context.resources.getColor(R.color.orange,null)
        dataSet.fillColor = context.resources.getColor(R.color.orange,null)
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha =200
        dataSet.isDrawHighlightCircleEnabled =true
        dataSet.setDrawHighlightIndicators(false)


        chartDataSetList.add(bacgroundSet)
        chartDataSetList.add(dataSet)

        val data = RadarData(chartDataSetList)
        data.setDrawValues(false)
        view.getView().radarChart.data = data
        view.getView().radarChart.invalidate()
    }
}