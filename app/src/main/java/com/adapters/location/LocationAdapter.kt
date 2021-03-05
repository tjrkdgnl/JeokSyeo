package com.adapters.location

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapters.viewholder.LocationViewHolder
import com.application.GlobalApplication
import com.fragments.signup.location.LocationContract
import com.model.area.AreaList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LocationAdapter(
    private var lst: MutableList<AreaList>,
    private var view: LocationContract.LocationView
) : RecyclerView.Adapter<LocationViewHolder>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(lst[position])

        holder.getViewBinding().locationName.setOnClickListener {
            compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                .getArea(
                    GlobalApplication.userBuilder.createUUID
                    , lst[position].code
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.data?.areaList?.let { list ->
                        setLocation(lst[position])
                        changeList(list.toMutableList())
                    }
                }, {

                })
            )
        }
    }

    override fun getItemCount(): Int = lst.size

    private fun setLocation(areaList: AreaList) {
        when {
            view.getViewModel().locationMap[SignUpViewModel.CITY] == null -> {
                view.getViewModel().locationMap[SignUpViewModel.CITY] = areaList
                view.setCityName()
            }
            view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] == null -> {
                view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] = areaList
                view.setMiddleTownName()
            }
            view.getViewModel().locationMap[SignUpViewModel.SMALL_TOWN] == null -> {
                view.getViewModel().locationMap[SignUpViewModel.SMALL_TOWN] = areaList
                view.setSmallTownName()
            }
        }
    }

    fun changeList(list: MutableList<AreaList>) {
        lst.clear()
        lst.addAll(list)

        if (lst.size == 0) {
            view.getViewModel().buttonState.value = true
        }

        notifyDataSetChanged()
    }
}