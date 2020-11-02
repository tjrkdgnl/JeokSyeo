package com.jeoksyeo.wet.activity.comment

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CommentPresenter : CommentContract.BasePresenter {
    override lateinit var view: CommentContract.BaseView
    override lateinit var lifecycleOwner: LifecycleOwner
    private  var disposable: Disposable? =null

    override fun confirmCheck(): Boolean {
        return view.getView().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat != 0f &&
                view.getView().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat != 0f &&
                view.getView().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat != 0f &&
                view.getView().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat != 0f &&
                view.getView().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat != 0f &&
                view.getView().commentWindowCommentEditText.text.isNotEmpty()
    }

    override fun getScore(): Float {
        return (view.getView().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
                + view.getView().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
                + view.getView().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
                + view.getView().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
                + view.getView().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat)

    }

    override fun createBalloon(context: Context?, str: Int): Balloon? {
        return Balloon.Builder(context!!)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.RIGHT)
            .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setArrowVisible(true)
            .setWidthRatio(0.7f)
            .setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            .setPaddingLeft(8)
            .setPaddingRight(8)
            .setPaddingTop(5)
            .setPaddingBottom(5)
            .setTextSize(12f)
            .setTextIsHtml(true)
            .setTextGravity(Gravity.START)
            .setTextColorResource(R.color.white)
            .setTextResource(str)
            .setElevation(0)
            .setAlpha(1f)
            .setCornerRadius(4f)
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setLifecycleOwner(lifecycleOwner)
            .setAutoDismissDuration(15000L)
            .build()
    }

    override fun setComment(context: Context, alcholId: String?, alcholName: String?) {
        val map = HashMap<String, Any>()
        map.put("contents", view.getView().commentWindowCommentEditText.text.toString())
        map.put(
            "aroma",
            view.getView().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
        )
        map.put(
            "mouthfeel",
            view.getView().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
        )
        map.put(
            "taste",
            view.getView().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
        )
        map.put(
            "appearance",
            view.getView().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
        )
        map.put(
            "overall",
            view.getView().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat
        )

        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .setComment(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getAccessToken(),
                alcholId,
                map
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.let { result ->
                    result.result?.let {
                        if (it.equals("SUCCESS")) {
                            Toast.makeText(context, alcholName + "에 리뷰를 남기셨습니다.", Toast.LENGTH_SHORT).show()
                            (context as Activity).finish()
                        } else
                            Toast.makeText(context, "주류 작성을 실패했습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT)
                                .show()
                    }
                }

            }, { t -> Log.e(ErrorManager.COMMENT, t.message.toString()) })
    }

    override fun detachView() {
        disposable?.dispose()
    }
}