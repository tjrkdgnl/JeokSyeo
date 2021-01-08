package com.jeoksyeo.wet.activity.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.error.ErrorManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jeoksyeo.wet.activity.alcohol_detail.AlcoholDetail
import com.jeoksyeo.wet.activity.login.apple.AppleLogin
import com.jeoksyeo.wet.activity.login.google.GoogleLogin
import com.jeoksyeo.wet.activity.login.kakao.KakaoLogin
import com.jeoksyeo.wet.activity.login.naver.NaverLogin
import com.jeoksyeo.wet.activity.main.MainActivity
import com.jeoksyeo.wet.activity.signup.SignUp
import com.model.token.GetUserData
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.service.NetworkUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: LoginBinding
    private val GOOGLE_SIGN = 1
    private lateinit var googleLogin: GoogleLogin
    private lateinit var kakaoLogin: KakaoLogin
    private lateinit var naverLogin: NaverLogin
    private lateinit var appleLogin: AppleLogin
    private var compositdisposable = CompositeDisposable()
    private var handlingNumber = 0
    private lateinit var networkUtil: NetworkUtil
    private val executeProgressBar: (Boolean) -> Unit = { status ->
        progressbarStatus(this, status)
    }

    //executeProgressBar를 다르게 사용할 수 있는 패턴들
