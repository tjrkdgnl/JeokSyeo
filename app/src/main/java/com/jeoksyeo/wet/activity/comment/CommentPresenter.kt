package com.jeoksyeo.wet.activity.comment

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.my_comment.Comment
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentPresenter : CommentContract.BasePresenter {
    override lateinit var view: CommentContract.BaseView
    override lateinit var activity: Activity
    override lateinit var lifecycleOwner: LifecycleOwner
    private var compositDisposable = CompositeDisposable()
    private lateinit var networkUtil: NetworkUtil


    override fun setNetworkUtil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(activity)
            networkUtil.register()
        }
    }

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

    //주류에 대한 코멘트를 작성하는 메소드
    override fun setComment(context: Context, alcoholId: String?, alcoholName: String?) {
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

        CoroutineScope(Dispatchers.IO).launch {
            var check = JWTUtil.checkToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    settingProgressbar(true)
                    compositDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .setComment(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,
                            map
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            settingProgressbar(false)
                            it.data?.let { result ->
                                result.result?.let {
                                    if (it.equals("SUCCESS")) {
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

                        }, { t ->
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

    override fun detachView() {
        compositDisposable.dispose()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
    }

    //내가 남긴 코멘트 셋팅하는 메서드
    override fun setMyComment(myComment: Comment) {
        view.getView().commentWindowBottomInclude.commentWindowAromaSeekbar.setProgress(myComment.aroma?.toFloat()!!)
        view.getView().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.setProgress(
            myComment.mouthfeel?.toFloat()!!
        )
        view.getView().commentWindowBottomInclude.commentWindowAppearanceSeekbar.setProgress(
            myComment.appearance?.toFloat()!!
        )
        view.getView().commentWindowBottomInclude.commentWindowTasteSeekbar.setProgress(myComment.taste?.toFloat()!!)
        view.getView().commentWindowBottomInclude.commentWindowOverallSeekbar.setProgress(myComment.overall?.toFloat()!!)
        myComment.contents?.let { content ->
            view.getView().commentWindowCommentEditText.setText(content)
        }

        myComment.score?.let { score ->
            view.getView().commentWindowRatingBar.rating = score.toFloat()
            view.getView().commentWindowScoreText.text = score.toString()
        }

        view.getView().commentWindowEvaluateButton.isEnabled = true
    }

    override fun editMyComment(context: Context, alcoholId: String, commentId: String) {
        val map = HashMap<String, Any>()
        map["contents"] = view.getView().commentWindowCommentEditText.text.toString()
        map["aroma"] =
            view.getView().commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
        map["mouthfeel"] =
            view.getView().commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
        map["taste"] =
            view.getView().commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
        map["appearance"] =
            view.getView().commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
        map["overall"] =
            view.getView().commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat


        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkToken()

            withContext(Dispatchers.Main) {
                if (check) {
                    settingProgressbar(true)

                    compositDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .editMyRatedReview(
                            GlobalApplication.userBuilder.createUUID,
                            GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,
                            commentId,
                            map
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
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
                        }, { t ->
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

    private fun settingProgressbar(check: Boolean) {
        if (check) {
            view.getView().progressbar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            view.getView().progressbar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }
    }


}