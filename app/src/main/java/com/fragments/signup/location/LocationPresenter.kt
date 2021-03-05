package com.fragments.signup.location

import android.app.Activity
import androidx.recyclerview.widget.GridLayoutManager
import com.adapters.location.LocationAdapter
import com.application.GlobalApplication
import com.model.area.GetAreaData
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LocationPresenter : LocationContract.LocationPresenter {
    override val view: LocationContract.LocationView by lazy {
        viewObj!!
    }
    override var viewObj: LocationContract.LocationView? = null
    override lateinit var activity: Activity
    private  var locationAdapter: LocationAdapter?=null
    private val compositeDisposable = CompositeDisposable()

    override fun getArea(code: String?) {
        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(GlobalApplication.userBuilder.createUUID, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetAreaData ->
                result.data?.areaList?.let {
                    if(locationAdapter ==null){
                        locationAdapter = LocationAdapter(it.toMutableList(), view)

                        view.getBindingObj().locationRecyclerView.apply {
                            adapter = locationAdapter
                            setHasFixedSize(true)
                            layoutManager = GridLayoutManager(activity, 4)
                        }
                    }
                    else{
                        //변경된 지역정보를 어댑터에 전달하여 리스트 변경
                        locationAdapter?.changeList(it.toMutableList())
                    }
                }
            }, { t: Throwable? -> t?.stackTrace })
        )
    }

    override fun initTownArea() {
        view.getViewModel().buttonState.value = false

        if(view.getViewModel().locationMap[SignUpViewModel.SMALL_TOWN] !=null){

            //'구'까지 설정했을 경우, 이전의 depth는 middleTown이므로 해당 지역 리스트로 셋팅되어야한다.
            if(view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] !=null){
                getArea(view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN]?.code)

                //townText에 '구'는 지우고 '시'or '군'만 표시하도록한다.
                view.getBindingObj().townText.text =
                    view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN]?.name

                //depth를 한단계 빠져 나왔으니 smallTown은 null 처리한다.
                view.getViewModel().locationMap[SignUpViewModel.SMALL_TOWN] =null

            }
        }
        else if(view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] !=null){

            //depth가 '시/군'까지였다면 이전의 depth는 city이기 때문에 city 리스트가 셋팅되어야한다.
            if(view.getViewModel().locationMap[SignUpViewModel.CITY] !=null){
                getArea(view.getViewModel().locationMap[SignUpViewModel.CITY]?.code)

                //town정보는 업어야한다.
                view.getBindingObj().townText.text = ""

                //depth를 한단계 빠져 나왔으니 middleTown은 null 처리한다.
                view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] =null
            }
        }
    }

    override fun initCityArea() {
        getArea(null)
        view.getBindingObj().cityText.text=""
        view.getBindingObj().townText.text = ""
        view.getViewModel().locationMap[SignUpViewModel.CITY] = null
        view.getViewModel().locationMap[SignUpViewModel.MIDDLE_TOWN] = null
        view.getViewModel().locationMap[SignUpViewModel.SMALL_TOWN] = null
        view.getViewModel().buttonState.value = false
    }

    override fun detach() {
        viewObj = null
        compositeDisposable.dispose()
    }
}