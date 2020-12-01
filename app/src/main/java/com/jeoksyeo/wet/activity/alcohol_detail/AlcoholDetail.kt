package com.jeoksyeo.wet.activity.alcohol_detail

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alcohol_detail.Alcohol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

class AlcoholDetail : AppCompatActivity(), AlcoholDetailContract.BaseView, View.OnClickListener {
    private lateinit var binding: AlcoholDetailBinding
    private lateinit var presenter: Presenter
    private var alcohol:Alcohol? =null
    private var refreshLikeCheck =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_detail)
        binding.lifecycleOwner = this

        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            alcohol = bundle?.getParcelable(GlobalApplication.MOVE_ALCHOL)
            binding.alcohol =alcohol
        }

        presenter = Presenter().apply {
            view = this@AlcoholDetail
            context=this@AlcoholDetail
            intent =this@AlcoholDetail.intent
            alcohol =this@AlcoholDetail.alcohol ?: Alcohol()
        }

        presenter.init()
        binding.AlcoholDetailSelectedByMe.setOnClickListener(this)
        binding.detailExpandableButton.setOnClickListener(this)
        presenter.checkCountLine()
    }

    override fun onStart() {
        super.onStart()
        if(refreshLikeCheck){
            refreshLikeCheck=false
            presenter.refreshIsLike()
        }
        presenter.initReview(this)
    }

    override fun onPause() {
        super.onPause()
        refreshLikeCheck = true
    }

    override fun getView(): AlcoholDetailBinding {
        return binding
    }

    override fun setLikeImage(isLike: Boolean) {
        if (isLike) {
            presenter.isLike = true
            binding.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_full_heart)

        } else {
            presenter.isLike=false
            binding.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_empty_heart)
        }
    }

    override fun settingProgressBar(check: Boolean) {
        if(check){
            binding.progressbar.root.visibility = View.VISIBLE
            this.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            binding.progressbar.root.visibility = View.INVISIBLE
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun settingExpandableText(check: Boolean) {
        if(check){
            binding.detailExpandableButton.visibility =View.VISIBLE

        }
        else{
            binding.detailExpandableButton.visibility =View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.AlcoholDetail_selectedByMe -> { presenter.executeLike() }

            R.id.AlcoholDetail_evaluateButton->{ presenter.checkReviewDuplicate(this) }

            R.id.detail_expandableButton->{ presenter.expandableText()}

            R.id.component_toggle->{presenter.commponentToggle()}
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}