package com.adapter.location

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.GlobalApplication
import com.fragment.location.LocationInterface
import com.model.area.AreaList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.BR
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LocationItemBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LocationAdapter(
    private var lst: MutableList<AreaList>,
    private var locationInterface: LocationInterface
) : RecyclerView.Adapter<LocationAdapter.MyViewHolder>() {
    private lateinit var binding: LocationItemBinding
    private lateinit var disposable: Disposable
    private var viewmodel: SignUpViewModel = locationInterface.getViewModel()
    var depth = 0

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
                var pressedPostion = adapterPosition
                if (pressedPostion != RecyclerView.NO_POSITION) {
                    when (depth) {
                        0 -> viewmodel.stateArea.value = lst[pressedPostion]

                        1 -> viewmodel.countryArea.value =  lst[pressedPostion]

                        2 -> viewmodel.townArea.value = lst[pressedPostion]
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
                            }, { t -> t.stackTrace })
                    }
                }
            }
        }

        fun bind(name: String) {
            //view.name = name으로 직접 연결한다면 반드시 executePendingBindings을 통해서 결합을 시켜줘야한다.
            //안그러면.... 갱신이 제대로 이뤄지지않는다...
            //이유는 뷰 업데이트가 일어날 경우, 다음 프레임에 view를 그리는 계산을 미루게 되면 잘못 측정된 값으로
            //업데이트를 시킨다. 때문에 executePendingBindings과 setVariable을 이용하여 사용하여 동기적으로 업데이트하여 다음 프레임으로 넘긴다.
            view.setVariable(BR.name, name)
//            view.executePendingBindings()
        }
    }

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