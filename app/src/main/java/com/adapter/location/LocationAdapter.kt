package com.adapter.location

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.model.area.AreaList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LocationItemBinding
import com.vuforia.engine.wet.databinding.SplashBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAdapter(private val context: Context, private var lst: MutableList<AreaList>) :
    RecyclerView.Adapter<LocationAdapter.MyViewHolder>() {
    private lateinit var binding: LocationItemBinding
    private lateinit var disposable: Disposable
    var pressedPostion = 0
    override fun onBindViewHolder(holder: LocationAdapter.MyViewHolder, position: Int) {
        holder.bind(lst[position].name.toString())
    }

    override fun getItemCount(): Int = lst.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationAdapter.MyViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.location_item,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    inner class MyViewHolder(private val view: LocationItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        init {
            view.locationName.setOnClickListener { v ->
                pressedPostion = adapterPosition
                if (pressedPostion != RecyclerView.NO_POSITION) {

                    disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                        .getArea(GlobalApplication.userBuilder.createUUID, lst[pressedPostion].code.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            lst.clear()
                            lst.addAll(it.data?.areaList?.toMutableList()!!)
                            Log.e("코루틴 아이템", "tet || " + lst.get(0).name)
                            notifyDataSetChanged()
                            disposable.dispose()
                        }, { t: Throwable? -> t?.stackTrace })

                }
            }
        }

        fun bind(name: String) {
            binding.name = name
        }
    }
}