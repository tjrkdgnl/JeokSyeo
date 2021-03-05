package com.fragments.signup.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.model.area.AreaList
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupLocationBinding

class Fragment_location : BaseFragment<FragmentSignupLocationBinding>(), View.OnClickListener,
    LocationContract.LocationView {
    override val layoutResID: Int = R.layout.fragment_signup_location
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var locationPresenter: LocationPresenter

    companion object {
        fun newInstance(): Fragment_location {
            return  Fragment_location()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cityText.setOnClickListener(this)
        binding.townText.setOnClickListener(this)

        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        locationPresenter = LocationPresenter().apply {
            this.viewObj=this@Fragment_location
            activity = requireActivity()
        }

        locationPresenter.getArea(null)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cityText -> {
                locationPresenter.initCityArea()
            }

            R.id.townText -> {
                locationPresenter.initTownArea()
            }
        }
    }

    override fun detachPresenter() {
        locationPresenter.detach()
    }

    override fun setCityName() {
        binding.cityText.text =  viewmodel.locationMap[SignUpViewModel.CITY]?.name
    }

    override fun setMiddleTownName() {
        binding.townText.text =  viewmodel.locationMap[SignUpViewModel.MIDDLE_TOWN]?.name
    }

    @SuppressLint("SetTextI18n")
    override fun setSmallTownName() {
        binding.townText.text =   "${viewmodel.locationMap[SignUpViewModel.MIDDLE_TOWN]?.name}/${
        viewmodel.locationMap[SignUpViewModel.SMALL_TOWN]?.name}"
    }

    override fun getViewModel(): SignUpViewModel {
        return viewmodel
    }


    override fun getBindingObj(): FragmentSignupLocationBinding {
        return binding
    }
}