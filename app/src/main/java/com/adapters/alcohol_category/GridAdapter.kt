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
import com.adapters.viewholder.AlcoholCategoryGridViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.activities.alcohol_detail.AlcoholDetail
import com.model.alcohol_category.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GridAdapter(
    private val context: Context,
    private val lst: MutableList<AlcoholList>,
    private val executeProgressBar: (Boolean) -> Unit
) : RecyclerView.Adapter<AlcoholCategoryGridViewHolder>() {
    private var sum: Int = 0
    private var disposable: Disposable? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlcoholCategoryGridViewHolder {
        return AlcoholCategoryGridViewHolder(parent)
    }

    override fun onBindViewHolder(holder: AlcoholCategoryGridViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().ratingBarGridRatingbar.rating = lst[position].review?.score!!

        holder.getViewBinding().gridItemParentLayout.setOnSingleClickListener {
            executeProgressBar(true)
            lst[position].alcoholId?.let { alcholId ->
                disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val bundle = Bundle()
                        bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, it.data?.alcohol)

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            val intent = Intent(context, AlcoholDetail::class.java)
                            intent.putExtra(GlobalApplication.ALCHOL_BUNDLE, bundle)

                            val pair = Pair.create(
                                holder.getViewBinding().gridMainImg as View,
                                holder.getViewBinding().gridMainImg.transitionName
                            )

                            val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (context as Activity), pair)

                            context.startActivity(intent, optionCompat.toBundle())
                        }
                        else{
                            GlobalApplication.instance.moveActivity(
                                context,
                                AlcoholDetail::class.java,
                                0,
                                bundle,
                                GlobalApplication.ALCHOL_BUNDLE
                            )
                        }
                        executeProgressBar(false)
                    }, { t ->
                        CustomDialog.networkErrorDialog(context)
                        executeProgressBar(false)
                        Log.e(ErrorManager.ALCHOL_DETAIL, t.message.toString())
                    })
            }
        }
        lst[position].isLiked?.let {
            if (it) {
                //detail 창에서 좋아요 한 후, 화면 갱신을 위한 코드도 포함해야 함.
                holder.getViewBinding().imageViewGridHeart.setImageResource(R.mipmap.small_heart_full)
            } else {
                holder.getViewBinding().imageViewGridHeart.setImageResource(R.mipmap.small_heart_empty)
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    //페이징을 통한 새로운 리스트의 아이템이 기존의 아이템과 중복되는지 확인 후, 리스트에 추가

    fun updateList(list: MutableList<AlcoholList>) {
        var duplicate = false
        val newList = mutableListOf<AlcoholList>()
        newList.clear()

        val currentItemCount = lst.size
        for (newData in list.withIndex()) {
            for (previousData in lst) {
                if (newData.value.alcoholId == previousData.alcoholId) {
                    duplicate = true
                    break
                }
            }
            if (!duplicate)
                newList.add(newData.value)

            duplicate = false
        }

        lst.addAll(newList)
        notifyItemChanged(currentItemCount, newList.size)

    }

    //정렬이 변경될 때 리스트 내부의 아이템들을 모두 교체
    fun changeSort(list: MutableList<AlcoholList>) {
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    //페이징처리를 위해서 맨 마지막 주류 id값을 리턴
    fun getLastAlcoholId(): String? {
        if (lst.size > 1)
            return lst.get(lst.size - 1).alcoholId
        else
            return null
    }

    //확장함수로 중복클릭 방지
    fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        val onSingleClickListener = OneClickListener {
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }
}