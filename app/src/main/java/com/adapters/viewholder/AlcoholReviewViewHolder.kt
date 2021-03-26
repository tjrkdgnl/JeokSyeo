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

        //유저들이 평가한 점수들 셋팅
        data.score?.let {
            binding.ratingBarReviewRatingbar.rating = it.toFloat()
        }

        //유저들의 프로필 사진 셋팅
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

        //유저들의 평가한 글 "더보기" 버튼 show / hide 셋팅
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


    //평가한 리뷰 좋아요 클릭 메서드
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
                            //현재 뷰모델(리뷰)을 좋아요했는지에 대한 여부 체크
                            likeList[position] =true
                            settingEnabledButton(getViewBinding().imageViewRecommendUpButton,true)
                            binding.imageViewRecommendUpButton.setImageResource(R.mipmap.like_full)
                            binding.imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_empty)

                            //현재 카운트 +1
                            binding.textViewRecommendUpCount.text =
                                GlobalApplication.instance.checkCount(binding.textViewRecommendUpCount.text.toString().toInt(),1)

                            //해당 리뷰에 "싫어요"가 클릭되어져 있는데 "좋아요" 클릭 할 경우
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

    //유저가 좋아요를 취소했을 경우
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

    //유저가 싫어요를 클릭했을 경우
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

                            //"좋아요"가 클릭되어져 있는 경우에, "싫어요"를 클릭 했을 때
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

    //싫어요를 취소한 경우
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