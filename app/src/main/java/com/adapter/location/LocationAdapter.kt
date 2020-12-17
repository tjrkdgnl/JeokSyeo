package com.adapter.location

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adapter.viewholder.LocationViewHolder
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.fragment.login.location.LocationInterface
import com.model.area.AreaList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LocationAdapter(
    private var lst: MutableList<AreaList>,
    private var locationInterface: LocationInterface
) : RecyclerView.Adapter<LocationViewHolder>() {
    private var compositDisposable = CompositeDisposable()
    private var viewmodel: SignUpViewModel = locationInterface.getViewModel()
    var depth = 0
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        context = parent.context
        return LocationViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(lst[position])

        holder.getViewBinding().locationName.setOnClickListener {
            viewmodel.OkButtonEnabled = false

            if (position != RecyclerView.NO_POSITION) {
                when (depth) {
                    0 -> {
                        viewmodel.stateArea.value = lst[position]
                    }

                    1 -> {
                        viewmodel.countryArea.value = lst[position]
                        viewmodel.depth = 1
                    }

                    2 -> {
                        viewmodel.townArea.value = lst[position]
                        viewmodel.depth = 2
                    }
                }

                compositDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                    .getArea(
                        GlobalApplication.userBuilder.createUUID,
                        lst[position].code
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.data?.areaList?.size != 0) //아직 depth가 남았으면
                            it.data?.areaList?.let { list ->
                                changeList(list.toMutableList())
                                depth += 1
                            }
                        else { //더 이상의 depth가 없다면 ok 버튼 활성화
                            viewmodel.OkButtonEnabled = true
                        }
                    }, { t ->
                        CustomDialog.networkErrorDialog(context)
                        Log.e(ErrorManager.LOCATION, t.message.toString())
                    })
                )
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
        compositDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(
                GlobalApplication.userBuilder.createUUID,
                code
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.data?.areaList?.size != 0)
                    it.data?.areaList?.let { list ->
                        if (list[0].code != lst[0].code)
                            changeList(list.toMutableList())
                    }

            }, { t ->
                CustomDialog.networkErrorDialog(context)
                t.stackTrace
            })
        )
    }
}