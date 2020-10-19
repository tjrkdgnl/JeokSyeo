package com.jeoksyeo.wet.activity.login

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
import com.google.firebase.auth.GoogleAuthProvider
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.signup.SignUp
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.model.GetUserData
import com.model.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import java.lang.Exception

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: LoginBinding
    private val GOOGLE_SIGN = 1
    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var naverLogin: NaverLogin
    private lateinit var appleLogin: AppleLogin

    private lateinit var disposable: Disposable
    private lateinit var refreshDisposable: Disposable

    init {
        loginObj = this
    }

    companion object {
        lateinit var loginObj: Login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login)

        binding.kakaoLoginButton.setOnClickListener(this)
        binding.naverLoginButton.setOnClickListener(this)
        binding.googleLoginButton.setOnClickListener(this)
        binding.appleLoginButton.setOnClickListener(this)

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
        appleLogin = AppleLogin(this)
        appleLogin.loginExecute()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.kakaoLogin_button -> kakaoExcute()

            R.id.naverLogin_Button -> naverExecute()

            R.id.googleLogin_button -> googleExecute()

            R.id.appleLogin_button -> appleExecute()

//            R.id.refreshButton-> refresh()

            R.id.logout-> {
                appleLogin = AppleLogin(this)
                appleLogin.appleSignOut()
            }
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

            //구글 소셜 로그인을 파이어베이스에 넘겨줌.
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)

            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener(this){
               setUserInfo("GOOGLE",it.result?.token.toString())
            }?.addOnFailureListener(this) {
                Log.e(ErrorManager.Google_TAG,it.message.toString())
            }

        } catch (e: Exception) {
            val message = e.message ?: e.stackTrace
            Log.e(ErrorManager.Google_TAG, message.toString())

        }
    }

    fun getRefreshToken() {
        val map = HashMap<String, Any>()
        GlobalApplication.userInfo.refreshToken?.let { map.put("refresh_token", it) }

        refreshDisposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .refreshToken(GlobalApplication.userInfo.createUUID, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetUserData ->

            }, { t: Throwable -> t.stackTrace })
    }

    fun handlingActivity() {
        var intent = Intent(this, SignUp::class.java)
        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .signUp(GlobalApplication.userInfo.createUUID, GlobalApplication.userInfo.infoMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetUserData ->

                //토큰이 있다는 것은 로그인 정보가 있다는 것. 여기서 액티비티 핸들링하기.
                if(result.data?.token !=null){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                //회원가입
                else{
                    result.data?.user?.userId?.let {
                        GlobalApplication.userInfo.user_id = it
                    }
                    result.data?.user?.hasNickname?.let {
                        intent.putExtra(GlobalApplication.NICKNAME, it)
                        GlobalApplication.userInfo.nickName =result.data?.user?.nickname
                        Log.e("닉네임체크", it.toString())
                    }
                    result.data?.user?.hasBirth?.let {
                        intent.putExtra(GlobalApplication.BIRTHDAY, it)
                        GlobalApplication.userInfo.birthDay = result.data?.user?.birth
                        Log.e("생일체크", it.toString())
                    }
                    result.data?.user?.hasGender?.let {
                        intent.putExtra(GlobalApplication.GENDER, it)
                        GlobalApplication.userInfo.gender = result.data?.user?.gender
                        Log.e("성별체크", it.toString())
                    }
                    startActivity(intent)
                }
            }, { t: Throwable? -> t?.stackTrace })

    }

    fun setUserInfo(provider: String?, oauth_id: String?) {
        GlobalApplication.userInfo = UserInfo.Builder("")
            .setProvider(provider)
            .setOAuthToken(oauth_id)
            .build()

        handlingActivity()
    }
}