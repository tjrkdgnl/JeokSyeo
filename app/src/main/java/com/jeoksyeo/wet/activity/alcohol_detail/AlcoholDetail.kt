package com.jeoksyeo.wet.activity.alcohol_detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

class AlcoholDetail : AppCompatActivity(), AlcoholDetailContract.BaseView, View.OnClickListener {
    private lateinit var binding: AlcoholDetailBinding
    private lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alcohol_detail)
        binding.lifecycleOwner = this

        presenter = Presenter().apply {
            view = this@AlcoholDetail
            context=this@AlcoholDetail
            intent =this@AlcoholDetail.intent
        }
        presenter.init()
        binding.AlcoholDetailSelectedByMe.setOnClickListener(this)
        binding.detailExpandableButton.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        presenter.isLike=false
        presenter.initReview(this)

    }

    override fun getView(): AlcoholDetailBinding {
        return binding
    }

    override fun setLikeImage(isLike: Boolean) {
        if (isLike) {
            binding.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_full_heart)

        } else {
            binding.AlcoholDetailSelectedByMe.setImageResource(R.mipmap.detail_empty_heart)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.AlcoholDetail_selectedByMe -> {
                if(!presenter.isLike) {
                    presenter.isLike = true
                    presenter.alchol?.let {
                        presenter.executeLike()
                    }
                }
                else{
                    presenter.isLike=false
                    presenter.alchol?.let {
                        presenter.cancelAlcoholLike()
                    }
                }
            }

            R.id.AlcoholDetail_evaluateButton->{
                presenter.checkReviewDuplicate(this)
            }

            R.id.detail_expandableButton->{ presenter.expandableText()}

            R.id.component_toggle->{presenter.commponentToggle()}
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}