package com.adapter.viewholder

import android.util.Log
import android.view.ViewGroup
import com.application.GlobalApplication
import com.base.BaseViewHolder
import com.error.ErrorManager
import com.model.favorite.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteItemBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoriteViewHolder(val compositeDisposable: CompositeDisposable, parent: ViewGroup) :
    BaseViewHolder<AlcoholList, FavoriteItemBinding>(R.layout.favorite_item, parent) {
    private var isLiked = true


    override fun bind(data: AlcoholList) {
        binding.alcohol = data
        binding.executePendingBindings()

//        if (isLiked) {
//            binding.imageViewFavoriteHeart.setOnClickListener {
//                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
//                    .cancelAlcoholLike(
//                        GlobalApplication.userBuilder.createUUID,
//                        GlobalApplication.userInfo.getAccessToken(),
//                        data.alcoholId
//                    )
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        binding.imageViewFavoriteHeart.setImageResource(R.mipmap.small_heart_empty)
//                        isLiked = false
//                    }, { t ->
//                        Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString())
//                    })
//                )
//            }
//        } else {
//            binding.imageViewFavoriteHeart.setOnClickListener {
//                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
//                    .alcoholLike(
//                        GlobalApplication.userBuilder.createUUID,
//                        GlobalApplication.userInfo.getAccessToken(),
//                        data.alcoholId
//                    )
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        binding.imageViewFavoriteHeart.setImageResource(R.mipmap.small_heart_full)
//                        isLiked = true
//
//                    }, {
//
//                            t ->
//                        Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString())
//                    })
//                )
//            }
//        }

    }
}