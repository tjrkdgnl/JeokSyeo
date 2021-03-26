package com.adapters.viewholder

import android.view.View
import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.model.rated.ReviewList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedItemBinding

class AlcoholRatedViewHolder(val parent:ViewGroup):BaseViewHolder<ReviewList,AlcoholRatedItemBinding>(R.layout.alcohol_rated_item,parent) {

    override fun bind(data: ReviewList) {
        getViewBinding().review = data
        getViewBinding().executePendingBindings()

        getLineCount()

        //평가한 주류 별점 표시
        data.score?.let {
            getViewBinding().ratedItemRatingbar.rating = it.toFloat()
        }

        //리뷰를 평가한 날짜 셋팅
        data.updatedAt?.let {updateUtc->
            if(updateUtc !=0){
                getViewBinding().reviewDate.text = GlobalApplication.instance.getDate(updateUtc*1000L)
            }
            else{
                data.createdAt?.let { createUtc->
                    getViewBinding().reviewDate.text = GlobalApplication.instance.getDate(createUtc*1000L)
                }
            }
        }
    }

    //3줄 이상으로 넘어갈 시, 텍스트뷰 확장 버튼 hide / show 설정
    private fun getLineCount(){
        binding.ratedItemComment.post {
            val lineCount = binding.ratedItemComment.lineCount

            if(lineCount <=2){
                binding.ratedItemExpandableButton.visibility = View.INVISIBLE
            } else
                binding.ratedItemExpandableButton.visibility = View.VISIBLE
        }
    }
}