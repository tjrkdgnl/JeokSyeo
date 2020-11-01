package com.jeoksyeo.wet.activity.comment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.CommentWindowBinding
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener

class Comment :AppCompatActivity(), OnProgressChangedListener, View.OnScrollChangeListener,
    View.OnClickListener, TextWatcher{
    private lateinit var binding:CommentWindowBinding
    private var aromaCheck = false
    private var mouthfeelCheck = false
    private var tasteCheck = false
    private var appearanceCheck = false
    private var overallCheck = false
    private var sumCheck = false
    private var score = 0f
    private var comment: String? = null
    private var commentCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.comment_window)

        binding.commentWindowContainer.setOnScrollChangeListener(this)

        binding.commentWindowBottomInclude.commentWindowAromaSeekbar.onProgressChangedListener = this
        binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.onProgressChangedListener = this
        binding.commentWindowBottomInclude.commentWindowTasteSeekbar.onProgressChangedListener = this
        binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.onProgressChangedListener = this
        binding.commentWindowBottomInclude.commentWindowOverallSeekbar.onProgressChangedListener = this

        binding.commentWindowCommentEditText.addTextChangedListener(this)
        binding.commentWindowCommentEditText.setOnKeyListener { v, keycode, event ->
            if (keycode == KeyEvent.KEYCODE_ENTER) {
                hideKeypad(binding.commentWindowCommentEditText)
                return@setOnKeyListener true
            } else return@setOnKeyListener false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }


    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(
        bubbleSeekBar: BubbleSeekBar?,
        progress: Int,
        progressFloat: Float,
        fromUser: Boolean
    ) {
        binding.commentWindowRatingBar.rating = getScore() / 5

        if (getScore() == 25f) {
            binding.commentWindowScoreText.text = getScore().toString() + "/25"
        } else binding.commentWindowScoreText.text = String.format("%.1f", getScore()) + "/25"

        confirmCheck()
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

    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        binding.commentWindowBottomInclude.commentWindowAromaSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowTasteSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowOverallSeekbar.correctOffsetWhenContainerOnScrolling()
    }

    private fun confirmCheck() {
        aromaCheck = binding.commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat != 0f
        mouthfeelCheck = binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat != 0f
        tasteCheck = binding.commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat != 0f
        appearanceCheck = binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat != 0f
        overallCheck = binding.commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat != 0f
        sumCheck = aromaCheck && mouthfeelCheck && tasteCheck && appearanceCheck && overallCheck && commentCheck
        binding.commentWindowEvaluateButton.isEnabled = sumCheck
    }

    private fun getScore(): Float {
        score = (binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.progressFloat
                + binding.commentWindowBottomInclude.commentWindowAromaSeekbar.progressFloat
                + binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.progressFloat
                + binding.commentWindowBottomInclude.commentWindowTasteSeekbar.progressFloat
                + binding.commentWindowBottomInclude.commentWindowOverallSeekbar.progressFloat)
        return score
    }

    fun CreateBalloon(context: Context?, str: Int): Balloon? {
        return Balloon.Builder(context!!)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.RIGHT)
            .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setArrowVisible(true)
            .setWidthRatio(0.7f)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
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
            .setLifecycleOwner(this)
            .setAutoDismissDuration(15000L)
            .build()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.compoent_aroma -> {
                CreateBalloon(this, R.string.explainAroma)?.apply {
                    showAlignLeft(v)
                    show(v)
                }
            }
            R.id.compoent_mouthfeel -> {
                CreateBalloon(this, R.string.explainMouthfeel)?.apply {
                    showAlignLeft(v)
                    show(v)
                }
            }
            R.id.compoent_taste -> {
                CreateBalloon(this, R.string.explainTaste)?.apply {
                    showAlignLeft(v)
                    show(v)
                }
            }
            R.id.compoent_appearance -> {
                CreateBalloon(this, R.string.explainAppearance)?.apply {
                    showAlignLeft(v)
                    show(v)
                }
            }
            R.id.compoent_overall -> {
                CreateBalloon(this, R.string.explainOverall)?.apply {
                    showAlignLeft(v)
                    show(v)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.commentWindowCommentCountText.text = binding.commentWindowCommentEditText.text.length.toString() + "/100"

        commentCheck = count != 0

        confirmCheck()
        comment = s.toString()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    fun hideKeypad(edit_nickname: EditText) {
        val inputMethodManager =
            baseContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_nickname.windowToken, 0)
    }
}