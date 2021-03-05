package com.activities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.activities.alcohol_detail.AlcoholDetail
import com.activities.login.apple.AppleLogin
import com.activities.login.google.GoogleLogin
import com.activities.login.kakao.KakaoLogin
import com.activities.login.naver.NaverLogin
import com.activities.main.MainActivity
import com.activities.signup.SignUp
import com.application.GlobalApplication
import com.base.BaseActivity
import com.custom.CustomDialog
import com.error.ErrorManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.model.token.GetUserData
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LoginBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Login : BaseActivity<LoginBinding>(), View.OnClickListener {
    override val layoutResID: Int = R.layout.login

    private val GOOGLE_SIGN = 1
    private lateinit var googleLogin: GoogleLogin                                    //구글 로그인 객체
    private lateinit var kakaoLogin: KakaoLogin                                      //카카오 로그인 객체
    private lateinit var naverLogin: NaverLogin                                      //네이버 로그인 객체
    private lateinit var appleLogin: AppleLogin                                      //애플 로그인 객체
    private var compositdisposable = CompositeDisposable()
    private var handlingNumber = 0                                                   //액티비티 핸들링번호

    private val executeProgressBar: (Boolean) -> Unit = { status ->                  //로딩화면 유무
        progressbarStatus(this, status)
    }

    /***
     *    executeProgressBar를 다르게 사용할 수 있는 패턴들
     *    private val exe = fun(){}
     *
     *    private val exe1 = fun():Boolean {
     *    return false
     *    }
     *
     *    private val exe2 = fun(check:Boolean):Boolean{
     *    return check
     *    }
     */


    init {
        loginObj = this
    }

    companion object {
        /**
         * 다른 클래스에서 쉽게 접근할 수 있도록 singleTon pattern을 적용
         */
        lateinit var loginObj: Login
    }


    override fun setOnCreate() {
        //액티비티를 핸들링하기 위한 넘버 get
        if (intent.hasExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ACTIVITY_HANDLING_BUNDLE)
            handlingNumber = bundle?.getInt(GlobalApplication.ACTIVITY_HANDLING, 0)!!
        }
    }

    override fun destroyPresenter() {
        compositdisposable.dispose()
    }


    /**
     * 카카오 로그인 객체 초기화
     */
    private fun kakaoExcute() {
        kakaoLogin = KakaoLogin(this)
        kakaoLogin.executeProgressBar = executeProgressBar

        executeProgressBar(true)
        //카카오톡 어플이 있는 경우 자동 정보를 얻어옴
        if (kakaoLogin.instance.isKakaoTalkLoginAvailable(this))
            kakaoLogin.instance.loginWithKakaoTalk(this, callback = kakaoLogin.callback)

        //무조건 웹뷰를 통해 로그인을 강제하도록하여 이후 정보를 얻어옴
        else
            kakaoLogin.instance.loginWithKakaoAccount(this, callback = kakaoLogin.callback)
    }

    /**
     * 네이버 로그인 객체 초기화
     */
    private fun naverExecute() {
        naverLogin = NaverLogin(this)
        naverLogin.executeProgressBar = executeProgressBar

        executeProgressBar(true)
        naverLogin.instance.startOauthLoginActivity(this, naverLogin.naverLoginHandler)
    }

    /**
     * 구글 로그인 객체 초기화
     * 애플과 구글 로그인은 파이어베이스로 권한을 양도하여 파이어베이스에서 관리하도록함
     * 때문에 기존의 로그인이 있다면 로그아웃하고 현재 로그인 객체로 할당
     */
    private fun googleExecute() {
        FirebaseAuth.getInstance().signOut()
        googleLogin = GoogleLogin(this)
        startActivityForResult(googleLogin.instance.signInIntent, GOOGLE_SIGN)
    }

    /**
     * 애플 로그인 객체 초기화
     * 애플과 구글 로그인은 파이어베이스로 권한을 양도하여 파이어베이스에서 관리하도록함
     * 때문에 기존의 로그인이 있다면 로그아웃하고 현재 로그인 객체로 할당
     */
    private fun appleExecute() {
        FirebaseAuth.getInstance().signOut()
        appleLogin = AppleLogin(this)
        appleLogin.executeProgressBar = executeProgressBar

        appleLogin.loginExecute()
        executeProgressBar(true)
    }

    /**
     * 로딩화면 유무 판단
     * 로딩화면 진행 중일때, 화면 터치 작동 x
     */
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

    /**
     * 소셜 로그인이 진행되고서, 다시 화면으로 돌아올 경우
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            executeProgressBar(false)
        }

        //구글인 경우
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

        //넘겨받은 권한으로 로그인 진행
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { result ->
                if (result.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                        ?.addOnCompleteListener(this) {
                            //유저 정보 셋팅
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

    /**
     * 유저 정보 일부를 셋팅
     */
    fun setUserInfo(provider: String?, oauth_token: String?) {
        GlobalApplication.userBuilder
            .setProvider(provider)
            .setOAuthToken(oauth_token)

        handlingActivity()
    }

    @SuppressLint("SetTextI18n")
    fun handlingActivity() {
        val map = HashMap<String, String?>()
        GlobalApplication.userBuilder.getProvider()?.let { map.put("oauth_provider", it) }
        GlobalApplication.userBuilder.getOAuthToken()?.let { map.put("oauth_token", it) }

        compositdisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .signUp(GlobalApplication.userBuilder.createUUID, map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result: GetUserData ->
                //프로그래스바 종료
                executeProgressBar(false)

                /**
                 * 서버로부터 받은 토큰이 존재한다면, 이는 해당 소셜 로그인을 통해 회원가입을
                 * 진행한 적이 있는 것이므로 서버에 저장된 유저데이터를 할당하고 액티비티를 전환시킨다.
                 * 이때 {@param handlingNumber}를 사용하여 로그인 전의 화면으로 핸들링 될 수 있도록한다.
                 */
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
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ user ->
                                //유저 정보 셋팅
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

                            //로그인 액티비티 이전으로 이동
                            moveActivity()
                        }, { e ->
                            Log.e("유저정보 셋팅 문제", e.message.toString())
                            CustomDialog.networkErrorDialog(this)
                        })
                    )
                }
                /**
                 * 서버로부터 받은 결과 값에 토큰이 없다면 회원가입을 진행한다.
                 * 소셜 로그인으로부터 제공받은 개인정보들은 해당 클래스에서 입력받고 회원가입
                 * 진행절차에서는 제외시킨다.
                 */
                else {
                    GlobalApplication.userBuilder.setOAuthId(result.data?.user?.userId)
                    val bundle = Bundle()

                    result.data?.user?.hasEmail?.let { hasEmail->
                        if(hasEmail){ //소셜 로그인으로부터 이메일을 제공 받는 경우

                            //소셜 로그인으로부터 생년월일을 제공 받는 경우
                            result.data?.user?.hasBirth?.let {
                                bundle.putBoolean(GlobalApplication.BIRTHDAY, it)
                                Log.e("생일체크", it.toString())
                                result.data?.user?.birth?.let { birth->
                                    GlobalApplication.userBuilder.setBirthDay(birth)
                                }
                            }

                            //소셜 로그인으로부터 성별을 제공 받는 경우
                            result.data?.user?.hasGender?.let {
                                bundle.putBoolean(GlobalApplication.GENDER, it)
                                Log.e("성별체크", it.toString())
                                result.data?.user?.gender?.let { gender->
                                    GlobalApplication.userBuilder.setGender(gender)
                                }
                            }

                            //회원가입 액티비티로 전환
                            GlobalApplication.instance.moveActivity(this,SignUp::class.java
                                ,0,bundle,GlobalApplication.USER_BUNDLE)
                        }
                        else{
                            /**
                             * 반드시 소셜로그인에서 이메일을 제공받은 후, 회원가입을 진행하는 것을 원칙으로 한다.
                             */

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

        /**
         * handling 유무에 따라서 액티비티 이동
         */
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

    /**
     * 소셜로그인 로그아웃을 처리하는 메서드
     */
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
}