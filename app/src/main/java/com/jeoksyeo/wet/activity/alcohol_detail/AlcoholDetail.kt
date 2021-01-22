package com.jeoksyeo.wet.activity.alcohol_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alcohol_detail.Alcohol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

class AlcoholDetail : AppCompatActivity(), AlcoholDetailContract.BaseView, View.OnClickListener,ViewTreeObserver.OnPreDrawListener {
    private lateinit var binding: AlcoholDetailBinding
    private var bindObj:AlcoholDetailBinding? =null
    private lateinit var presenter: Presenter
    private var alcohol:Alcohol? =null
    private var refreshLikeCheck =false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindObj = DataBindingUtil.setContentView(this, R.layout.alcohol_detail)
        binding = bindObj!!
        binding.lifecycleOwner = this

        //shared element를 통해 이동할 이미지가 set되는 것 지연시켜줌
        //호출된 액티비티로 넘어온 이미지는 자신이 들어갈 이미지뷰의 사이즈를 측정알아야 자연스러운 애니메이션이 형성되므로
        //사이즈 측정이 끝날 때 까지 지연되야한다.
        supportPostponeEnterTransition()

        //이미지 측정이 끝났을 때 지연되었던 이미지를 setting 진행
        binding.detailMainImg.viewTreeObserver.addOnPreDrawListener {
            //넘어온 이미지를 통해 셋팅되야하므로 현재 이미지로 그려지는 것을 제거해야한다.
           binding.detailMainImg.viewTreeObserver.removeOnPreDrawListener(this@AlcoholDetail)
           supportStartPostponedEnterTransition()
            true
        }

        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            alcohol = bundle?.getParcelable(GlobalApplication.MOVE_ALCHOL)
            binding.alcohol =alcohol //주류 이미지
            binding.detailAlcoholinfo.alcohol =alcohol //주류에 대한 기본정보
            binding.detailDescription.alcohol =alcohol //주류 설명
            Log.e("alcoholId", binding.detailAlcoholinfo.alcohol!!.alcoholId.toString())
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
        bindObj=null
        presenter.detach()
    }

    override fun onPreDraw(): Boolean {
        return true
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
        supportFinishAfterTransition()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}