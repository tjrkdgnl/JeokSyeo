package com.adapters.viewholder

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetTextI18n")
class AlcoholReviewViewHolder(
    val context:Context, val parent:ViewGroup, val likeList: MutableList<Boolean>
    ,val disLikeList: MutableList<Boolean>,val alcoholId: String?) :BaseViewHolder<ReviewList,ReviewItemBinding>(R.layout.review_item,parent) {
    private val compositeDisposable = CompositeDisposable()

    override fun bind(data: ReviewList) {
        binding.reivews = data
        binding.executePendingBindings()

        //리뷰 날짜 설정
        data.updatedAt?.let {updateUtc->
            if(updateUtc !=0){
                getViewBinding().textViewDate.text = GlobalApplication.instance.getDate(updateUtc*1000L)
            }
            else{
                data.createdAt?.let { createUtc->
                    getViewBinding().textViewDate.text = GlobalApplication.instance.getDate(createUtc*1000L)
                }
            }
        }

        //유저 정보 셋팅
         data.level?.let {
             binding.textViewCommentUserRank.text = "Lv." + it.toString() +" "+ GlobalApplication.instance.getLevelName(it-1)
         }
        data.score?.let {
            binding.ratingBarReviewRatingbar.rating = it.toFloat()
        }
        if(data.profile?.size !=0){
            data.profile?.get(0)?.mediaResource?.small?.let {
                Glide.with(parent.context)
                    .load(it.src)
                    .apply(RequestOptions()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop())
                    .into(binding.imageViewCommentProfileImg)
            }
        }

        // "더 보기" 유무 판단
        getLineCount()
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
    }

    private fun getLineCount(){
        binding.expandableTextViewReviewcomment.post {
            val count = binding.expandableTextViewReviewcomment.lineCount
            if(count <=2){
                binding.reviewItemToggleButton.visibility =View.INVISIBLE
            }
        }
    }
    private fun settingEnabledButton(view: View,setting:Boolean){
        view.isEnabled =setting
    }

    fun setLike(review:ReviewList,position:Int){

        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                if(check){
                    settingEnabledButton(getViewBinding().imageViewRecommendUpButton,false)

                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .setLike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,review.review_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            likeList[position] =true
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_full)
                            binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_empty)

                            binding.textViewRecommendUpCount.text =
                                GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),1)

                            if(disLikeList[position]){
                                disLikeList[position] =false
                                binding.textViewRecommendDownCount.text =
                                    GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),-1)
                            }

                        },{ t ->
                            CustomDialog.networkErrorDialog(context)
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            Log.e(ErrorManager.REVIEW_LIKE,t.message.toString())
                        }))
                }
                else{
                    CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }


    fun setUnlike(review:ReviewList,position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                if(check){
                    settingEnabledButton(getViewBinding().imageViewRecommendUpButton,false)
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .setUnLike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,review.review_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            likeList[position] =false
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_empty)
                            binding.textViewRecommendUpCount.text =
                                GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),-1)

                        },{ t ->
                            CustomDialog.networkErrorDialog(context)
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,false)
                            Log.e(ErrorManager.REVIEW_UNLIKE,t.message.toString())
                        }))
                }
                else{
                    CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }

    fun setDislike(review:ReviewList,position: Int){

        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                if(check){
                    settingEnabledButton(getViewBinding().imageViewRecommendUpButton,false)
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .setDislike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,review.review_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            disLikeList[position] =true

                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_full)
                            binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_empty)

                            binding.textViewRecommendDownCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),1)

                            if(likeList[position]){
                                likeList[position]=false
                                binding.textViewRecommendUpCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),-1)
                            }
                        },{ t ->
                            CustomDialog.networkErrorDialog(context)
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            Log.e(ErrorManager.REVIEW_DISLIKE,t.message.toString())
                        }))
                }
                else{
                    CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }

    fun setUnDislike(review:ReviewList,position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val check = JWTUtil.checkAccessToken()

            withContext(Dispatchers.Main){
                if(check){
                    settingEnabledButton(getViewBinding().imageViewRecommendUpButton,false)
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .setUnDislike(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                            alcoholId,review.review_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            disLikeList[position] = false

                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_empty)
                            binding.textViewRecommendDownCount.text = GlobalApplication.instance.checkCount(binding.textViewRecommendDownCount.text.toString().toInt(),-1)

                        },{ t ->
                            CustomDialog.networkErrorDialog(context)
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            Log.e(ErrorManager.REVIEW_UNDISLIKE,t.message.toString())
                        }))
                }
                else{
                    CustomDialog.loginDialog(context,GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }
}