package com.jeoksyeo.wet.activity.comment

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.model.my_comment.Comment
import com.skydoves.balloon.Balloon
import com.vuforia.engine.wet.databinding.CommentWindowBinding

interface CommentContract {

    interface BaseView{
        fun getView():CommentWindowBinding
    }

    interface BasePresenter{
        var view:BaseView
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