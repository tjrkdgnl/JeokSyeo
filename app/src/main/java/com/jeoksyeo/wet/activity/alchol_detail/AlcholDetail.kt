package com.jeoksyeo.wet.activity.alchol_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholDetailBinding

class AlcholDetail : AppCompatActivity(), AlcholDetailContract.BaseView, View.OnClickListener {
    private lateinit var binding: AlcholDetailBinding
    private lateinit var presenter: Presenter
    private var alchol: Alchol? = null
    private var isLike = false
    private var refreshCheck =false
    private var category_position=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_detail)
        binding.lifecycleOwner = this

        presenter = Presenter().apply {
            view = this@AlcholDetail
            context=this@AlcholDetail
        }


        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            category_position = bundle?.getInt(GlobalApplication.CATEGORY_POSITION)!!

            alchol = bundle.getParcelable(GlobalApplication.MOVE_ALCHOL)
            alchol?.let { alcholData->
                binding.alchol = alcholData
                presenter.initComponent(this, alcholData,category_position)
                Log.e("처음 알콜 상세",alchol?.alcholId.toString())
                alcholData.isLiked?.let { like ->
                    setLike(like)
                        isLike = like }

                alcholData.alcholId?.let {id-> presenter.initReview(this,id) }
            }
        }
        binding.AlcholDetailSelectedByMe.setOnClickListener(this)
        binding.detailExpandableButton.setOnClickListener(this)
    }

    override fun getView(): AlcholDetailBinding {
        return binding
    }

    override fun setLike(isLike: Boolean) {
        if (isLike) {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.full_heart)
            binding.alcholdetailLikeCount.text = (binding.alcholdetailLikeCount.text.toString().toInt()+1).toString()
        } else {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.empty_heart)
            binding.alcholdetailLikeCount.text = (binding.alcholdetailLikeCount.text.toString().toInt()-1).toString()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.AlcholDetail_selectedByMe -> {
                GlobalApplication.userInfo.getProvider()?.let {
                    if(!isLike){
                        isLike =true
                        alchol?.let {
                            presenter.executeLike(it.alcholId!!)
                        }
                    }
                    else{
                        isLike=false
                        alchol?.let {
                            presenter.cancelAlcholLike(it.alcholId!!)
                        }
                    }
                }
            }

            R.id.AlcholDetail_evaluateButton->{
                presenter.checkReviewDuplicate(this,alchol)
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
            alchol?.alcholId?.let {
                presenter.initReview(this,it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        refreshCheck =true
    }
}