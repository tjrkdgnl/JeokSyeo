package com.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupCongratulationBinding
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
                GlobalApplication.userInfo = GlobalApplication.userBuilder.build()

                disposable =ApiGenerator.retrofit.create(ApiService::class.java).signUp(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //내장 디비에 토큰 값들 저장
                    GlobalApplication.userDataBase.setAccessToken(it.data?.token?.accessToken.toString())
                    GlobalApplication.userDataBase.setRefreshToken(it.data?.token?.refreshToken.toString())
                    JWTUtil.decodeAccessToken(GlobalApplication.userDataBase.getAccessToken())
                    JWTUtil.decodeRefreshToken(GlobalApplication.userDataBase.getRefreshToken())

                    Log.e("DB_엑세스 토큰",GlobalApplication.userDataBase.getAccessToken().toString())
                    Log.e("DB_리프레쉬 토큰",GlobalApplication.userDataBase.getRefreshToken().toString())

                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    Toast.makeText(requireContext(),"회원가입을 축하드립니다!",Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                },{t:Throwable->t.stackTrace})}

            else->{}
        }
    }
}