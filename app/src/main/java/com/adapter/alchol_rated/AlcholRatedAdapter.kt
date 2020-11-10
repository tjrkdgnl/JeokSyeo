package com.adapter.alchol_rated

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.AlcholNoRatedViewHolder
import com.adapter.viewholder.AlcholRatedViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.fragment.alchol_rated.Fragment_alcholRated
import com.jeoksyeo.wet.activity.comment.Comment
import com.model.rated.ReviewList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException

class AlcholRatedAdapter(private val context: Context,
                         private val lst:MutableList<ReviewList>,
                         private val smoothScrollPosition: Fragment_alcholRated.SmoothScrollListener
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val checkList = mutableListOf<Boolean>()
    private val ITEM = 1
    private val NO_ITEM=0
    private val compositeDisposable =CompositeDisposable()

    init {
        for(i in lst){
            checkList.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM ->{ AlcholRatedViewHolder(parent) }
            NO_ITEM ->{ AlcholNoRatedViewHolder(parent) }
            else ->{throw RuntimeException("알수 없는 뷰타입 에러")}
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AlcholRatedViewHolder){
            holder.bind(lst[position])

            //리뷰 접고 펼치기
            holder.getViewBinding().ratedItemExpandableButton.setOnClickListener{
                if(!checkList[position] && !holder.getViewBinding().ratedItmeComment.isExpanded){
                    checkList[position] =true
                    holder.getViewBinding().ratedItmeComment.expand()
                    smoothScrollPosition.moveScroll(position)
                }
                else{
                    checkList[position] =false
                    holder.getViewBinding().ratedItmeComment.collapse()
                }
            }

            //리뷰 삭제
            holder.getViewBinding().ratedItemDelete.setOnClickListener{
                val dialog = CustomDialog.createCustomDialog(context)
                val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
                val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
                val contents = dialog.findViewById<TextView>(R.id.dialog_contents)

                contents.text = lst[position].alchol?.name.toString() +"에 대한 리뷰를 삭제하시겠습니까?"
                okButton.text = "삭제"

                okButton.setOnClickListener {
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .deleteMyRatedReview(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                        lst[position].alchol?.alcholId,lst[position].reviewId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            dialog.dismiss()
                            lst.removeAt(position)

                            if(lst.size ==0){
                                lst.add(ReviewList())
                            }

                           notifyDataSetChanged()
                        },{
                            t->Log.e("내가 평가한 리뷰 삭제 에러",t.message.toString())
                        }))
                }
                cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
            }

            //리뷰 수정
            holder.getViewBinding().ratedItemCommentEdit.setOnClickListener{
                val dialog = CustomDialog.createCustomDialog(context)
                val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
                val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
                val contents = dialog.findViewById<TextView>(R.id.dialog_contents)

                contents.text = lst[position].alchol?.name.toString() +"에 대한 리뷰를 수정하시겠습니까?"
                okButton.text = "수정"

                okButton.setOnClickListener {
                    compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .getAlcholDetail(GlobalApplication.userBuilder.createUUID
                            ,GlobalApplication.userInfo.getAccessToken(), lst[position].alchol?.alcholId!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ alchol->
                            val bundle = Bundle()
                            bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,alchol.data?.alchol)

                            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                                .getCommentOfAlchol(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
                                    lst[position].alchol?.alcholId!!,lst[position].reviewId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({comment->
                                    bundle.putParcelable(GlobalApplication.MOVE_MY_COMMENT,comment.data?.review)
                                    GlobalApplication.instance.moveActivity(context, Comment::class.java
                                        ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                                },{t ->
                                    Log.e(ErrorManager.MY_COMMENT,t.message.toString()) }))
                        },{ t ->
                            Log.e(com.error.ErrorManager.ALCHOL_DETAIL,t.message.toString())
                        }))
                }
                cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
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
        val currentPosition = lst.size
        lst.addAll(list)
        notifyItemChanged(currentPosition-1,list.size)
    }
}