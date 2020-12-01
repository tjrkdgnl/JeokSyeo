package com.service

import android.util.Base64
import android.util.Log
import com.application.GlobalApplication
import com.model.user.UserInfo
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object JWTUtil {
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"

    //엑세스토큰 JWT decode
    @Throws(Exception::class)
    fun decodeAccessToken(
        accessToken: String?,
        splashCheck: Boolean = false,
        loginCheck: Boolean = false
    ) {
        try {
            val split = accessToken?.split(".")
            Log.e("Decode accessToken", getJson(split?.get(1)))
            jsonParsing(getJson(split?.get(1)), ACCESS_TOKEN, splashCheck, loginCheck)
        } catch (e: UnsupportedEncodingException) {
            e.stackTrace
        }
    }

    //리프레쉬토큰 JWT decode
    @Throws(Exception::class)
    fun decodeRefreshToken(
        refreshToken: String?,
        splashCheck: Boolean = false,
        loginCheck: Boolean = false
    ) {
        try {
            val split = refreshToken?.split(".")
            jsonParsing(getJson(split?.get(1)), REFRESH_TOKEN, splashCheck, loginCheck)
        } catch (e: UnsupportedEncodingException) {
            e.stackTrace
        }
    }

    //json value get
    @Throws(UnsupportedEncodingException::class)
    private fun getJson(str: String?): String {
        val decodedByte = Base64.decode(str, Base64.URL_SAFE)

        return String(decodedByte, Charset.defaultCharset()) //default charset은 안드로이드 기준 항상 UTF-8
    }

    //json parsing
    private fun jsonParsing(
        json: String,
        token: String,
        splashCheck: Boolean,
        loginCheck: Boolean
    ) {
        val jsonObject = JSONObject(json)

        val check = splashCheck || loginCheck

        //토큰 만료시간 셋팅
        if (token.equals(ACCESS_TOKEN) && check) {
            GlobalApplication.userDataBase.setAccessTokenExpire(jsonObject.getLong("exp"))
            val user = ApiGenerator.retrofit.create(ApiService::class.java)
                .getUserInfo(GlobalApplication.userBuilder.createUUID, "Bearer " + GlobalApplication.userDataBase.getAccessToken())
                .subscribeOn(Schedulers.io())
                .blockingGet()

            GlobalApplication.userInfo = UserInfo.Builder("")
                .setProvider(jsonObject.getString("oauth_provider"))// //provider를 통해서 모든 로그인 체크 여부를 결정하기 때문에 setting해야함
                .setNickName(user.data?.userInfo?.nickname ?: "")
                .setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                .setProfile(user.data?.userInfo?.profile)
                .setGender(user.data?.userInfo?.gender ?: "M")
                .setAddress("") //추후에 셋팅하기
                .setLevel(user.data?.userInfo?.level ?: 0)
                .setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                .build()

            Log.e("엑세스 토큰",GlobalApplication.userInfo.getAccessToken().toString())
        } else {
            GlobalApplication.userDataBase.setRefreshTokenExpire(jsonObject.getLong("exp"))
        }
    }

    private fun getCurrentUTC(): Long {
        val currentUTC = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val utc = simpleDateFormat.parse(simpleDateFormat.format(currentUTC))

        return utc?.time ?: 0
    }

//    private fun getFiveDaysAgo(): Long {
//        val currentCalendar = Calendar.getInstance()
//        currentCalendar.add(Calendar.DATE, -5)
//        val simpleDateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault())
//        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
//
//        val utc = simpleDateFormat.parse(simpleDateFormat.format(currentCalendar.time))
//
//        return utc?.time ?: 0
//    }

    //토큰 만료시간을 체크
    private fun checkExpireOfAccessToken(expire: Long, splashCheck: Boolean): Boolean {
        val accessTokenExpire: Long = expire * 1000L //컴퓨터에서 구한 utc는 1000L이 곱해져 있는 형태이므로 단위 맞춤.
        val currentUTC = getCurrentUTC()

        //엑세스토큰 유효성 검사
        //스플레쉬 화면 일때는 유저 정보를 set하고 이후는 not set하기 때문에 loginCheck는 false
        if (accessTokenExpire > currentUTC) {
            Log.e("엑세스토큰", "유효함")
            decodeAccessToken(GlobalApplication.userDataBase.getAccessToken(), splashCheck, false)
            return true

        } else {
            Log.e("엑세스토큰", "만료")

            //엑세스토큰이 만료되었음으로 리프레쉬토큰 유효성검사 실시
            var check = false
            GlobalApplication.userDataBase.getRefreshTokenExpire().let { refreshTokenExpire ->
//                val fiveDay = getFiveDaysAgo()

                //리프레쉬 토큰이 유효하다면 서버로부터 엑세스 토큰 및 리프레쉬 토큰 재갱신
                if (refreshTokenExpire * 1000L > currentUTC) {
                    Log.e("리프레쉬 토큰", "유효함")
                    val map = HashMap<String, Any>()

                    try {
                        GlobalApplication.userDataBase.getRefreshToken()?.let { refreshToken ->
                            map.put(GlobalApplication.REFRESH_TOKEN, refreshToken)
                            val userData = ApiGenerator.retrofit.create(ApiService::class.java)
                                .refreshToken(GlobalApplication.userBuilder.createUUID, map)
                                .subscribeOn(Schedulers.io())
                                .blockingGet()

                            userData?.data?.token?.let { token ->
                                token.accessToken?.let { accessToken ->
                                    GlobalApplication.userDataBase.setAccessToken(accessToken)
                                    //유저 정보가 재갱신 되어야하기 때문에 loginCheck =true
                                    decodeAccessToken(accessToken, splashCheck, true)
                                    check = true
                                }
                                token.refreshToken?.let { refreshToken ->
                                    GlobalApplication.userDataBase.setRefreshToken(refreshToken)
                                }
                            }
                        }
                    } catch (e: Exception) {

                    }
                } else {
                    Log.e("리프레쉬 토큰 만료 체크", "만료됨")
                    GlobalApplication.userDataBase.setAccessToken(null)
                    GlobalApplication.userDataBase.setRefreshToken(null)
                    GlobalApplication.userDataBase.setAccessTokenExpire(0)
                    GlobalApplication.userDataBase.setRefreshTokenExpire(0)
                    GlobalApplication.userInfo = UserInfo()
                    check = false
                }
            }
            return check
        }
    }

    //한번 회원가입을 진행하고나서 로그인을 계속 유지시키기 위한 method
    fun settingUserInfo(splashCheck: Boolean = false): Boolean {
        //엑세스 토큰이 내장 DB에 저장되어져 있다면 로그인을 한 상태.
        var check = false
        GlobalApplication.userDataBase.getAccessToken()?.let { token ->
            GlobalApplication.userDataBase.getAccessTokenExpire().let { expire ->
                if (expire >= 0) {
                    check = checkExpireOfAccessToken(expire, splashCheck)
                }
            }
        }
        Log.e("check", check.toString())
        return check
    }
}