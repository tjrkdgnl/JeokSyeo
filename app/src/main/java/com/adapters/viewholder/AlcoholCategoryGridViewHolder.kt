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

/**
 * 주류 카테고리 화면에서 grid로 보여질 경우, 각 아이템 뷰홀더
 */
class AlcoholCategoryGridViewHolder(
    val parent: ViewGroup,
    private val executeProgressBar: (Boolean) -> Unit
) : BaseViewHolder<AlcoholList, ViewpagerGridItemBinding>(R.layout.viewpager_grid_item, parent) {
    private var disposable: Disposable?=null

    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

        //각 아이템 마다 좋아요한 개수 셋팅
        data.likeCount?.let {
            binding.textViewGridHeartCount.text = GlobalApplication.instance.checkCount(it)
        }

        //각 아이템 마다 리뷰 개수 셋팅
        data.review?.reviewCount?.let {
            binding.textViewGridCommentCount.text = GlobalApplication.instance.checkCount(it)
        }

        //각 아이템 마다 조회수 개수 셋팅
        data.viewCount?.let {
            binding.categoryGridEyeCount.text = GlobalApplication.instance.checkCount(it)
        }
    }

    //유저가 아이템을 클릭할 경우 해당아이템의 주류상세 화면으로 이동
    fun setClickListener(alcoholId: String?) {
        binding.gridItemParentLayout.setOnSingleClickListener {
            executeProgressBar(true)

            //유저가 선택한 주류에 대한 정보를 서버로부터 받아옴
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

                        //화면 전환을 위한 transition 애니메이션 적용
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