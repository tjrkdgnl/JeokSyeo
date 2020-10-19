package com.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupCongratulationBinding
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Fragment_Congratulation : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentSignupCongratulationBinding
    private lateinit var disposable: Disposable
    companion object {
        fun newInstance(): Fragment_Congratulation {
            val fragment = Fragment_Congratulation()

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_congratulation, container, false)
        binding.lifecycleOwner =this

       binding.button.setOnClickListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposable?.let { it.dispose() }
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.button ->{
                //최종적으로 얻어온 모든 정보를 가지고서, map 재 셋팅
                GlobalApplication.userInfo.settingMap()

                disposable =ApiGenerator.retrofit.create(ApiService::class.java).signUp(GlobalApplication.userInfo.createUUID,GlobalApplication.userInfo.infoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    GlobalApplication.userInfo.accessToken = it.data?.token?.accessToken
                    GlobalApplication.userInfo.refreshToken = it.data?.token?.refreshToken
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    Toast.makeText(requireContext(),"회원가입을 축하드립니다!",Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                },{t:Throwable->t.stackTrace})}

            else->{}
        }
    }
}