package com.adapter.alcohol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcoholNoRatedViewHolder
import com.adapter.viewholder.AlcoholRatedViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.fragment.alcohol_rated.Fragment_alcoholRated
import com.jeoksyeo.wet.activity.alcohol_rated.AlcoholRated
import com.jeoksyeo.wet.activity.comment.Comment
import com.model.rated.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AlcoholRatedAdapter(private val context: Context,
                         private val lst:MutableList<ReviewList>,
                         private val smoothScrollPosition: Fragment_alcoholRated.SmoothScrollListener,
                          private val progressbar:(Boolean) ->Unit
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val checkList = mutableListOf<Boolean>()
    private val ITEM = 1
    private val NO_ITEM=0
    private val compositeDisposable =CompositeDisposable()
    private lateinit var viewModel : RatedViewModel
    init {
        for(i in lst){
            checkList.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(context is AlcoholRated){
            viewModel = ViewModelProvider(context).get(RatedViewModel::class.java)
        }

        return when(viewType){
            ITEM ->{ AlcoholRatedViewHolder(parent) }
            NO_ITEM ->{ AlcoholNoRatedViewHolder(parent) }
            else ->{throw RuntimeException("알수 없는 뷰타입 에러")}
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AlcoholRatedViewHolder){
            holder.bind(lst[position])

            //리뷰 접고 펼치기
            holder.getViewBinding().ratedItemExpandableButton.setOnClickListener{
                if(!checkList[position] && !holder.getViewBinding().ratedItemComment.isExpanded){
                    checkList[position] =true
                    holder.getViewBinding().ratedItemComment.expand()
                    smoothScrollPosition.moveScroll(position)
                    holder.getViewBinding().ratedItemArrow.setImageResource(R.mipmap.up_errow)
                    holder.getViewBinding().ratedItemExpandableText.text = "접기"
                }
                else{
                    checkList[position] =false
                    holder.getViewBinding().ratedItemComment.collapse()
                    holder.getViewBinding().ratedItemArrow.setImageResource(R.mipmap.down_errow)
                    holder.getViewBinding().ratedItemExpandableText.text = "펼치기"

                }
            }

            //리뷰 삭제
            holder.getViewBinding().ratedItemDelete.setOnClickListener{
                val dialog = CustomDialog.createCustomDialog(context)
                val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
                val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
                val contents = dialog.findViewById<TextView>(R.id.dialog_contents)

                contents.text = lst[position].alcohol?.name.toString() +"에 대한 리뷰를 삭제하시겠습니까?"
                okButton.text = "삭제"

                okButton.setOnClickListener {
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .deleteMyRatedReview(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                        lst[position].alcohol?.alcoholId,lst[position].reviewId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            dialog.dismiss()
                            lst.removeAt(position)

                            viewModel.reviewCount.value = lst.size

                            if(lst.size ==0){
                                lst.add(ReviewList())
                            }

                           notifyDataSetChanged()
                        },{
                            t->Log.e("내가 평가한 리뷰 삭제 에러",t.message.toString())
                        }))
                }
                cancelButton.setOnClickListener {  dialog.dismiss() }
            }

            //리뷰 수정
            //comment 화면에 보여질 주류 정보를 얻기
            holder.getViewBinding().ratedItemCommentEdit.setOnClickListener {
                progressbar(true)
                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(GlobalApplication.userBuilder.createUUID
                        ,GlobalApplication.userInfo.getAccessToken(), lst[position].alcohol?.alcoholId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ alcohol->
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,alcohol.data?.alcohol)

                        //내가 남긴 코멘트 정보 가져오기
                        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                            .getCommentOfAlcohol(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                                lst[position].alcohol?.alcoholId!!,lst[position].reviewId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({comment->
                                progressbar(false)

                                bundle.putParcelable(GlobalApplication.MOVE_MY_COMMENT,comment.data?.review)
                                GlobalApplication.instance.moveActivity(context, Comment::class.java
                                    ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                            },{t ->
                                progressbar(false)
                                Log.e(ErrorManager.MY_COMMENT,t.message.toString()) }))
                    },{ t ->
                        progressbar(false)
                        Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString()) }))
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
       return lst[position].reviewId?.let { ITEM } ?: NO_ITEM
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    fun updateItem(list:MutableList<ReviewList>){
        for(i in 0 until list.size){ //페이징이 되면 체크 리스트 또한 증가해야 한다.
            checkList[i] = false
        }

        val currentPosition = lst.size
        lst.addAll(list)
        notifyItemChanged(currentPosition-1,list.size)
    }
}