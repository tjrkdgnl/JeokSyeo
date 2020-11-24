package com.adapter.alcoholdetail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcoholMoreReviewViewHolder
import com.adapter.viewholder.AlcoholReviewViewHolder
import com.adapter.viewholder.NoAlcoholReviewViewHolder
import com.application.GlobalApplication
import com.custom.OneClickListener
import com.model.review.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException

class AlcoholReviewAdapter(private val context: Context,
                          private val alcoholId: String?,
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
            GlobalApplication.DETAIL_NO_REVIEW -> {
                NoAlcoholReviewViewHolder(parent)
            }
            GlobalApplication.DETAIL_REVIEW -> {
                AlcoholReviewViewHolder(context,parent)
            }
            GlobalApplication.DETAIL_MORE_REVIEW ->{
                AlcoholMoreReviewViewHolder(parent)
            }

            else -> throw RuntimeException("알수 없는 viewtype 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlcoholReviewViewHolder) {
            holder.bind(lst[position])

            //리뷰에 대한 좋아요,싫어요 표시 셋팅
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

            //좋아요를 눌렀을 때
            holder.getViewBinding().imageViewRecommendUpButton.setUpDownClickListener{
                if(!likeList[position]){
                    likeList[position] =true
                    holder.setLike(alcoholId,lst[position],disLikeList,position)
                }
                else{
                    holder.setUnlike(alcoholId,lst[position])
                    likeList[position] =false
                }
            }
            //싫어요를 눌렀을 때
            holder.getViewBinding().imaveViewRecommendDownButton.setUpDownClickListener{
                if(!disLikeList[position]){
                    disLikeList[position]=true
                    holder.setDislike(alcoholId,lst[position],likeList,position)
                }
                else{
                    holder.setUnDislike(alcoholId,lst[position])
                    disLikeList[position]=false
                }
            }
        }
        //리뷰 더 보기
        else if( holder is AlcoholMoreReviewViewHolder){
            holder.getViewBinding().reviewMoreParentLayout.setOnClickListener {

            compositeDisposable?.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholReivew(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcoholId, pageNum)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        result.data?.pageInfo?.page?.let {
                            pageNum = it.toInt() + 1
                        }
                        result.data?.reviewList?.let {
                            if(it.isNotEmpty()){ //리뷰가 있을 때
                                updateList(it.toMutableList())
                            }
                            else{ //리뷰가 없을 때,
                                val lastIdx = lst.size-1
                                lst.removeAt(lastIdx)
                                notifyItemRemoved(lastIdx)
                            }
                        }
                    }, {}))
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    //리뷰 더 보기 눌렀을 때, 리스트 업데이트
    private fun updateList(list:MutableList<ReviewList>){
        lst.removeAt(lst.size-1) // 더보기 칸 지우기
        var duplicateCheck =false
        val size = lst.size

        for(newData in list){
            for(j in lst){
                if(newData.review_id == j.review_id){
                    duplicateCheck=true
                    break
                }
            }
            if(!duplicateCheck){
                lst.add(newData)
            }
            duplicateCheck=false
        }
        lst.add(ReviewList().apply { checkMore=GlobalApplication.DETAIL_MORE_REVIEW })
        notifyItemChanged(size,lst.size)
    }

    override fun getItemViewType(position: Int): Int {
        return when(lst[position].checkMore){
            0 ->{    return GlobalApplication.DETAIL_NO_REVIEW}
            1->{  return GlobalApplication.DETAIL_REVIEW}
            2->{return GlobalApplication.DETAIL_MORE_REVIEW}
            else->{GlobalApplication.DETAIL_NO_REVIEW}
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable?.dispose()
    }

    private fun View.setUpDownClickListener(onClick:(View)->Unit){
        val oneClick = OneClickListener{
            onClick(it)
        }
        setOnClickListener(oneClick)
    }

}