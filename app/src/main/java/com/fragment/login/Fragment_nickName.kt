package com.fragment.login

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.agreement.Agreement
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

@SuppressLint("UseCompatLoadingForDrawables")
class Fragment_nickName : Fragment(), TextWatcher, View.OnKeyListener, View.OnClickListener {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupNicknameBinding
    private var bindObj: FragmentSignupNicknameBinding? =null
    private var checkPrivateAgreement = false
    private var checkAppAgreement = false
    private var checkTotalAgreement = false
    private var checkNickname = false
    private lateinit var nicknameDisposable: Disposable
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        fun newInstance(): Fragment_nickName {
            val fragment = Fragment_nickName()
            return fragment
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindObj = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_nickname, container, false)
        binding = bindObj!!
        binding.lifecycleOwner = this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.insertInfoEditText.addTextChangedListener(this)
        binding.insertInfoEditText.setOnKeyListener(this)

        binding.plzAgreement.allAgreement.setOnClickListener(this)
        binding.plzAgreement.appAgreement.setOnClickListener(this)
        binding.plzAgreement.privateAgreement.setOnClickListener(this)
        binding.plzAgreement.appAgreementWebView.setOnClickListener(this)
        binding.plzAgreement.privateAgreementWebView.setOnClickListener(this)


        //화면을 터치할 시, 키패드 삭제
        GlobalApplication.instance.removeEditextFocus(binding.insertInfoEditText,binding.nickNameParentLayout)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun rightNickName() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ //서버 과부화를 막기위해서 300ms 이후에 조회 요청

        var check = Pattern.matches("^\\w+|[가-힣]+$", binding.insertInfoEditText.text.toString())
        if (binding.insertInfoEditText.text.toString().isNotEmpty()) {

            for(word in GlobalApplication.instance.getBanWordList()){
                if(binding.insertInfoEditText.text.contains(word)){
                    check = false
                }
            }

            if (check) {
                //api 설정
                    nicknameDisposable = ApiGenerator.retrofit.create(ApiService::class.java)
                        .checkNickName(GlobalApplication.userBuilder.createUUID, binding.insertInfoEditText.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //result=true면 닉네임 중복을 의미
                            it.data?.result?.let { result->
                                if(!result){// 중복이 아닌경우
                                    checkNickname = true
                                    setGreen()
                                } else{ //중복인 경우
                                    setRed()
                                    checkNickname = false
                                }
                            }
                            //버튼 활성화 여부 확인
                            checkEnable()
                        }, {t ->
                            Log.e(ErrorManager.NICKNAME_DUPLICATE,t.message.toString())})

            } else { //닉네임 패턴을 위배하는 경우
                setRed()
                checkNickname=false
                //버튼 활성화 여부 확인
                checkEnable()
            }
        } else {// 사용자가 다 닉네임을 다 지운 경우
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.insertNameLinearLayout.background =
                    resources.getDrawable(R.drawable.bottom_line, null)
            }else{
                context?.let {
                    binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(it,R.drawable.bottom_line) }
            }
            binding.checkNickNameText.visibility = View.INVISIBLE

            checkNickname=false
            //버튼 활성화 여부 확인
            checkEnable()
        }},300)
    }


    private fun setGreen(){
        binding.checkNickNameText.visibility = View.VISIBLE
        binding.checkNickNameText.text = getString(R.string.useNickName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.insertNameLinearLayout.background = resources.getDrawable(R.drawable.bottom_line_green, null)
        }else{
            context?.let {
                binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(it,R.drawable.bottom_line) }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.checkNickNameText.setTextColor(resources.getColor(R.color.green, null))
        }else{
            context?.let {
                binding.checkNickNameText.setTextColor(ContextCompat.getColor(it,R.color.green))
            }
        }
    }

    private fun setRed(){
        binding.checkNickNameText.visibility = View.VISIBLE
        binding.checkNickNameText.text = getString(R.string.dontUseNickName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.insertNameLinearLayout.background =
                resources.getDrawable(R.drawable.bottom_line_red, null)
        }else{
            context?.let {
                binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(it,R.drawable.bottom_line_red)
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.checkNickNameText.setTextColor(resources.getColor(R.color.red, null))
        }else{
            context?.let {
                binding.checkNickNameText.setTextColor(ContextCompat.getColor(it, R.color.red))
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            GlobalApplication.instance.keyPadSetting(binding.insertInfoEditText,requireContext())
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        rightNickName()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.all_agreement -> {
                if (!checkTotalAgreement) {
                    checkTotalAgreement = true
                    checkPrivateAgreement = true
                    checkAppAgreement = true
                } else {
                    checkTotalAgreement = false
                    checkPrivateAgreement = false
                    checkAppAgreement = false
                }
                checkEnable()
            }

            R.id.app_agreement -> {
                if (!checkAppAgreement) {
                    checkAppAgreement = true

                } else {
                    checkAppAgreement = false
                    checkTotalAgreement = false
                }
                checkEnable()
            }
            R.id.private_agreement -> {
                if (!checkPrivateAgreement) {
                    checkPrivateAgreement = true
                } else {
                    checkPrivateAgreement = false
                    checkTotalAgreement = false

                }
                checkEnable()
            }
            R.id.app_agreement_webView->{
                val  bundle =Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                GlobalApplication.instance.moveActivity(requireContext(),Agreement::class.java,0,bundle,GlobalApplication.AGREEMENT,1)
            }

            R.id.private_agreement_webView->{
                val  bundle =Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,1)
                GlobalApplication.instance.moveActivity(requireContext(),Agreement::class.java,0,bundle,GlobalApplication.AGREEMENT,1)
            }
            else -> {
            }
        }
    }

    private fun checkEnable() {
        var result = false
        //전체 동의로 클릭했을 때
        if (checkTotalAgreement) {
            binding.plzAgreement.allCheck.setImageResource(R.mipmap.big_checkbox_full)
            binding.plzAgreement.appAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)
            binding.plzAgreement.privateAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)

            result = checkNickname && checkTotalAgreement
            viewmodel.buttonState.value = result
            viewmodel.nickname = binding.insertInfoEditText.text.toString()

        } else {
            //total로 클릭하지 않고 mini 동의로 클릭했을 때
            if (checkAppAgreement && checkPrivateAgreement) {
                binding.plzAgreement.allCheck.setImageResource(R.mipmap.big_checkbox_full)
                binding.plzAgreement.appAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)
                binding.plzAgreement.privateAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)

                //동의가 모두 끝났기 때문에 이름만 입력되면 버튼 활성화
                result = checkNickname &&checkAppAgreement && checkPrivateAgreement

                viewmodel.buttonState.value = result
                viewmodel.nickname = binding.insertInfoEditText.text.toString()

            }
            //mini 동의를 클릭했을 때
            else {
                viewmodel.buttonState.value = false
                binding.plzAgreement.allCheck.setImageResource(R.mipmap.big_checkbox_empty)

                if (checkAppAgreement) {
                    binding.plzAgreement.appAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)
                } else {
                    binding.plzAgreement.appAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_empty)
                }

                if (checkPrivateAgreement) {
                    binding.plzAgreement.privateAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)
                } else {
                    binding.plzAgreement.privateAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_empty)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj =null
    }
}