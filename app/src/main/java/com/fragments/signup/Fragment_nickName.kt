package com.fragments.signup

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.activities.agreement.Agreement
import com.activities.signup.SignUp
import com.application.GlobalApplication
import com.base.BaseFragment
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.regex.Pattern

@SuppressLint("UseCompatLoadingForDrawables")
class Fragment_nickName : BaseFragment<FragmentSignupNicknameBinding>(), TextWatcher, View.OnKeyListener, View.OnClickListener {
    override val layoutResID: Int =  R.layout.fragment_signup_nickname

    private lateinit var viewmodel: SignUpViewModel
    private var checkPrivateAgreement = false   //개인정보처리방침 체크
    private var checkAppAgreement = false       //이용약관방침 체크
    private var checkTotalAgreement = false     //전체 동의 체크
    private var checkNickname = false           //닉네임 사용유무 체크
    private  var nicknameDisposable: Disposable? =null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        fun newInstance(): Fragment_nickName {
            val fragment = Fragment_nickName()
            return fragment
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    }

    override fun detachPresenter() {
        nicknameDisposable?.dispose()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun rightNickName() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ //서버 과부화를 막기위해서 300ms 이후에 조회 요청

            //입력되는 네임에 띄워쓰기, 특수문자 사용못하도록 규칙 생성
        var check = Pattern.matches("^\\w+|[가-힣]+$", binding.insertInfoEditText.text.toString())
        if (binding.insertInfoEditText.text.toString().isNotEmpty()) {

            //닉네임에 포함되어져 있는 단어가 있다면 허용 안됨
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
        } else {// 사용자가 다 닉네임을 다 지운 경우 기본 색상으로 셋팅
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.insertNameLinearLayout.background =
                    resources.getDrawable(R.drawable.bottom_line, null)
            }else{
                context?.let {
                    binding.insertNameLinearLayout.background =
                    ContextCompat.getDrawable(it,R.drawable.bottom_line) }
            }

            //사용가능에 대한 안내문구 숨김
            binding.checkNickNameText.visibility = View.INVISIBLE

            checkNickname=false
            //버튼 활성화 여부 확인
            checkEnable()
        }},300)
    }

    //닉네임이 사용 가능한 경우, 밑줄과 안내문구를 녹색
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

    //닉네임이 사용 불가능한 경우 밑줄과 안내문구를 빨간색으로 표시
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

    //키패드에서 엔터를 클릭했을 시, 키패드 내리기
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
            //약관 모두 동의 및 모두 취소
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
            //이용약관 한번만 클릭했을 경우
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
            // '>' 클릭 시, 이용약관에 대한 webview 띄우기
            R.id.app_agreement_webView->{
                val  bundle =Bundle()
                bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                GlobalApplication.instance.moveActivity(requireContext(),Agreement::class.java,0,bundle,GlobalApplication.AGREEMENT,1)
            }
            // '>' 클릭 시, 개인정보처리방침에 대한 webview 띄우기
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

            //ok버튼 활성화
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
                //둘다 동의했을 시가 아닌, 한개만 동의한 결과이므로 ok버튼 비활성화
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

}