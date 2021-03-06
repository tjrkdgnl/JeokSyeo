package com.jeoksyeo.wet.activity.level

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LevelBinding
import kotlinx.android.synthetic.main.level_bottom_wave.view.*

class LevelActivity:AppCompatActivity(), View.OnClickListener, LevelContract.BaseView {
    private lateinit var binding:LevelBinding
    private lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this, R.layout.level)
        binding.myLevelBasicHeader.basicHeaderWindowName.text = "나의 주류 레벨"

        presenter = Presenter().apply {
            view = this@LevelActivity
            context = this@LevelActivity.baseContext
        }

        presenter.setNetworkUtil()

        presenter.initMiniImageArray()
        presenter.getMyLevel()

    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = LevelActivity::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.basicHeader_backButton ->{
                finish()
                overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }

    override fun getView(): LevelBinding {
        return binding
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
        val rest = reviewCount % 10
        val ssb = SpannableStringBuilder("현재 당신은 ${GlobalApplication.instance.getLevelName(level-1)} 입니다")

        ssb.setSpan(ForegroundColorSpan(Color.parseColor("#fdb14b")),7,7+(GlobalApplication.instance.getLevelName(level-1).length),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.textViewCurrentLevel.setTextColor(resources.getColor(R.color.black,null))
        }else{
            binding.textViewCurrentLevel.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
        binding.textViewCurrentLevel.text = ssb

        if(level <5){
            binding.levelBottomBottle.textViewEvaluationNoticeNextLevelText.text = GlobalApplication.instance.getLevelName(level) //다음레벨
        }
        else{
            finalLevel()
        }


        binding.levelBottomBottle.textViewEvaluationNoticeNextLevelCountText.text = "까지 ${11-rest}병 남았습니다." // 다음 레벨까지 남은 리뷰 개수

        for(i in 0 until rest){
            presenter.miniAlcoholList[i].setImageResource(R.mipmap.mini_bottle_full)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun finalLevel() {
        binding.levelBottomBottle.root.visibility = View.INVISIBLE
        binding.bottomWave.root.visibility=View.VISIBLE
        binding.bottomWave.waveParentLayout.setBackgroundColor(Color.parseColor("#f8f8f8"))

        binding.bottomWave.ranking5level.text = "${presenter.rankCount} 번째 선주(酒)자가"
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
}