package com.jeoksyeo.wet.activity.alchol_detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholDetailBinding

class AlcholDetail : AppCompatActivity(), AlcholDetailContract.BaseView, View.OnClickListener {
    private lateinit var binding: AlcholDetailBinding
    private lateinit var presenter: Presenter
    private var refreshCheck =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_detail)
        binding.lifecycleOwner = this

        presenter = Presenter().apply {
            view = this@AlcholDetail
            context=this@AlcholDetail
            intent =this@AlcholDetail.intent
        }
        presenter.init()

        binding.AlcholDetailSelectedByMe.setOnClickListener(this)
        binding.detailExpandableButton.setOnClickListener(this)

    }

    override fun getView(): AlcholDetailBinding {
        return binding
    }

    override fun setLikeImage(isLike: Boolean) {
        if (isLike) {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.full_heart)

        } else {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.empty_heart)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.AlcholDetail_selectedByMe -> {
                GlobalApplication.userInfo.getProvider()?.let {
                    if(!presenter.isLike){
                        presenter.isLike =true
                        presenter.alchol?.let {
                            presenter.executeLike()
                            binding.alcholdetailLikeCount.text = GlobalApplication.instance.checkCount(binding.alcholdetailLikeCount.text.toString().toInt(),1)
                        }
                    }
                    else{
                        presenter.isLike=false
                        presenter.alchol?.let {
                            presenter.cancelAlcholLike()
                            binding.alcholdetailLikeCount.text = GlobalApplication.instance.checkCount(binding.alcholdetailLikeCount.text.toString().toInt(),-1)
                        }
                    }
                }
            }

            R.id.AlcholDetail_evaluateButton->{
                presenter.checkReviewDuplicate(this)
            }

            R.id.detail_expandableButton->{ presenter.expandableText()}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }

    override fun onResume() {
        super.onResume()

        if(refreshCheck){
            refreshCheck=false
            presenter.initReview(this)
        }
    }

    override fun onStop() {
        super.onStop()
        refreshCheck =true
    }
}