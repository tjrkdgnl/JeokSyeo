package com.jeoksyeo.wet.activity.comment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.CommentWindowBinding
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener

class Comment :AppCompatActivity(), OnProgressChangedListener, View.OnScrollChangeListener,
    View.OnClickListener, TextWatcher, CommentContract.BaseView {
    private lateinit var binding:CommentWindowBinding
    private var calculateScore:Float =0f
    private var alchol:Alchol? =null

    private lateinit var commentPresenter: CommentPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.comment_window)

        commentPresenter = CommentPresenter().apply {
            view=this@Comment
            lifecycleOwner =this@Comment
        }

        if(intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)){
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            bundle?.let { bun->
               alchol = bun.getParcelable(GlobalApplication.MOVE_ALCHOL)
                alchol?.let {
                    binding.alchol = it
                }
            }
        }

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
    override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
        calculateScore = commentPresenter.getScore()

        binding.commentWindowRatingBar.rating = calculateScore / 5

        if (calculateScore == 25f) {
            binding.commentWindowScoreText.text = calculateScore.toString() + "/25"
        } else binding.commentWindowScoreText.text = String.format("%.1f", calculateScore) + "/25"

        binding.commentWindowEvaluateButton.isEnabled = commentPresenter.confirmCheck()
    }

    override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
    }

    override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
    }

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int
    ) {
        binding.commentWindowBottomInclude.commentWindowAromaSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowMourhfeelSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowTasteSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowAppearanceSeekbar.correctOffsetWhenContainerOnScrolling()
        binding.commentWindowBottomInclude.commentWindowOverallSeekbar.correctOffsetWhenContainerOnScrolling()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.comment_window_evaluateButton->{commentPresenter.setComment(this,alchol?.alcholId,alchol?.name?.kr)}

            R.id.compoent_aroma -> {
                with(commentPresenter.createBalloon(this,R.string.explainAroma)){
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_mouthfeel -> {
                with(commentPresenter.createBalloon(this, R.string.explainMouthfeel)){
                    this?.showAlignLeft(v)
                    this?.show(v)
                }

            }
            R.id.compoent_taste -> {
                with(commentPresenter.createBalloon(this, R.string.explainTaste)){
                    this?.showAlignLeft(v)
                    this?.show(v)
                }
            }
            R.id.compoent_appearance -> {
                with(commentPresenter.createBalloon(this, R.string.explainAppearance)){
                    this?.showAlignLeft(v)
                    this?.show(v)
                }

            }
            R.id.compoent_overall -> {
                with(commentPresenter.createBalloon(this,R.string.explainOverall)){
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
        binding.commentWindowCommentCountText.text = binding.commentWindowCommentEditText.text.length.toString() + "/100"


        binding.commentWindowEvaluateButton.isEnabled = commentPresenter.confirmCheck()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    fun hideKeypad(edit_nickname: EditText) {
        val inputMethodManager =
            baseContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_nickname.windowToken, 0)
    }

    override fun getView(): CommentWindowBinding {
       return binding
    }

    override fun onDestroy() {
        super.onDestroy()
        commentPresenter.detachView()
    }
}