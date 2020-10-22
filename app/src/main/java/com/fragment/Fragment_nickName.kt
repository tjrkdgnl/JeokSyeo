package com.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentSignupNicknameBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class Fragment_nickName : Fragment(), TextWatcher, View.OnKeyListener, View.OnClickListener {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FragmentSignupNicknameBinding
    private var checkPrivateAgreement = false
    private var checkAppAgreement = false
    private var checkTotalAgreement = false
    private var checkNickname = false
    private lateinit var nicknameDisposable: Disposable

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_nickname, container, false)
        binding.lifecycleOwner = this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.insertInfoEditText.addTextChangedListener(this)
        binding.insertInfoEditText.setOnKeyListener(this)

        binding.plzAgreement.allCheck.setOnClickListener(this)
        binding.plzAgreement.appAgreemntCheckbox.setOnClickListener(this)
        binding.plzAgreement.privateAgreemntCheckbox.setOnClickListener(this)

        return binding.root
    }

    private fun rightNickName(name: String) {
        val check = Pattern.matches("^\\w+|[가-힣]+$", name)

        if (name.isNotEmpty()) {
            if (check) {
                //api 설정
                Log.e("name",name)
                nicknameDisposable = ApiGenerator.retrofit.create(ApiService::class.java)
                    .checkNickName(GlobalApplication.userBuilder.createUUID, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //result=true면 닉네임 중복을 의미
                        if(!it.data?.result!!){
                            GlobalApplication.userBuilder.setNickName(name)
                            checkNickname = name.isNotEmpty()
                            binding.checkNickNameText.visibility = View.VISIBLE
                            binding.checkNickNameText.text = getString(R.string.useNickName)
                            binding.insertNameLinearLayout.background = resources.getDrawable(R.drawable.bottom_line_green, null)
                            binding.checkNickNameText.setTextColor(resources.getColor(R.color.green, null))
                        }
                    }, {t -> t.stackTrace})
            } else {
                binding.checkNickNameText.visibility = View.VISIBLE
                binding.checkNickNameText.text = getString(R.string.dontUseNickName)
                binding.insertNameLinearLayout.background =
                    resources.getDrawable(R.drawable.bottom_line_red, null)
                binding.checkNickNameText.setTextColor(resources.getColor(R.color.red, null))
            }
        } else {
            binding.insertNameLinearLayout.background =
                resources.getDrawable(R.drawable.bottom_line, null)
            binding.checkNickNameText.visibility = View.INVISIBLE
        }
    }

    fun hideKeypad(edit_nickname: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_nickname.windowToken, 0)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            hideKeypad(binding.insertInfoEditText)
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        rightNickName(s.toString())
        checkEnable()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.all_check -> {
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

            R.id.app_agreemnt_checkbox -> {
                if (!checkAppAgreement) {
                    checkAppAgreement = true

                } else {
                    checkAppAgreement = false
                    checkTotalAgreement = false
                }
                checkEnable()
            }
            R.id.private_agreemnt_checkbox -> {
                if (!checkPrivateAgreement) {
                    checkPrivateAgreement = true
                } else {
                    checkPrivateAgreement = false
                    checkTotalAgreement = false

                }
                checkEnable()
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
        } else {
            //total로 클릭하지 않고 mini 동의로 클릭했을 때
            if (checkAppAgreement && checkPrivateAgreement) {
                binding.plzAgreement.allCheck.setImageResource(R.mipmap.big_checkbox_full)
                binding.plzAgreement.appAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)
                binding.plzAgreement.privateAgreemntCheckbox.setImageResource(R.mipmap.mini_checkbox_full)

                //동의가 모두 끝났기 때문에 이름만 입력되면 버튼 활성화
                result = checkNickname

                viewmodel.buttonState.value = result
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
}