package com.jeoksyeo.wet.activity.alcohol_detail

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_detail)
        binding.lifecycleOwner = this

        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            alcohol = bundle?.getParcelable(GlobalApplication.MOVE_ALCHOL)
            binding.alcohol =alcohol //주류 이미지
            binding.detailAlcoholinfo.alcohol =alcohol //주류에 대한 기본정보
            binding.detailDescription.alcohol =alcohol //주류 설명

        }

        alcohol?.abv?.let {
            binding.detailAlcoholinfo.textViewDosu.text ="${it}%"
        }

        presenter = Presenter().apply {
            view = this@AlcoholDetail
            context=this@AlcoholDetail
            intent =this@AlcoholDetail.intent
            alcohol =this@AlcoholDetail.alcohol ?: Alcohol()
        }
        presenter.setNetworkUtil()

        presenter.init()
        presenter.checkCountLine()
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = AlcoholDetail::class.java
        if(refreshLikeCheck){
            refreshLikeCheck=false
            presenter.refreshIsLike()
        }
        presenter.initReview(this)
    }

    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onPause() {
        super.onPause()
        refreshLikeCheck = true
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun getView(): AlcoholDetailBinding {
        return binding
    }

    override fun setLikeImage(isLike: Boolean) {
        if (isLike) {
            presenter.isLike = true
            binding.detailAlcoholinfo.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_full_heart)

        } else {
            presenter.isLike=false
            binding.detailAlcoholinfo.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_empty_heart)
        }
    }

    override fun settingProgressBar(check: Boolean) {
        if(check){
            binding.detailAlcoholinfo.progressbar.root.visibility = View.VISIBLE
            this.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            binding.detailAlcoholinfo.progressbar.root.visibility = View.INVISIBLE
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun settingExpandableText(check: Boolean) {
        if(check){
            binding.detailDescription.detailExpandableButton.visibility =View.VISIBLE

        }
        else{
            binding.detailDescription.detailExpandableButton.visibility =View.INVISIBLE
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