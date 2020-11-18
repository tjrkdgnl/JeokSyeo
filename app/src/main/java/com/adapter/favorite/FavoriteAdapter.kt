package com.adapter.favorite

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.FavoriteViewHolder
import com.adapter.viewholder.NoFavoriteViewHolder
import com.application.GlobalApplication
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

    init {
        for (i in lst.indices) {
            likeCheckList.add(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                FavoriteViewHolder(compositeDisposable, parent)
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
                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .cancelAlcoholLike(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(), lst[0].alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                holder.getViewBinding().imageViewFavoriteHeart.setImageResource(
                                    R.mipmap.small_heart_empty)
                                likeCheckList[position] = false
                            }, { t ->
                                Log.e(ErrorManager.ALCHOL_LIKE, t.message.toString())
                            })
                    )
                } else {
                    compositeDisposable.add(
                        ApiGenerator.retrofit.create(ApiService::class.java)
                            .alcoholLike(
                                GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(), lst[0].alcoholId
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                holder.getViewBinding().imageViewFavoriteHeart.setImageResource(
                                    R.mipmap.small_heart_full)
                                likeCheckList[position] = true
                            }, { t ->
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
        val idx = lst.size

        lst.addAll(list)
        notifyItemChanged(idx, lst.size)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        compositeDisposable.dispose()
    }
}