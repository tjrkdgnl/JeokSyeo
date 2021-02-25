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
        var view: CommentView


        var lifecycleOwner: LifecycleOwner
        fun confirmCheck():Boolean
        fun getScore(): Float
        fun createBalloon(context: Context?, str: Int): Balloon?
        fun setComment(context: Context,alcoholId:String?,alcoholName:String?)
        fun detachView()
        fun setMyComment(myComment:Comment)
        fun editMyComment(context: Context,alcoholId: String,commentId:String)

    }
}