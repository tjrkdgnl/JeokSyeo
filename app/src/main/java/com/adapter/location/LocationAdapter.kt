package com.adapter.location

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.LocationViewHolder
import com.application.GlobalApplication
import com.error.ErrorManager
import com.fragment.login.location.LocationInterface
import com.model.area.AreaList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LocationAdapter(
    private var lst: MutableList<AreaList>,
    private var locationInterface: LocationInterface
) : RecyclerView.Adapter<LocationViewHolder>() {
    private lateinit var disposable: Disposable
    private var viewmodel: SignUpViewModel = locationInterface.getViewModel()
    var depth = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(lst[position])

        holder.getViewBinding().locationName.setOnClickListener { v ->
                        var pressedPostion = position
                    if (pressedPostion != RecyclerView.NO_POSITION) {
                        when (depth) {
                            0 -> {viewmodel.stateArea.value = lst[pressedPostion]}

                            1 ->{viewmodel.countryArea.value =  lst[pressedPostion]
                                viewmodel.depth = 1}

                            2 -> {viewmodel.townArea.value = lst[pressedPostion]
                                viewmodel.depth =2}
                }

                if (!viewmodel.lock) {
                    disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                        .getArea(
                            GlobalApplication.userBuilder.createUUID,
                            lst[pressedPostion].code
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.data?.areaList?.size != 0)
                                it.data?.areaList?.let { list ->
                                    changeList(list.toMutableList())
                                    depth +=1
                                }
                            else {
                                viewmodel.lock=true
                            }
                        }, { t -> Log.e(ErrorManager.LOCATION,t.message.toString())})
                }
            }
        }
    }

    override fun getItemCount(): Int = lst.size


    fun changeList(list: MutableList<AreaList>) {
        lst.clear()
        lst.addAll(list)
        notifyDataSetChanged()
    }

    fun updateList(code: String?) {
        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(
                GlobalApplication.userBuilder.createUUID,
                code
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.data?.areaList?.size != 0)
                    it.data?.areaList?.let { list ->
                        if(list[0].code != lst[0].code)
                            changeList(list.toMutableList())
                    }

            }, { t -> t.stackTrace })
    }
}