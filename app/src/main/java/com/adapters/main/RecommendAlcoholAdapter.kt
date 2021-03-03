package com.adapters.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.activities.alcohol_detail.AlcoholDetail
import com.adapters.viewholder.RecommendAlcoholViewHolder
import com.adapters.viewholder.RecommendEmptyAlcoholViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.model.recommend_alcohol.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecommendAlcoholAdapter(
    private val context: Context,
    private var lst: MutableList<AlcoholList>,
    private val progressbar:(Boolean)->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var compositDisposable = CompositeDisposable()
    private var likeList = mutableListOf<Boolean>()

    init {
        for (item in lst) {
            item.isLiked?.let { like ->
                likeList.add(like)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            -1 -> {
                RecommendEmptyAlcoholViewHolder(parent)
            }
            1 -> {
                RecommendAlcoholViewHolder(parent)
            }
            else -> {
                throw RuntimeException("알 수 없는 뷰타입 에러")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecommendAlcoholViewHolder) {
            holder.bind(lst.get(position))

            holder.getViewBinding().activityMainRecommendImg.setOnSingleClickListener {

                CoroutineScope(Dispatchers.IO).launch {
                    JWTUtil.checkAccessToken()

                    withContext(Dispatchers.Main) {
                        progressbar(true)
                        moveActivity(holder,position)
                    }
                }
            }

            holder.getViewBinding().activtyMainLikeImg.setOnSingleClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val check = JWTUtil.checkAccessToken()

                    withContext(Dispatchers.Main) {
                        if (check) {
                            if (!likeList[position]) {
                                recommendAlcoholLike(holder, position)

                            } else {
                                recommendAlcoholCancelLike(holder, position)
                            }
                        }
                        else{
                            CustomDialog.loginDialog(context,0,false)
                        }
                    }
                }
            }
        }
    }

    private fun moveActivity(holder:RecommendAlcoholViewHolder,position: Int){
        lst[position].alcoholId?.let { alcoholId ->
            compositDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .getAlcoholDetail(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(), alcoholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            val bundle = Bundle()
                            bundle.putParcelable(
                                GlobalApplication.MOVE_ALCHOL,
                                it.data?.alcohol
                            )

                            progressbar(false)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                val intent = Intent(context, AlcoholDetail::class.java)
                                intent.putExtra(GlobalApplication.ALCHOL_BUNDLE,bundle)
                                val pair = androidx.core.util.Pair.create(
                                    holder.getViewBinding().activityMainRecommendImg as View, holder.getViewBinding().activityMainRecommendImg.transitionName)

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
                        },
                        { t ->
                            CustomDialog.networkErrorDialog(context)
                            Log.e(
                                ErrorManager.ALCHOL_DETAIL,
                                t.message.toString()
                            )
                        })
            )
        }
    }

    private fun recommendAlcoholLike(holder: RecommendAlcoholViewHolder, position: Int) {
        lst[position].alcoholId.let { alcoholId ->
            compositDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .alcoholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcoholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        likeList[position] = true
                        holder.getViewBinding().activityMainLikeCount.text =
                            GlobalApplication.instance.checkCount(
                                holder.getViewBinding().activityMainLikeCount.text.toString()
                                    .toInt(), 1
                            )
                        holder.getViewBinding().activtyMainLikeImg.setImageResource(
                            R.mipmap.recommend_heart_full
                        )
                    }, { t ->
                        CustomDialog.networkErrorDialog(context)
                        Log.e(
                            ErrorManager.ALCHOL_LIKE,
                            t.message.toString()
                        )
                    })
            )
        }
    }

    fun recommendAlcoholCancelLike(holder: RecommendAlcoholViewHolder, position: Int) {
        lst[position].alcoholId?.let { alcoholId ->
            compositDisposable.add(
                ApiGenerator.retrofit.create(ApiService::class.java)
                    .cancelAlcoholLike(
                        GlobalApplication.userBuilder.createUUID,
                        GlobalApplication.userInfo.getAccessToken(),
                        alcoholId
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        likeList[position] = false
                        holder.getViewBinding().activityMainLikeCount.text =
                            GlobalApplication.instance.checkCount(
                                holder.getViewBinding().activityMainLikeCount.text.toString()
                                    .toInt(), -1
                            )
                        holder.getViewBinding().activtyMainLikeImg.setImageResource(
                            R.mipmap.recommend_heart_empty
                        )
                    }, { t ->
                        CustomDialog.networkErrorDialog(context)
                        Log.e(
                            ErrorManager.ALCHOL_CANCEL_LIKE,
                            t.message.toString()
                        )
                    })
            )
        }
    }


    fun updateList(list: MutableList<AlcoholList>) {
        lst.clear()
        lst.addAll(list)
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositDisposable.dispose()

    }

    override fun getItemViewType(position: Int): Int {
        return if (lst[position].type != 1) -1 else 1
    }

    fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        val setOnSingleClickListener = OneClickListener {
            onSingleClick(it)
        }
        setOnClickListener(setOnSingleClickListener)
    }

}