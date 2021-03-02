package com.activities.comment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.application.GlobalApplication
import com.base.BaseActivity
import com.custom.CustomDialog
import com.model.alcohol_detail.Alcohol
import com.model.my_comment.Comment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.CommentWindowBinding
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener

@RequiresApi(Build.VERSION_CODES.M)
class CommentActivity : BaseActivity<CommentWindowBinding>(), OnProgressChangedListener,
    View.OnScrollChangeListener,
    View.OnClickListener, TextWatcher, CommentContract.CommentView,
    ViewTreeObserver.OnPreDrawListener {

    private var calculateScore: Float = 0f
    private var alcohol: Alcohol? = null
    private var myComment: Comment? = null
    private lateinit var commentPresenter: CommentPresenter
    private var cancelDialog: Dialog? = null
    private lateinit var cancelOkButton: Button
    private lateinit var cancelButton: Button
    private lateinit var cancelContent: TextView
    override val layoutResID: Int = R.layout.comment_window


    override fun setOnCreate() {
        //shared element를 통해 이동할 이미지가 set되는 것 지연시켜줌
        //호출된 액티비티로 넘어온 이미지는 자신이 들어갈 이미지뷰의 사이즈를 측정알아야 자연스러운 애니메이션이 형성되므로
        //사이즈 측정이 끝날 때 까지 지연되야한다.
        supportPostponeEnterTransition()

        //이미지 측정이 끝났을 때 지연되었던 이미지를 setting 진행
        binding.commentAlcoholImg.viewTreeObserver.addOnPreDrawListener {
            //넘어온 이미지를 통해 셋팅되야하므로 현재 이미지로 그려지는 것을 제거해야한다.
            binding.commentAlcoholImg.viewTreeObserver.removeOnPreDrawListener(this)
            supportStartPostponedEnterTransition()
            true
        }

        commentPresenter = CommentPresenter().apply {
            viewObj = this@CommentActivity
            lifecycleOwner = this@CommentActivity
            activity = this@CommentActivity
        }

        //주류상세화면으로에서 전달한 알코올 객체 확인
        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)

            bundle?.let { bun ->
                alcohol = bun.getParcelable(GlobalApplication.MOVE_ALCHOL)
                alcohol?.let {
                    //databinding class에 알코올 객체 전달
                    binding.alcohol = it
                }
                myComment = bun.getParcelable(GlobalApplication.MOVE_MY_COMMENT)
            }
        }

        //내가 평가한 주류를 수정하는 것이라면 기존에 평가한 정보 셋팅
        if (myComment != null) {
            myComment?.let {
                commentPresenter.setMyComment(it)
            }
        }

        binding.commentWindowCommentEditText.addTextChangedListener(this)


        //seekBar의 thumb가 위치가 잘못잡히는 현상이 발생하므로 해당 라이브러리 github에 올라와있는
        //임시 대체방법으로 설정
        binding.commentWindowContainer.setOnScrollChangeListener(this)
        binding.commentWindowBottomInclude.commentWindowAromaSeekbar.onProgressChangedListener =
            this
        binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.onProgressChangedListener =
            this
        binding.commentWindowBottomInclude.commentWindowTasteSeekbar.onProgressChangedListener =
            this
        binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.onProgressChangedListener =
            this
        binding.commentWindowBottomInclude.commentWindowOverallSeekbar.onProgressChangedListener =
            this
    }

    override fun destroyPresenter() {
        commentPresenter.detachView()

    }


    override fun onBackPressed() {
        if (cancelDialog == null) {
            cancelDialog = CustomDialog.createCustomDialog(this)

            cancelOkButton = cancelDialog?.findViewById(R.id.dialog_okButton)!!
            cancelButton = cancelDialog?.findViewById(R.id.dialog_cancelButton)!!
            cancelContent = cancelDialog?.findViewById(R.id.dialog_contents)!!
            cancelContent.text = "리뷰 작성을 취소하시겠습니까?\n작성한 내용이 사라집니다."
        }

        cancelOkButton.setOnClickListener {
            supportFinishAfterTransition()
            overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
            cancelDialog?.dismiss()
        }
        cancelButton.setOnClickListener {
            cancelDialog?.dismiss()
        }
    }

    override fun onPreDraw(): Boolean {
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float,
        fromUser: Boolean
    ) {
        calculateScore = commentPresenter.getScore()

        //별점 계산
        binding.commentWindowRatingBar.rating = calculateScore / 5

        //25점인 경우 25.0이 아닌 25 정수로 나오도록 분기처리
        if (calculateScore == 25f) {
            binding.commentWindowScoreText.text = "$calculateScore/25"
        } else {
            binding.commentWindowScoreText.text = String.format("%.1f", calculateScore) + "/25"
        }

        //확인버튼 활성화
        binding.commentWindowEvaluateButton.isEnabled = commentPresenter.confirmCheck()
    }

    override fun getProgressOnActionUp(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float
    ) {
    }

    override fun getProgressOnFinally(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float,
        fromUser: Boolean
    ) {
    }

    //스크롤뷰에 seekbar 라이브러리가 포함되어있다. 하지만 seekbar의 thumb의 위치가 잘못잡히므로 임시 해결방법을
    //사용하여 해결한다. 이는 해당 라이브러리의 github를 참조해야한다.
    override fun onScrollChange(
        v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int
    ) {
        binding.commentWindowBottomInclude.commentWindowAromaSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowTasteSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowOverallSeekbar.correctOffsetWhenContainerOnScrolling()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.comment_window_evaluateButton -> {
                myComment?.let { //이미 평가한 화면을 재 수정할 때
                    commentPresenter.editMyComment(
                        this,
                        alcohol?.alcoholId,
                        myComment?.reviewId
                    )
                } ?: commentPresenter.setComment(this, alcohol?.alcoholId, alcohol?.name?.kr)
            }

            R.id.compoent_aroma -> {
                with(commentPresenter.createBalloon(this, R.string.explainAroma)) {
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_mouthfeel -> {
                with(commentPresenter.createBalloon(this, R.string.explainMouthfeel)) {
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_taste -> {
                with(commentPresenter.createBalloon(this, R.string.explainTaste)) {
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_appearance -> {
                with(commentPresenter.createBalloon(this, R.string.explainAppearance)) {
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_overall -> {
                with(commentPresenter.createBalloon(this, R.string.explainOverall)) {
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.commentWindowCommentCountText.text =
            binding.commentWindowCommentEditText.text.length.toString() + "/500"
        binding.commentWindowEvaluateButton.isEnabled = commentPresenter.confirmCheck()
    }

    override fun afterTextChanged(s: Editable?) {
    }


    override fun getBindingObj(): CommentWindowBinding {
        return binding
    }
}