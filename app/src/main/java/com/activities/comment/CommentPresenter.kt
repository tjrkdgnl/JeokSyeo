package com.activities.comment

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.my_comment.Comment
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentPresenter : CommentContract.CommentPresenter {
    override var viewObj: CommentContract.CommentView? =null

    override  val view: CommentContract.CommentView by lazy {
        viewObj!!
    }

    override lateinit var activity: Activity
    override lateinit var lifecycleOwner: LifecycleOwner
    private var compositDisposable = CompositeDisposable()

    override fun confirmCheck(): Boolean {
        return view.getBindingObj().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat != 0f &&
                view.getBindingObj().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat != 0f &&
                view.getBindingObj().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat != 0f &&
                view.getBindingObj().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat != 0f &&
                view.getBindingObj().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat != 0f &&
                view.getBindingObj().commentWindowCommentEditText.text.isNotEmpty()
    }

    override fun getScore(): Float {
        return (view.getBindingObj().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
                + view.getBindingObj().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
                + view.getBindingObj().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
                + view.getBindingObj().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
                + view.getBindingObj().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat)

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

    override fun setComment(context: Context, alcoholId: String?, alcoholName: String?) {
        //API에 전송할 파라미터값 셋팅
        val map = HashMap<String, Any>()
        map.put(
            "contents",
            view.getBindingObj().commentWindowCommentEditText.text.toString())
        map.put(
            "aroma",
            view.getBindingObj().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
        )
        map.put(
            "mouthfeel",
            view.getBindingObj().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
        )
        map.put(
            "taste",
            view.getBindingObj().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
        )
        map.put(
            "appearance",
            view.getBindingObj().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
        )
        map.put(
            "overall",
            view.getBindingObj().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat
        )

        CoroutineScope(Dispatchers.IO).launch {
            var check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    settingProgressbar(true)
                    //api 호출
                    compositDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .setComment(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(),
                                alcoholId,
                                map
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({    //api 호출 결과
                                settingProgressbar(false)
                                it.data?.let { result ->
                                    result.result?.let {
                                        if (it == "SUCCESS") {
                                            Toast.makeText(
                                                context,
                                                alcoholName + "에 리뷰를 남기셨습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            (context as Activity).finish()
                                        } else
                                            Toast.makeText(
                                                context,
                                                "주류 작성을 실패했습니다. 다시 시도해 주세요",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                }

                            }, { t -> //api 호출 에러
                                CustomDialog.networkErrorDialog(context)
                                settingProgressbar(false)
                                Log.e(ErrorManager.COMMENT, t.message.toString())
                            })
                    )
                } else {
                    CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }

    override fun setMyComment(myComment: Comment) {
        view.getBindingObj().commentWindowBottomInclude.commentWindowAromaSeekbar.setProgress(
            myComment.aroma?.toFloat()!!
        )
        view.getBindingObj().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.setProgress(
            myComment.mouthfeel?.toFloat()!!
        )
        view.getBindingObj().commentWindowBottomInclude.commentWindowAppearanceSeekbar.setProgress(
            myComment.appearance?.toFloat()!!
        )
        view.getBindingObj().commentWindowBottomInclude.commentWindowTasteSeekbar.setProgress(
            myComment.taste?.toFloat()!!
        )
        view.getBindingObj().commentWindowBottomInclude.commentWindowOverallSeekbar.setProgress(
            myComment.overall?.toFloat()!!
        )
        myComment.contents?.let { content ->
            view.getBindingObj().commentWindowCommentEditText.setText(content)
        }

        myComment.score?.let { score ->
            view.getBindingObj().commentWindowRatingBar.rating = score.toFloat()
            view.getBindingObj().commentWindowScoreText.text = score.toString()
        }

        view.getBindingObj().commentWindowEvaluateButton.isEnabled = true
    }

    override fun editMyComment(context: Context, alcoholId: String?, commentId: String?) {
        //api에 전송할 데이터값 map에 삽입
        val map = HashMap<String, Any>()
        map["contents"] = view.getBindingObj().commentWindowCommentEditText.text.toString()
        map["aroma"] =
            view.getBindingObj().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
        map["mouthfeel"] =
            view.getBindingObj().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
        map["taste"] =
            view.getBindingObj().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
        map["appearance"] =
            view.getBindingObj().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
        map["overall"] =
            view.getBindingObj().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat


        CoroutineScope(Dispatchers.IO).launch {
            //엑세스 토큰이 유효한 경우에만 호출
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    settingProgressbar(true)
                    //api 호출
                    compositDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .editMyRatedReview(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(),
                                alcoholId,
                                commentId,
                                map
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({        //api 결과
                                settingProgressbar(false)
                                it.data?.let { result ->
                                    result.result?.let {
                                        if (it == "SUCCESS") {
                                            Toast.makeText(context, " 수정되었습니다.", Toast.LENGTH_SHORT)
                                                .show()
                                            (context as Activity).finish()
                                        } else
                                            Toast.makeText(
                                                context,
                                                "리뷰 수정을 실패했습니다. 다시 시도해 주세요",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                    }
                                }
                            }, { t ->//api 호출 실패
                                CustomDialog.networkErrorDialog(context)
                                settingProgressbar(false)
                                Log.e(ErrorManager.COMMENT_EDIT, t.message.toString())
                            })
                    )
                } else {
                    CustomDialog.loginDialog(context, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }

    //로딩화면의 유무 표시
    private fun settingProgressbar(check: Boolean) {
        if (check) {
            view.getBindingObj().progressbar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            view.getBindingObj().progressbar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun detachView() {
        compositDisposable.dispose()
        viewObj=null
    }
}