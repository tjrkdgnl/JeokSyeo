package com.jeoksyeo.wet.activity.login

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.jeoksyeo.wet.activity.signup.SignUp
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import com.error.ErrorManager
import com.google.android.gms.tasks.OnCompleteListener
import com.jeoksyeo.wet.activity.alchol_category.AlcholCategory
import com.jeoksyeo.wet.activity.alchol_detail.AlcholDetail
import com.jeoksyeo.wet.activity.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.model.token.GetUserData
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import java.lang.Exception

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: LoginBinding
    private val GOOGLE_SIGN = 1
    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var naverLogin: NaverLogin
    private lateinit var appleLogin: AppleLogin
    private lateinit var disposable: Disposable
    private var handlingNumber = 0

    private val executeProgressBar:(Boolean)->Unit = {status->
        progressbarStatus(this,status)
    }

    init {
        loginObj = this
    }

    companion object {
        lateinit var loginObj: Login
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login)

        Log.e("액티비티넘버",intent.getIntExtra(GlobalApplication.ACTIVITY_HANDLING,0).toString())
        if(intent.hasExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)){
            val bundle = intent.getBundleExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)
            handlingNumber = bundle?.getInt(GlobalApplication.ACTIVITY_HANDLING,0)!!
        }
    }

    override fun onStart() {
        super.onStart()

        if(GlobalApplication.userInfo.getProvider() ==null)
            FirebaseAuth.getInstance().signOut()
    }

    private fun kakaoExcute() {
        kakaoLogin = KakaoLogin(this)
        kakaoLogin.executeProgressBar =executeProgressBar

        executeProgressBar(true)
        if (kakaoLogin.instance.isKakaoTalkLoginAvailable(this))
            kakaoLogin.instance.loginWithKakaoTalk(this, callback = kakaoLogin.callback)
        else
            kakaoLogin.instance.loginWithKakaoAccount(this, callback = kakaoLogin.callback)
    }

    private fun naverExecute() {
        naverLogin = NaverLogin(this)
        naverLogin.executeProgressBar=executeProgressBar

        executeProgressBar(true)
        naverLogin.instance.startOauthLoginActivity(this, naverLogin.naverLoginHandler)
    }

    private fun googleExecute() {
        googleLogin = GoogleLogin(this, this)
        startActivityForResult(googleLogin.instance.signInIntent, GOOGLE_SIGN)
    }

    private fun appleExecute() {
            appleLogin = AppleLogin(this,this)
            appleLogin.executeProgressBar = executeProgressBar

            appleLogin.loginExecute()
            executeProgressBar(true)
    }

    private fun progressbarStatus(activity: Activity,setting:Boolean){
        if(setting){
            binding.loginProgressBar.root.visibility = View.VISIBLE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            binding.loginProgressBar.root.visibility = View.INVISIBLE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.kakaoLogin_button -> kakaoExcute()

            R.id.naverLogin_Button -> naverExecute()

            R.id.googleLogin_button -> googleExecute()

            R.id.appleLogin_button -> appleExecute()

            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode !=Activity.RESULT_OK){
            executeProgressBar(false)
        }

        if (requestCode == GOOGLE_SIGN) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            executeProgressBar(true)
            val account = task.getResult(ApiException::class.java)

            //구글 소셜 로그인을 파이어베이스에 넘겨줌.
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {result->
                    if(result.isSuccessful){
                        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener(this) {
                            setUserInfo("GOOGLE", it.result?.token.toString())
                        }?.addOnFailureListener {
                            Log.e(ErrorManager.Google_TAG, it.message.toString())
                            executeProgressBar(false)
                        }
                    }
                    else{
                        executeProgressBar(false)
                    }
                }.addOnFailureListener {
                    Log.e(ErrorManager.Google_TAG,it.message.toString())
                    executeProgressBar(false)
                }

        } catch (e: Exception) {
            val message = e.message ?: e.stackTrace
            Log.e(ErrorManager.Google_TAG, message.toString())
            executeProgressBar(false)
        }
    }

    fun setUserInfo(provider: String?, oauth_token: String?) {
        GlobalApplication.userBuilder
            .setProvider(provider)
            .setOAuthToken(oauth_token)

        handlingActivity()
    }

    fun handlingActivity() {
        val map = HashMap<String,Any>()
        GlobalApplication.userBuilder.getProvider()?.let { map.put("oauth_provider",it) }
        GlobalApplication.userBuilder.getOAuthToken()?.let { map.put("oauth_token",it) }

        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .signUp(GlobalApplication.userBuilder.createUUID,map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetUserData ->
                //프로그래스바 종료
                executeProgressBar(false)

                //토큰이 있다는 것은 로그인 정보가 있다는 것. 여기서 액티비티 핸들링하기.
                if (result.data?.token != null) {
                    GlobalApplication.userDataBase.setAccessToken(result.data?.token?.accessToken)
                    GlobalApplication.userDataBase.setRefreshToken(result.data?.token?.refreshToken)
                    JWTUtil.decodeAccessToken(GlobalApplication.userDataBase.getAccessToken(),
                        splashCheck = false,
                        loginCheck = true
                    )
                    JWTUtil.decodeRefreshToken(GlobalApplication.userDataBase.getRefreshToken(),
                        splashCheck = false,
                        loginCheck = true
                    )
                    Toast.makeText(this,"로그인 되었습니다.",Toast.LENGTH_SHORT).show()

                    moveActivity()
                }
                //회원가입
                else {
                    GlobalApplication.userBuilder.setOAuthId(result.data?.user?.userId)
                    val bundle = Bundle()

                    result.data?.user?.hasBirth?.let {
                        bundle.putBoolean(GlobalApplication.BIRTHDAY, it)
                        Log.e("생일체크", it.toString())
                        GlobalApplication.userBuilder.setBirthDay(result.data?.user?.birth)
                    }
                    result.data?.user?.hasGender?.let {
                        bundle.putBoolean(GlobalApplication.GENDER, it)
                        Log.e("성별체크", it.toString())
                        GlobalApplication.userBuilder.setGender(result.data?.user?.gender)
                    }
                    GlobalApplication.instance.moveActivity(this,SignUp::class.java
                        ,0,bundle,GlobalApplication.USER_BUNDLE)
                }
            }, { t: Throwable? ->
                t?.stackTrace
                executeProgressBar(false)
            })
    }

    private fun moveActivity(){
        finish()
        when(handlingNumber){
            GlobalApplication.ACTIVITY_HANDLING_MAIN-> {
                GlobalApplication.instance.moveActivity(this,MainActivity::class.java,Intent.FLAG_ACTIVITY_CLEAR_TOP) }

            GlobalApplication.ACTIVITY_HANDLING_DETAIL->{
                GlobalApplication.instance.moveActivity(this,AlcholDetail::class.java,Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            GlobalApplication.ACTIVITY_HANDLING_CATEGORY->{
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java,Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            GlobalApplication.ACTIVITY_HANDLING_COMMENT->{
                GlobalApplication.instance.moveActivity(this,AlcholCategory::class.java)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }
}