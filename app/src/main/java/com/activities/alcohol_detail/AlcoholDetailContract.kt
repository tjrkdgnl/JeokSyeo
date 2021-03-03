package com.activities.alcohol_detail

import android.content.Context
import com.base.BasePresenter
import com.base.BaseView
import com.model.alcohol_detail.Alcohol
import com.model.review.EvaluateIndicator
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding


/**
 * '주류상세정보'에 대한 인터페이스입니다. 해당 인터페이스에서는 '주류 상세 정보'에서 발생하는 이벤트 및 ui 업데이트를
 * 해결하기 위한 기본정보들을 갖습니다. baseView는 액티비티에서 구현이 되어야 하며,basePresenter는 Presenter 클래스에서 구현되어야 합니다.
 */
interface AlcoholDetailContract {

    interface  AlcoholDetailView:BaseView<AlcoholDetailBinding>{

        /**
         * 유저가 해당 술을 찜했는지의 여부를 {@param isLike}를 통해서 UI를 업데이트 하는 기능입니다.
         */
        fun setLikeImage(isLike:Boolean)

        /**
         * 주류 설명이 5줄이상인지 검사하여 '더 보기' 버튼의 유무를 판단합니다.
         * 만약 5줄 미만이라면 {@param check}는 true 입니다. 따라서 '더 보기' 버튼이 표시가 됩니다.
         * 반대로 5줄 이상이라면 {@param check}는 false입니다. 따라서 '더 보기' 버튼을 화면에 표시하지 않습니다.
         */
        fun settingExpandableText(check :Boolean)

        /**
         * 시간이 지연될 것 같은 일을 진행할 시, 화면에서 진행 상황을 알려주도록 하기 위한 메서드입니다.
         * 만약 일이 진행중이라면 {@param check}는 true가 됩니다. 따라서 'loading' 애니메이션이 화면상에 나옵니다.
         * 반대로 일처리가 끝나거나, 에러로 인해 일처리가 중지되면 {@param check}를 false로 합니다.
         * 따라서 'loading' 애니메이션이 화면에서 사라집니다.
         */
        fun settingProgressBar(check:Boolean)

        /**
         * 주류 설명을 접고 펼치는 기능입니다.
         */
        fun expandableText()
    }

    interface AlcoholDetailPresenter:BasePresenter<AlcoholDetailBinding>{
        /**
         * 액티비티로부터 구현된 AlcoholDetailView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view:AlcoholDetailView
        var viewObj:AlcoholDetailView?

        /**
         * '주류상세정보'화면은 언제나 이전 화면으로부터 alcohol 객체를 넘겨받습니다. 이 객체가 갖고 있는 정보를 통해
         *  UI에서 표시해야하는 정보들을 표시합니다.
         */
        var alcohol:Alcohol

        /**
         * 찜한 수, 조회수, 내가 찜했는지에 대한 여부 등 기본적인 셋팅을 진행합니다.
         */
        fun init()


        /**
         * 유저가 좋아요 및 싫어요를 클릭했는지에 대한 여부를 api 통신을 통해 저장합니다.
         */
        fun executeLike()


        /**
         * 주류 컴포넌트를 셋팅합니다. 주류 타입별로 주류가 갖고있는 정보가 다르며, 보여져야하는 정보의 순서도 다릅니다.
         * 따라서 보여져야하는 컴포넌트 값을 셋팅합니다.
         */
        fun initComponent(context: Context)


        /**
         * 리뷰글, 리뷰 점수, 리뷰 총평 등 주류가 갖는 리뷰들을 불러와서 셋팅합니다. 이는 주류를 평가하게 되면 자동 업데이트가
         * 되어져야 하는 부분이므로 액티비티의 onResume()에서 호출하게 됩니다.
         */
        fun initReview(context: Context)


        /**
         * 리뷰를 남긴 주류인지 체크 하는 기능입니다. 만약 리뷰를 남겼다면, toast를 통해 유저에게 알립니다.
         * 리뷰를 남기지 않았으면 평가화면으로 이동시킵니다.
         */
        fun checkReviewDuplicate(context: Context)


        /**
         * 주류 평가에 대한 차트를 생성합니다. 디자인대로 만들기 위해서각 항목의 최대치를 같는 set(회색배경)을
         * 차트에 포함시켰습니다.
         */
        fun initRadarChart(review:EvaluateIndicator)

        /**
         * 주류 설명이 몇줄인지 체크합니다. 이를 통해 {@method settingExpandableText(context:Context)}가 호출됩니다.
         */
        fun checkScriptLine()

        /**
         * 유저가 찜을 했는지에 대한 여부입니다. 만약 로그인이 되어있지 않는 상태일 때, '평가하기','찜하기', '리뷰 좋아요 및 싫어요'등을
         * 클릭하면 로그인 여부 다이얼로그가 나옵니다. 이를 통해 로그인 진행 후, 다시 '주류상세화면'으로 이동하게 됩니다. 때문에
         * 액티비티의 onResume()에서 실행되어야 refresh 된 정보를 ui에 표시할 수 있습니다.
         */
        fun CheckLike()

        /**
         * 리뷰 모양 말풍선 클릭 시, 리뷰가 보이도록 스크롤을 이동시키는 메서드입니다.
         */
       fun moveReview()

        /**
         * 액티비티가 사라질 때 실행되어야하며 메모리 할당을 해제시켜줘야하는 작업을 진행하는 메서드입니다.
         */
        fun detach()

    }
}