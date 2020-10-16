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
import com.jeoksyeo.wet.activity.application.GlobalApplication
import com.jeoksyeo.wet.activity.signup.SignUp
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import error.ErrorManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.Data
import model.TokenData
import model.UserInfo
import service.ApiGenerator
import service.ApiService
import java.lang.Exception
import java.util.function.LongToDoubleFunction

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: LoginBinding
    private val GOOGLE_SIGN = 1
    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var naverLogin: NaverLogin
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

        binding.kakakoButton.setOnClickListener(this)
        binding.naverLogin.setOnClickListener(this)
        binding.googleLogin.setOnClickListener(this)
        binding.appleLogin.setOnClickListener(this)
        binding.refreshButton.setOnClickListener(this)

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

            R.id.refreshButton-> refresh()

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
            val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential)

            updateUI(account)
        } catch (e: Exception) {
            val message = e.message ?: e.stackTrace
            Log.e(ErrorManager.Google_TAG, message.toString())
            updateUI(null)
        }
    }

    fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            setUserInfo("GOOGLE",account.id.toString(),"goole회원가입",account.email,"1994-08-18","M","123")

            Log.e("googleAccessToken", account.idToken.toString())
            Log.e("google accout Id", account.id.toString())
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    fun refresh(){
        val map =HashMap<String,Any>()
        GlobalApplication.userInfo.refreshToken?.let { map.put("refresh_token", it) }

        refreshDisposable = ApiGenerator.retrofit.create(ApiService::class.java).refreshToken(GlobalApplication.userInfo.createUUID, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {result :TokenData ->
                Log.e("갱신성공",result.data?.token?.accessToken.toString())

            }
                ,{t:Throwable->t.stackTrace} )
    }


    fun handlingActivity() {
        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .signUp(GlobalApplication.userInfo.createUUID, GlobalApplication.userInfo.infoMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: TokenData ->
                Log.e("result",result.data?.token?.accessToken.toString())
                GlobalApplication.userInfo.accessToken =result.data?.token?.accessToken
                GlobalApplication.userInfo.refreshToken=result.data?.token?.refreshToken

            }, { t: Throwable? -> t?.stackTrace })

        startActivity(Intent(this, SignUp::class.java))
    }

    fun setUserInfo(provider: String?, oauth_id: String?, nickname: String,
        email: String?, birth: String?, gender: String?, profileURL: String?) {

        GlobalApplication.userInfo = UserInfo.Builder("")
            .setProvider(provider)
            .setOAuthId(oauth_id)
            .setNickName(nickname)
            .setEmail(email)
            .setBirthDay(birth)
            .setGender(gender)
            .setProfileImgURL(profileURL)
            .build()

        handlingActivity()
    }
}