package com.fragment.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.location.LocationAdapter
import com.application.GlobalApplication
import com.model.area.AreaList
import com.model.area.GetAreaData
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupLocationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Fragment_location : Fragment(), View.OnClickListener, LocationInterface {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupLocationBinding
    var item: String = ""
    private lateinit var disposable: Disposable
    private var locationAdapter: LocationAdapter? = null
    var emptyAreaList = AreaList().apply {
        name = ""
        code = ""
    }

    companion object {
        fun newInstance(): Fragment_location {
            val fragment = Fragment_location()
            return fragment
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_location, container, false)
        binding.lifecycleOwner = this //데이터의 변경을 감지하기 위해서 설정해줘야함.
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.stateText.setOnClickListener(this)
        binding.countryText.setOnClickListener(this)

        binding.locationRecyclerView.setHasFixedSize(true)
        val gridLayout = GridLayoutManager(requireContext(), 4)
        binding.locationRecyclerView.layoutManager = gridLayout

        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(GlobalApplication.userBuilder.createUUID, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetAreaData ->
                result.data?.areaList?.let {
                    // 시/도에 대한 리스트는 한번 호출 시 임시로 저장.
                    locationAdapter = LocationAdapter(it.toMutableList(), this)
                    binding.locationRecyclerView.adapter = locationAdapter
                }
            }, { t: Throwable? -> t?.stackTrace })

        viewmodel.stateArea.observe(viewLifecycleOwner, Observer { it ->
            it?.let { area -> binding.stateText.text = area.name }
        })

        viewmodel.countryArea.observe(viewLifecycleOwner, Observer { it ->
            it?.let { area -> binding.countryText.text = area.name }
        })

        viewmodel.townArea.observe(viewLifecycleOwner, Observer {
            it?.let { town ->
                if(town.name !=""){
                    binding.countryText.text = viewmodel.countryArea.value?.name + town.name
                }
            }
        })

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.state_Text -> {
                locationAdapter?.updateList(null)
                binding.stateText.text = ""
                binding.countryText.text = ""
                locationAdapter?.depth = 0
                viewmodel.lock = false
                viewmodel.countryArea.value = emptyAreaList
                viewmodel.stateArea.value = emptyAreaList
                viewmodel.townArea.value = emptyAreaList
            }

            R.id.country_text -> {
                locationAdapter?.updateList(viewmodel.stateArea.value?.code!!)
                binding.countryText.text = ""
                locationAdapter?.depth = 1
                viewmodel.lock = false
                viewmodel.countryArea.value =emptyAreaList
                viewmodel.townArea.value = emptyAreaList
            }

            else -> {
            }
        }
    }

    override fun getViewModel(): SignUpViewModel {
        return viewmodel
    }
}