package com.adapter.alcholdetail

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholMoreReviewViewHolder
import com.adapter.viewholder.AlcholReviewViewHolder
import com.adapter.viewholder.NoAlcholReviewViewHolder
import com.application.GlobalApplication
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException

class AlcholReviewAdapter(private val context: Context,
                          private val alcholId: String?,
                          private val lst: MutableList<ReviewList>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //좋아요, 싫어요 여부 체크리스트
    private val likeList = mutableListOf<Boolean>()
    private val disLikeList = mutableListOf<Boolean>()

    private val compositeDisposable:CompositeDisposable? = CompositeDisposable()

    //초기에는 detail presenter에서 호출되므로 다음 페이지는 항상 2부터이다.
    private var pageNum =2

    init {
        for(idx  in 0..lst.size){
            likeList.add(false)
            disLikeList.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GlobalApplication.DETAIL_REVIEW_ITEM_0 -> {
                NoAlcholReviewViewHolder(parent)
            }
            GlobalApplication.DETAIL_REVIEW_ITEM_1 -> {
                AlcholReviewViewHolder(context,parent)
            }
            GlobalApplication.DETAIL_REVIEW_ITEM_2 ->{
                AlcholMoreReviewViewHolder(parent)
            }

            else -> throw RuntimeException("알수 없는 viewtype 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlcholReviewViewHolder) {
            holder.bind(lst[position])

            lst[position].has_like?.let {
                if(it){
                    holder.getViewBinding().imageViewRecommendUpButton.setImageResource(R.mipmap.like_full)
                    likeList[position] =true }
            }
            lst[position].has_dislike?.let {
                if(it){
                    holder.getViewBinding().imaveViewRecommendDownButton.setImageResource(R.mipmap.dislike_full)
                    disLikeList[position]=true }
            }

            holder.getViewBinding().imageViewRecommendUpButton.setOnClickListener{
                if(!likeList[position]){
                    likeList[position] =true
                    holder.setLike(alcholId,lst[position],disLikeList,position)
                }
                else{
                    holder.setUnlike(alcholId,lst[position])
                    likeList[position] =false
                }
            }
            holder.getViewBinding().imaveViewRecommendDownButton.setOnClickListener{
                if(!disLikeList[position]){
                    disLikeList[position]=true
                    holder.setDislike(alcholId,lst[position],likeList,position)
                }
                else{
                    holder.setUnDislike(alcholId,lst[position])
                    disLikeList[position]=false
                }
            }
        }
        else if( holder is AlcholMoreReviewViewHolder){
            holder.getViewBinding().reviewMoreParentLayout.setOnClickListener {

            compositeDisposable?.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcholReivew(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcholId, pageNum)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        result.data?.pageInfo?.page?.let {
                            pageNum = it.toInt() + 1
                        }
                        result.data?.reviewList?.let {
                            val currentPosition = lst.size
                            lst.addAll(it.toMutableList())
                            notifyItemChanged(currentPosition-1, lst.size)
                        }
                    }, {}))
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(lst[position].checkMore){
            GlobalApplication.DETAIL_REVIEW_ITEM_0 ->{    return GlobalApplication.DETAIL_REVIEW_ITEM_0}
            GlobalApplication.DETAIL_REVIEW_ITEM_1->{  return GlobalApplication.DETAIL_REVIEW_ITEM_1}
            GlobalApplication.DETAIL_REVIEW_ITEM_2->{return GlobalApplication.DETAIL_REVIEW_ITEM_2}
            else->{GlobalApplication.DETAIL_REVIEW_ITEM_0}
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable?.dispose()
    }
}