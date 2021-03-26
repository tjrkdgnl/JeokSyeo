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
import com.adapters.viewholder.AlcoholCategoryGridViewHolder
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

class GridAdapter(
    private val lst: MutableList<AlcoholList>,
    private val executeProgressBar: (Boolean) -> Unit
) : RecyclerView.Adapter<AlcoholCategoryGridViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlcoholCategoryGridViewHolder {
        return AlcoholCategoryGridViewHolder(parent,executeProgressBar)
    }

    override fun onBindViewHolder(holder: AlcoholCategoryGridViewHolder, position: Int) {
        holder.bind(lst[position])

        //아이템의 rating 점수 셋팅
        holder.getViewBinding().ratingBarGridRatingbar.rating = lst[position].review?.score!!

        //아이템 클릭 리스너 셋팅
        holder.setClickListener(lst[position].alcoholId)

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


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

    }
}