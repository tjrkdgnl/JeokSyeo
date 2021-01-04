package com.adapter.main

import android.annotation.SuppressLint
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
import com.adapter.viewholder.AlcoholRankViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.custom.OneClickListener
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.model.alcohol_ranking.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlcoholRankAdapter(
    private val context: Context,
    private val lst:MutableList<AlcoholList>
) : RecyclerView.Adapter<AlcoholRankViewHolder>() {

    private var disposable: Disposable? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcoholRankViewHolder {
        return AlcoholRankViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlcoholRankViewHolder, position: Int) {
        holder.bind(lst[position])
        holder.getViewBinding().rankingText.text = (position+1).toString()

        if(position == lst.size-1){
            holder.getViewBinding().monthlyBoundary.visibility = View.INVISIBLE
        }

        holder.getViewBinding().alcoholRankParentLayout.setOnSingleClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                JWTUtil.checkToken()

                withContext(Dispatchers.Main){
                    lst[position].alcoholId?.let {alcoholId->
                        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                            .getAlcoholDetail(GlobalApplication.userBuilder.createUUID,
                                GlobalApplication.userInfo.getAccessToken(), alcoholId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val bundle = Bundle()
                                bundle.putParcelable(GlobalApplication.MOVE_ALCHOL,it.data?.alcohol)


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    val intent = Intent(context, AlcoholDetail::class.java)
                                    intent.putExtra(GlobalApplication.ALCHOL_BUNDLE,bundle)
                                    val pair = androidx.core.util.Pair.create(
                                        holder.getViewBinding().rankMainImage as View, holder.getViewBinding().rankMainImage.transitionName)

                                    val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        (context as Activity), pair)

                                    context.startActivity(intent, optionCompat.toBundle())
                                }
                                else{
                                    GlobalApplication.instance.moveActivity(context,AlcoholDetail::class.java
                                        ,0,bundle,GlobalApplication.ALCHOL_BUNDLE)
                                }
                            },{t->
                                CustomDialog.networkErrorDialog(context)
                                Log.e(ErrorManager.ALCHOL_DETAIL,t.message.toString())})
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposable?.dispose()
    }

    fun View.setOnSingleClickListener( onSingleClick:(View)->Unit){
        val onSingleClickListener = OneClickListener{
            onSingleClick(it)
        }
        setOnClickListener(onSingleClickListener)
    }
}