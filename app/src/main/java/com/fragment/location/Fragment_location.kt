package com.fragment.location

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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Fragment_location : Fragment(), View.OnClickListener, LocationInterface {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupLocationBinding
    var item: String = ""
    private var checkState = false
    private var checkCountry = false
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var stateList: MutableList<AreaList>? = null
    private set
    private var locationAdapter: LocationAdapter? = null

    companion object {
        fun newInstance(): Fragment_location {
            val fragment = Fragment_location()

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_location, container, false)
        binding.lifecycleOwner = this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.stateText.setOnClickListener(this)
        binding.countryText.setOnClickListener(this)

        binding.locationRecyclerView.setHasFixedSize(true)
        val gridLayout = GridLayoutManager(requireContext(), 4)
        binding.locationRecyclerView.layoutManager = gridLayout

        compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(GlobalApplication.userBuilder.createUUID, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetAreaData ->
                result.data?.areaList?.let {
                    // 시/도에 대한 리스트는 한번 호출 시 임시로 저장.
                    stateList = result.data?.areaList?.toMutableList()
                    locationAdapter = LocationAdapter(requireContext(), it.toMutableList(), this)
                    binding.locationRecyclerView.adapter = locationAdapter
                }
            }, { t: Throwable? -> t?.stackTrace }))

        viewmodel.stateSelectButton.observe(viewLifecycleOwner, Observer { it ->
            if(it){
                viewmodel.stateName?.let {name-> binding.stateText.text =name }
            }
        })

        viewmodel.countrySelectButton.observe(viewLifecycleOwner, Observer { it ->
            if(it){
                viewmodel.countryName?.let { name->binding.countryText.text =name }
            }
        })

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.state_Text -> {
                stateList?.let { list ->
                    locationAdapter?.changeList(list.toMutableList())
                    binding.stateText.text = ""
                    binding.countryText.text = ""
                    viewmodel.countrySelectButton.value=false
                }
            }

            R.id.country_text -> {
                stateList?.let { list ->
                    locationAdapter?.changeList(list.toMutableList())
                    binding.countryText.text = ""
                }
            }

            else -> {
            }
        }
    }

    override fun getViewModel(): SignUpViewModel {
        return viewmodel
    }
}