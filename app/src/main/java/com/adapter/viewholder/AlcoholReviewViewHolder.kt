package com.adapter.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ReviewItemBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.FieldPosition

@SuppressLint("SetTextI18n")
class AlcoholReviewViewHolder(
    val context:Context,val parent:ViewGroup) :BaseViewHolder<ReviewList,ReviewItemBinding>(R.layout.review_item,parent) {
    private val compositeDisposable = CompositeDisposable()

    override fun bind(data: ReviewList) {
        binding.reivews = data
        binding.executePendingBindings()

        getLineCount()// "더 보기" 유무 판

         data.level?.let {
             binding.textViewCommentUserRank.text = "Lv." + it.toString() +" "+ GlobalApplication.instance.getLevelName(it)
         }
        data.score?.let {
            binding.ratingBarReviewRatingbar.rating = it.toFloat()
        }

        binding.reviewItemToggleButton.setOnClickListener{
            if(binding.expandableTextViewReviewcomment.isExpanded){
                binding.expandableTextViewReviewcomment.collapse()
                binding.reviewItemToggleButton.text="더보기"
            }
            else{
                binding.expandableTextViewReviewcomment.expand()
                binding.reviewItemToggleButton.text="접기"
            }
        }

        if(data.profile?.size !=0){
            data.profile?.get(0)?.mediaResource?.small?.let {
                Glide.with(parent.context)
                    .load(it.src)
                    .apply(RequestOptions()
                        .signature(ObjectKey("signature"))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop())
                    .into(binding.imageViewCommentProfileImg)
            }
        }
    }

    private fun getLineCount(){
        binding.expandableTextViewReviewcomment.post(Runnable {
            val count = binding.expandableTextViewReviewcomment.lineCount
            if(count <=2){
                binding.reviewItemToggleButton.visibility =View.INVISIBLE
            }
        })
    }

    fun setLike(alcoholId:String?,review:ReviewList,disLikeList:MutableList<Boolean>,position:Int){
        var check = JWTUtil.settingUserInfo(false)
        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .setLike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    alcoholId,review.review_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_full)
                    binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_empty)

                    binding.textViewRecommendUpCount.text =
                        GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),1)

                    if(disLikeList[position]){
                        disLikeList[position] =false
                        binding.textViewRecommendDownCount.text =
                            GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),-1)
                    }

                },{
                        t -> Log.e(ErrorManager.REVIEW_LIKE,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }

    }
    fun setUnlike(alcoholId:String?,review:ReviewList){
        var check = JWTUtil.settingUserInfo(false)

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .setUnLike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    alcoholId,review.review_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_empty)
                    binding.textViewRecommendUpCount.text =
                        GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),-1)

                },{
                        t -> Log.e(ErrorManager.REVIEW_UNLIKE,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }
    fun setDislike(alcoholId:String?,review:ReviewList,likeList:MutableList<Boolean>,position: Int){
        var check = JWTUtil.settingUserInfo(false)

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .setDislike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    alcoholId,review.review_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_full)
                    binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_empty)

                    binding.textViewRecommendDownCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),1)

                    if(likeList[position]){
                        binding.textViewRecommendUpCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),-1)
                    }
                },{
                        t -> Log.e(ErrorManager.REVIEW_DISLIKE,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }
    fun setUnDislike(alcoholId:String?,review:ReviewList){
        var check = JWTUtil.settingUserInfo(false)

        if(check){
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .setUnDislike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                    alcoholId,review.review_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_empty)

                    binding.textViewRecommendDownCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),-1)

                },{ t -> Log.e(ErrorManager.REVIEW_UNDISLIKE,t.message.toString())
                }))
        }
        else{
            CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
        }
    }
}