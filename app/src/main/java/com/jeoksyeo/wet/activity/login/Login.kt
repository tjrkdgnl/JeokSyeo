package com.jeoksyeo.wet.activity.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import error.ErrorManager
import java.lang.Exception

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: LoginBinding

    private val GOOGLE_SIGN = 1
    private val APPLE_SIGN = 2

    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var naverLogin: NaverLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login)

        binding.kakakoButton.setOnClickListener(this)
        binding.naverLogin.setOnClickListener(this)
        binding.googleLogin.setOnClickListener(this)
        binding.appleLogin.setOnClickListener(this)

    }

    private fun kakaoExcute() {
        kakaoLogin = KakaoLogin(this)
        if (kakaoLogin.instance.isKakaoTalkLoginAvailable(this))
            kakaoLogin.instance.loginWithKakaoTalk(this, callback = kakaoLogin.callback)
        else
            kakaoLogin.instance.loginWithKakaoAccount(this, callback = kakaoLogin.callback)
    }

    private fun naverExecute() {
        naverLogin = NaverLogin(this)
        naverLogin.instance.startOauthLoginActivity(this, naverLogin.naverLoginHandler)
    }

    private fun googleExecute() {
        googleLogin = GoogleLogin(this, this)
        startActivityForResult(googleLogin.instance.signInIntent, GOOGLE_SIGN)
    }

    private fun appleExecute() {
        val appleLogin = AppleLogin(this)
        appleLogin.loginExecute()

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.kakakoButton -> kakaoExcute()

            R.id.naverLogin -> naverExecute()

            R.id.googleLogin -> googleExecute()

            R.id.appleLogin -> appleExecute()

            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: Exception) {
            val message = e.message ?: e.stackTrace
            Log.e(ErrorManager.Google_TAG, message.toString())
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            Log.e("googleAccessToken", account.idToken.toString())
            startActivity(Intent(this, SignUp::class.java))
        }
    }

}