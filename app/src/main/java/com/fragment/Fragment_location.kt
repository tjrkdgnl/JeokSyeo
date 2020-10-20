package com.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.location.LocationAdapter
import com.application.GlobalApplication
import com.model.area.GetAreaData
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupLocationBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Fragment_location : Fragment() {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupLocationBinding
    var item: String = ""
    private var checkState = false
    private var checkCountry = false
    private lateinit var disposable: Disposable

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

        binding.locationRecyclerView.setHasFixedSize(true)
        val gridLayout = GridLayoutManager(requireContext(), 4)
        binding.locationRecyclerView.layoutManager = gridLayout
        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .getArea(GlobalApplication.userBuilder.createUUID, "11")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetAreaData ->
                result.data?.areaList?.let {
                    binding.locationRecyclerView.adapter =
                        LocationAdapter(requireContext(), it.toMutableList())
                }
            }, { t: Throwable? -> t?.stackTrace })


        binding.stateText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0) {
                    checkState = true
                    if (checkState && checkCountry) {
                        viewmodel.setButtonState(true)
                        viewmodel.setCheckSignUp(true)
                    }

                } else {
                    viewmodel.setButtonState(false)
                    viewmodel.setCheckSignUp(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.countryText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count != 0) {
                    checkCountry = true
                    if (checkState && checkCountry) {
                        viewmodel.setButtonState(true)
                        viewmodel.setCheckSignUp(true)
                    }

                } else {
                    viewmodel.setButtonState(false)
                    viewmodel.setCheckSignUp(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding.root
    }
}