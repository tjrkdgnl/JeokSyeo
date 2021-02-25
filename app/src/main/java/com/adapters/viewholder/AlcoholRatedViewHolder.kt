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

        data.score?.let {
            getViewBinding().ratedItemRatingbar.rating = it.toFloat()
        }

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


    private fun getLineCount(){
        binding.ratedItemComment.post(Runnable {
            val lineCount = binding.ratedItemComment.lineCount

            if(lineCount <=2){
                binding.ratedItemExpandableButton.visibility = View.INVISIBLE
            }
            else
                binding.ratedItemExpandableButton.visibility = View.VISIBLE
        })
    }
}