package com.adapters.alcohol_category

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.activities.alcohol_detail.AlcoholDetail
import com.adapters.viewholder.AlcoholCategoryListViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.alcohol_category.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ListAdapter(private val context: Context,
                  private val lst:MutableList<AlcoholList>,
                  private val executeProgressBar:(Boolean)->Unit
):RecyclerView.Adapter<AlcoholCategoryListViewHolder>() {

    private var disposable: Disposable? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoholCategoryListViewHolder {
        return AlcoholCategoryListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcoholCategoryListViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarListRatingbar.rating = lst[position].review?.score!!

        //아이템을 클릭할 경우 해당 주류 상세 페이지로 이동
        holder.getViewBinding().listItemParentLayout.setOnSingleClickListener{
            executeProgressBar(true)
            lst[position].alcoholId?.let {alcholId->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alcohol)


                        //화면 전환에 필요한 trasition 애니메이션 적용
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            val intent = Intent(context,AlcoholDetail::class.java)
                            intent.putExtra(GlobalApplication.ALCHOL_BUNDLE,bundle)
                            val pair = Pair.create(
                                holder.getViewBinding().listMainImage as View,
                                holder.getViewBinding().listMainImage.transitionName
                            )

                            val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (context as Activity), pair)

                            context.startActivity(intent, optionCompat.toBundle())
                        }
                        else{
                            GlobalApplication.instance.moveActivity(context,AlcoholDetail::class.java
                                ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                        }

                        executeProgressBar(false)

                    },{t->
                        CustomDialog.networkErrorDialog(context)
                        executeProgressBar(false)
                        Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString())})
            }
        }

        //해당 주류를 찜한 적이 있다면 표시
        lst[position].isLiked?.let {
            if(it ){
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.list_heart_full)
            }
            else{
                holder.getViewBinding().imageViewListHeart.setImageResource(R.mipmap.list_heart_empty)

            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    //페이징 처리로 인해 데이터를 업데이트 할 시, 추가될 페이지의 데이터가 중복되는지 확인 후 업데이트
    fun updateList(list:MutableList<AlcoholList>){
        var duplicate =false
        val newlist = mutableListOf<AlcoholList>()
        val currentSize = lst.size

        for(newData in list.withIndex()){
            for(previousData in lst){
                if(newData.value.alcoholId == previousData.alcoholId){
                    duplicate =true
                    break
                }
            }

            if(!duplicate){
                newlist.add(newData.value)
            }

            duplicate=false
        }
        lst.addAll(newlist)
        notifyItemChanged(currentSize,newlist.size)
    }

    //정렬 기준 변경
    fun changeSort(list:MutableList<AlcoholList>){
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    //api 호출에 사용된 객체들 메모리 할당해제
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }

    //중복 클릭을 방지하기 위해서 확장함수 선언
    fun View.setOnSingleClickListener(onSingleClick:(View)->Unit){
        val onSingleClickListener =OneClickListener{
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }

}