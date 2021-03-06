package com.adapter.favorite

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.FavoriteViewHolder
import com.adapter.viewholder.NoFavoriteViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.model.favorite.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoriteAdapter(private var lst: MutableList<AlcoholList>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val compositeDisposable = CompositeDisposable()
    private var likeCheckList = mutableListOf<Boolean>()
    private lateinit var context: Context

    init {
        for (i in lst.indices) {
            likeCheckList.add(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            1 -> {
                FavoriteViewHolder(parent)
            }
            -1 -> {
                NoFavoriteViewHolder(parent)
            }
            else -> {
                throw RuntimeException("알 수 없는 뷰타입 에러")
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavoriteViewHolder) {
            holder.bind(lst[position])

            holder.getViewBinding().imageViewFavoriteHeart.setOnClickListener {
                if (likeCheckList[position]) {
                    likeCheckList[position] = false

                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .cancelAlcoholLike(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(), lst[position].alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                holder.getViewBinding().imageViewFavoriteHeart.setImageResource(
                                    R.mipmap.favorite_heart_empty)
                                likeCheckList[position] = false
                            }, { t ->
                                CustomDialog.networkErrorDialog(context)
                                Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString())
                            })
                    )
                } else {
                    holder.getViewBinding().imageViewFavoriteHeart.isEnabled =false
                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .alcoholLike(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(), lst[position].alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                holder.getViewBinding().imageViewFavoriteHeart.isEnabled =true
                                holder.getViewBinding().imageViewFavoriteHeart.setImageResource(
                                    R.mipmap.favorite_heart_full)
                                likeCheckList[position] = true
                            }, { t ->
                                CustomDialog.networkErrorDialog(context)
                                holder.getViewBinding().imageViewFavoriteHeart.isEnabled =true
                                Log.e(ErrorManager.ALCHOL_CANCEL_LIKE, t.message.toString())
                            })
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].type
    }

    fun pageUpdate(list: MutableList<AlcoholList>) {
        for(idx in list.indices){ //페이징 처리되는 것 만큼 좋아요 체크 리스트도 개수 증가
            likeCheckList.add(true)
        }

        val idx = lst.size

        lst.addAll(list)
        notifyItemChanged(idx, lst.size)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        compositeDisposable.dispose()
    }
}