package com.activities.comment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.activities.alcohol_rated.AlcoholRatedContact
import com.base.BasePresenter
import com.base.BaseView
import com.model.my_comment.Comment
import com.skydoves.balloon.Balloon
import com.vuforia.engine.wet.databinding.CommentWindowBinding

interface CommentContract {

    interface CommentView:BaseView<CommentWindowBinding>

    interface CommentPresenter: BasePresenter<CommentWindowBinding>{
        /**
         * 액티비티로부터 구현된 CommentView 인터페이스를 presenter가 얻도록합니다.
         * 이를 통해 presenter는 액티비티에서 구현한 baseView의 모든 메서드를 사용할 수 있습니다.
         */
        val view: CommentView
        var viewObj:CommentView?

        /**
         * 말풍선 생성할 때, 필요하기 때문에 액티비티로부터 얻음
         */
        var lifecycleOwner: LifecycleOwner

        /**
         * '확인'버튼 활성화 여부 판단
         */
        fun confirmCheck():Boolean

        /**
         * 유저가 평가한 지표에 대해서 최종 스코어 계산
         */
        fun getScore(): Float

        /**
         * 말풍선 생성 해당 메서드를 통해서 물음표 이미지 클릭 시 , 컴포넌트를 설명하는 안내 말풍선 생성
         */
        fun createBalloon(context: Context?, str: Int): Balloon?

        /**
         * '확인'버튼 활성화 후 클릭 시 ,주류에 대한 코멘트 추가 수행
         */
        fun setComment(context: Context,alcoholId:String?,alcoholName:String?)

        /**
         * 메모리 해제
         */
        fun detachView()

        /**
         * 내가 평가한 주류를 수정할 때, 내가 남긴 점수 및 코멘트를 불러와서 셋팅
         */
        fun setMyComment(myComment:Comment)

        /**
         * 주류 평가를 수정하고서 확인을 눌렀을 때, 수정된 정보로 코멘트 수정
         */
        fun editMyComment(context: Context,alcoholId: String?,commentId:String?)

    }
}