package com.activities.alcohol_detail

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import com.application.GlobalApplication
import com.base.BaseActivity
import com.model.alcohol_detail.Alcohol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholDetailBinding

class AlcoholDetail :BaseActivity<AlcoholDetailBinding>(), AlcoholDetailContract.AlcoholDetailView, View.OnClickListener,ViewTreeObserver.OnPreDrawListener {

    override val layoutResID: Int = R.layout.alcohol_detail

    private lateinit var presenter: Presenter
    private var alcohol:Alcohol? =null

    @SuppressLint("SetTextI18n")
    override fun setOnCreate() {
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
        }

        presenter = Presenter().apply {
            viewObj = this@AlcoholDetail
            activity=this@AlcoholDetail
            alcohol =this@AlcoholDetail.alcohol ?: Alcohol()
        }
        presenter.init()
        presenter.checkScriptLine()

    }

    override fun onResume() {
        super.onResume()
        presenter.initReview(this)
        presenter.CheckLike()
    }

    override fun destroyPresenter() {
        presenter.detach()
    }


    override fun onPreDraw(): Boolean {
        return true
    }

    override fun getBindingObj(): AlcoholDetailBinding {
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
            R.id.alcoholdetail_likeCount->{presenter.executeLike()}

            R.id.AlcoholDetail_evaluateButton->{ presenter.checkReviewDuplicate(this) }

            R.id.detail_expandableButton->{ expandableText()}

            R.id.component_toggle->{presenter.commponentToggle() }

            R.id.detail_reviewCount_top->{presenter.moveReview()}
            R.id.detail_reviewImg_top->{presenter.moveReview()}
        }
    }

    override fun expandableText() {
        if (binding.detailDescription.textViewAlcoholDescription.isExpanded) {
            binding.detailDescription.textViewAlcoholDescription.collapse()
            binding.detailDescription.detailArrow.setImageResource(R.mipmap.down_errow)
            binding.detailDescription.detailExpandableText.text = "더보기"
        } else {
            binding.detailDescription.textViewAlcoholDescription.expand()
            binding.detailDescription.detailArrow.setImageResource(R.mipmap.up_errow)
            binding.detailDescription.detailExpandableText.text = "접기"
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}