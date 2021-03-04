package com.adapters.viewholder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.activities.alcohol_detail.AlcoholDetail
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.alcohol_category.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ViewpagerGridItemBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AlcoholCategoryGridViewHolder(
    val parent: ViewGroup,
    private val executeProgressBar: (Boolean) -> Unit
) : BaseViewHolder<AlcoholList, ViewpagerGridItemBinding>(R.layout.viewpager_grid_item, parent) {
    private var disposable: Disposable?=null

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        data.likeCount?.let {
            binding.textViewGridHeartCount.text = GlobalApplication.instance.checkCount(it)
        }

        data.review?.reviewCount?.let {
            binding.textViewGridCommentCount.text = GlobalApplication.instance.checkCount(it)
        }

        data.viewCount?.let {
            binding.categoryGridEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }

    fun setClickListener(alcoholId: String?) {
        binding.gridItemParentLayout.setOnSingleClickListener {
            executeProgressBar(true)

            disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcoholDetail(
                    GlobalApplication.userBuilder.createUUID,
                    GlobalApplication.userInfo.getAccessToken(), alcoholId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val bundle = Bundle()
                    bundle.putParcelable(GlobalApplication.MOVE_ALCHOL, it.data?.alcohol)

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val intent = Intent(parent.context, AlcoholDetail::class.java)
                        intent.putExtra(GlobalApplication.ALCHOL_BUNDLE, bundle)

                        val pair = Pair.create(
                            binding.gridMainImg as View,
                            binding.gridMainImg.transitionName
                        )

                        val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (parent.context as Activity), pair
                        )

                        parent.context.startActivity(intent, optionCompat.toBundle())
                    } else {
                        GlobalApplication.instance.moveActivity(
                            parent.context,
                            AlcoholDetail::class.java,
                            0,
                            bundle,
                            GlobalApplication.ALCHOL_BUNDLE
                        )
                    }
                    executeProgressBar(false)
                }, { t ->
                    CustomDialog.networkErrorDialog(parent.context)
                    executeProgressBar(false)
                    Log.e(ErrorManager.ALCHOL_DETAIL, t.message.toString())
                })
        }
    }

    fun detach(){
        disposable?.dispose()
    }

    //확장함수로 중복클릭 방지
    private fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        val onSingleClickListener = OneClickListener {
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }
}