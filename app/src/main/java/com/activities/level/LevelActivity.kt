package com.activities.level

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.application.GlobalApplication
import com.base.BaseActivity
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LevelBinding

class LevelActivity:BaseActivity<LevelBinding>(), LevelContract.LevelView {
    private lateinit var presenter: Presenter

    override val layoutResID: Int = R.layout.level

    override fun setOnCreate() {
        presenter = Presenter().apply {
            viewObj = this@LevelActivity
            activity = this@LevelActivity
        }

        setHeaderInit()

        presenter.initMiniImageArray()
        presenter.getMyLevel()
    }

    override fun destroyPresenter() {
        presenter.detach()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

    override fun getBindingObj(): LevelBinding {
        return binding
    }

    override fun setHeaderInit() {
        binding.myLevelBasicHeader.basicHeaderWindowName.text = "나의 주류 레벨"
        binding.myLevelBasicHeader.basicHeaderWindowName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))

        //status bar 배경변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.white,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            }
        }

        //status bar의 icon 색상 변경
        val decor = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or decor.systemUiVisibility
        }else{
            decor.systemUiVisibility =0
        }
    }

    override fun settingMainAlcholGIF(level: Int) {
        when(level){
            1 ->{binding.imageViewEvaluationByMeMainBottle.setAnimation(R.raw.bottle_level01)}
            2 ->{binding.imageViewEvaluationByMeMainBottle.setAnimation(R.raw.bottle_level02)}
            3 ->{binding.imageViewEvaluationByMeMainBottle.setAnimation(R.raw.bottle_level03)}
            4 ->{binding.imageViewEvaluationByMeMainBottle.setAnimation(R.raw.bottle_level04)}
            5 ->{binding.imageViewEvaluationByMeMainBottle.setAnimation(R.raw.bottle_level05)}
        }
    }

    @SuppressLint("SetTextI18n")
    override fun settingExperience(reviewCount: Int,level: Int) {
        //기본적인 텍스트색상 셋팅
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.textViewCurrentLevel.setTextColor(resources.getColor(R.color.black,null))
        }else{
            binding.textViewCurrentLevel.setTextColor(ContextCompat.getColor(this,R.color.black))
        }

        //텍스트 특정부분만 색을 변경하기 위해서 spannableString 객체 생성
        val ssb = SpannableStringBuilder("현재 당신은 ${GlobalApplication.instance.getLevelName(level-1)} 입니다")

        //레벨 부분의 텍스트 색상 변경
        ssb.setSpan(ForegroundColorSpan(Color.parseColor("#fdb14b")),7,7+(GlobalApplication.instance.getLevelName(level-1).length),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewCurrentLevel.text = ssb

        if(level <5){
            //다음레벨 텍스트 셋팅
            binding.levelBottomBottle.textViewEvaluationNoticeNextLevelText.text = GlobalApplication.instance.getLevelName(level)
        }
        else{
            finalLevel()
        }

        //현재 리뷰 개수를 통한 경험치 계산
        val rest = reviewCount % 10

        // 다음 레벨까지 남은 리뷰 개수
        binding.levelBottomBottle.textViewEvaluationNoticeNextLevelCountText.text = "까지 ${11-rest}병 남았습니다."

        //경험치를 의미하는 full bottle 이미지 셋팅
        for(i in 0 until rest){
            presenter.miniAlcoholList[i].setImageResource(R.mipmap.mini_bottle_full)
        }
    }

    //마지막 레벨일 때, 숨겨져있는 로티 실행
    @SuppressLint("SetTextI18n")
    override fun finalLevel() {
        binding.levelBottomBottle.root.visibility = View.INVISIBLE
        binding.bottomWave.root.visibility=View.VISIBLE
        binding.bottomWave.waveParentLayout.setBackgroundColor(Color.parseColor("#f8f8f8"))

        binding.bottomWave.ranking5level.text = "${presenter.rankCount} 번째 선주(酒)자가 되셨습니다."
    }
}