//    private val exe = fun(){}
//
//    private val exe1 = fun():Boolean {
//        return false
//    }
//
//    private val exe2 = fun(check:Boolean):Boolean{
//        return check
//    }

    init {
        loginObj = this
    }

    companion object {
        lateinit var loginObj: Login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login)


       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil = NetworkUtil(this)
            networkUtil.register()
        }


        if (intent.hasExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)
            handlingNumber = bundle?.getInt(GlobalApplication.ACTIVITY_HANDLING, 0)!!
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = Login::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    private fun kakaoExcute() {
        kakaoLogin = KakaoLogin(this)
        kakaoLogin.executeProgressBar = executeProgressBar

        executeProgressBar(true)
        if (kakaoLogin.instance.isKakaoTalkLoginAvailable(this))
            kakaoLogin.instance.loginWithKakaoTalk(this, callback = kakaoLogin.callback)
        else
            kakaoLogin.instance.loginWithKakaoAccount(this, callback = kakaoLogin.callback)
    }

    private fun naverExecute() {
        naverLogin = NaverLogin(this)
        naverLogin.executeProgressBar = executeProgressBar

        executeProgressBar(true)
        naverLogin.instance.startOauthLoginActivity(this, naverLogin.naverLoginHandler)
    }

    private fun googleExecute() {
        FirebaseAuth.getInstance().signOut()
        googleLogin = GoogleLogin(this, this)
        startActivityForResult(googleLogin.instance.signInIntent, GOOGLE_SIGN)
    }

    private fun appleExecute() {
        FirebaseAuth.getInstance().signOut()
        appleLogin = AppleLogin(this, this)
        appleLogin.executeProgressBar = executeProgressBar

        appleLogin.loginExecute()
        executeProgressBar(true)
    }

    private fun progressbarStatus(activity: Activity, setting: Boolean) {
        if (setting) {
            binding.loginProgressBar.root.visibility = View.VISIBLE
            binding.loginScrollView.isFillViewport = true
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        } else {
            binding.loginProgressBar.root.visibility = View.INVISIBLE
            binding.loginScrollView.isFillViewport = false
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

        if (resultCode != Activity.RESULT_OK) {
            executeProgressBar(false)
        }

        if (requestCode == GOOGLE_SIGN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                handleSignInResult(account.idToken!!)
            } catch (e: Exception) {
                Log.e(ErrorManager.Google_TAG, e.message.toString())
            }
        }
    }

    private fun handleSignInResult(idToken: String) {
        executeProgressBar(true)
        //구글 소셜 로그인을 파이어베이스에 넘겨줌.
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { result ->
                if (result.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                        ?.addOnCompleteListener(this) {

                            setUserInfo("GOOGLE", it.result?.token.toString())

                        }?.addOnFailureListener {
                        Log.e(ErrorManager.Google_TAG, it.message.toString())
                        executeProgressBar(false)
                    }
                } else {
                    Log.w(ErrorManager.Google_TAG, result.exception?.message.toString())
                    executeProgressBar(false)
                }
            }.addOnFailureListener {
                CustomDialog.networkErrorDialog(this)
                Log.e(ErrorManager.Google_TAG, it.message.toString())
                executeProgressBar(false)
            }
    }

    fun setUserInfo(provider: String?, oauth_token: String?) {
        GlobalApplication.userBuilder
            .setProvider(provider)
            .setOAuthToken(oauth_token)

        handlingActivity()
    }

    @SuppressLint("SetTextI18n")
    fun handlingActivity() {
        val map = HashMap<String, Any>()
        GlobalApplication.userBuilder.getProvider()?.let { map.put("oauth_provider", it) }
        GlobalApplication.userBuilder.getOAuthToken()?.let { map.put("oauth_token", it) }

        compositdisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .signUp(GlobalApplication.userBuilder.createUUID, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetUserData ->
                //프로그래스바 종료
                executeProgressBar(false)

                //토큰이 있다는 것은 로그인 정보가 있다는 것. 여기서 액티비티 핸들링하기.
                if (result.data?.token != null) {
                    //토큰 내장디비에 저장
                    GlobalApplication.userDataBase.setAccessToken(result.data?.token?.accessToken)
                    GlobalApplication.userDataBase.setRefreshToken(result.data?.token?.refreshToken)

                    //토큰 내부에 들어있는 만료시간 내장디비에 저장
                    JWTUtil.decodeAccessToken(result.data?.token?.accessToken)
                    JWTUtil.decodeRefreshToken(result.data?.token?.refreshToken)

                    compositdisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                        .getUserInfo(GlobalApplication.userBuilder.createUUID, "Bearer " + GlobalApplication.userDataBase.getAccessToken())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ user ->
                                GlobalApplication.userInfo = UserInfo.Builder().apply {
                                setProvider(user.data?.userInfo?.provider)
                                setNickName(user.data?.userInfo?.nickname ?: "")
                                setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                                setProfile(user.data?.userInfo?.profile)
                                setGender(user.data?.userInfo?.gender ?: "M")
                                setAddress("") //추후에 셋팅하기
                                setLevel(user.data?.userInfo?.level ?: 0)
                                setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                            }.build()
                            moveActivity()
                        }, { e ->
                            Log.e("유저정보 셋팅 문제", e.message.toString())
                            CustomDialog.networkErrorDialog(this)
                        })
                    )
                }
                //회원가입
                else {
                    GlobalApplication.userBuilder.setOAuthId(result.data?.user?.userId)
                    val bundle = Bundle()

                    result.data?.user?.hasEmail?.let { hasEmail->
                        if(hasEmail){ //이메일을 제공 받는 경우
                            result.data?.user?.hasBirth?.let {
                                bundle.putBoolean(GlobalApplication.BIRTHDAY, it)
                                Log.e("생일체크", it.toString())
                                result.data?.user?.birth?.let { birth->
                                    GlobalApplication.userBuilder.setBirthDay(birth)
                                }

                            }
                            result.data?.user?.hasGender?.let {
                                bundle.putBoolean(GlobalApplication.GENDER, it)
                                Log.e("성별체크", it.toString())
                                result.data?.user?.gender?.let { gender->
                                    GlobalApplication.userBuilder.setGender(gender)
                                }
                            }
                            GlobalApplication.instance.moveActivity(this,SignUp::class.java
                                ,0,bundle,GlobalApplication.USER_BUNDLE)
                        }
                        else{ //이메일을 제공받지 않는 경우

                            //연결한 소셜 로그인 링크제거
                            GlobalApplication.userBuilder.getProvider()?.let { providerId->
                                loginUnlink(providerId)
                            }

                            val dialog = CustomDialog.createCustomDialog(this)
                            val content = dialog.findViewById<TextView>(R.id.dialog_contents)
                            val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
                            val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
                            content.text = "\'적셔\'는 서비스 이용내역 안내를 위해 이메일 사용 권한 허용이 필요합니다.\n 이메일 제공에 동의해주세요."
                            okButton.setOnClickListener{dialog.dismiss()}
                            cancelButton.setOnClickListener { dialog.dismiss() }
                        }
                    } ?:   CustomDialog.networkErrorDialog(this)
                }
            }, { t: Throwable? ->
                CustomDialog.networkErrorDialog(this)
                t?.stackTrace
                executeProgressBar(false)
            })
        )
    }

    private fun moveActivity() {
        finish()
        when (handlingNumber) {
            GlobalApplication.ACTIVITY_HANDLING_MAIN -> {
                GlobalApplication.instance.moveActivity(
                    this,
                    MainActivity::class.java,
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            }

            GlobalApplication.ACTIVITY_HANDLING_DETAIL -> {
                GlobalApplication.instance.moveActivity(
                    this,
                    AlcoholDetail::class.java,
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
        }
    }

    private fun loginUnlink(provider: String) {
        when (provider) {
            "KAKAO" -> {
                kakaoLogin.kakaoUnlink()
            }
            "NAVER" -> {
                naverLogin.naverUnlink()
            }
            "GOOGLE" -> {
                googleLogin.googleUnlink()
            }
            "APPLE" -> {
                appleLogin.appleUnlink()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositdisposable.dispose()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkUtil.unRegister()
        }
    }
}