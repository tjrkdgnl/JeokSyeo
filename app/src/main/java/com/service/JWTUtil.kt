package com.service

import android.util.Base64
import android.util.Log
import com.application.GlobalApplication
import com.model.user.UserInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object JWTUtil {
    private val compositeDisposable = CompositeDisposable()
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"

    //엑세스토큰 JWT decode
    @Throws(Exception::class)
    fun decodeAccessToken(accessToken: String?) {
        try {
            val split = accessToken?.split(".")
            Log.e("Decode accessToken", getJson(split?.get(1)))
            jsonParsing(getJson(split?.get(1)), ACCESS_TOKEN)
        } catch (e: UnsupportedEncodingException) {
            e.stackTrace
        }
    }

    //리프레쉬토큰 JWT decode
    @Throws(Exception::class)
    fun decodeRefreshToken(refreshToken: String?) {
        try {
            val split = refreshToken?.split(".")
            Log.e("Decode refreshToken", getJson(split?.get(1)))
            jsonParsing(getJson(split?.get(1)), REFRESH_TOKEN)
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
    private fun jsonParsing(json: String, token: String) {
        val jsonObject = JSONObject(json)

        //토큰 만료시간 셋팅
        if (token.equals(ACCESS_TOKEN)) {
            GlobalApplication.userDataBase.setAccessTokenExpire(jsonObject.getLong("exp"))
            GlobalApplication.userInfo = UserInfo.Builder("")
                .setOAuthId(jsonObject.getString("user_id"))
                .setProvider(jsonObject.getString("oauth_provider"))
                .setNickName(jsonObject.getString("nickname"))
                .setEmail(jsonObject.getString("email"))
                .setBirthDay(jsonObject.getString("birth"))
                .setGender(jsonObject.getString("gender"))
                .build()
        } else {
            GlobalApplication.userDataBase.setRefreshTokenExpire(jsonObject.getLong("exp"))
        }
    }

    private fun getCurrentUTC(): Long {
        val currentUtc = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val utc = simpleDateFormat.parse(simpleDateFormat.format(currentUtc))

        Log.e("utc", utc?.time.toString())
        return utc.time
    }

    //토큰 만료시간을 체크
    private fun checkExpireOfAccessToken(accessToken: Long) {
        val accessTokenExpire: Long = accessToken * 1000L
        val currentUTC = getCurrentUTC()

        //엑세스토큰 유효성 검사
        if (accessTokenExpire > currentUTC) {
            Log.e("엑세스토큰", "유효함")
            GlobalApplication.userDataBase.getAccessToken()?.let {
                //유효하면 userinfo를 위해 setting해줌
                GlobalApplication.userBuilder.setAccessToken(it)
                GlobalApplication.userBuilder.setRefreshToken(GlobalApplication.userDataBase.getRefreshToken())
                decodeAccessToken(it)
            }
        } else {
            //엑세스토큰이 만료되었음으로 리프레쉬토큰 유효성검사 실시
            GlobalApplication.userDataBase.getRefreshTokenExpire().let { refreshTokenExpire ->
                val fiveDay = 432000000L

                //리프레쉬 토큰이 유효하다면 서버로부터 엑세스 토큰 및 리프레쉬 토큰 재갱신
                if ((currentUTC - fiveDay) < refreshTokenExpire || refreshTokenExpire * 1000L > currentUTC) {
                    Log.e("리프레쉬 토큰", "유효함")
                    Log.e(
                        "리프레쉬토큰만료시간 확인",
                        GlobalApplication.userDataBase.getRefreshTokenExpire().toString()
                    )

                    val map = HashMap<String, Any>()
                    GlobalApplication.userDataBase.getRefreshToken()?.let { refreshToken ->
                        map.put(GlobalApplication.REFRESH_TOKEN, refreshToken)

                        compositeDisposable.add(
                            ApiGenerator.retrofit.create(ApiService::class.java)
                                .refreshToken(GlobalApplication.userBuilder.createUUID, map)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Log.e("토큰 갱신", "성공")
                                    GlobalApplication.userDataBase.setAccessToken(it.data?.token?.accessToken)
                                    GlobalApplication.userDataBase.setRefreshToken(it.data?.token?.refreshToken)
                                    decodeAccessToken(it.data?.token?.accessToken)

                                }, { t: Throwable? -> t?.stackTrace })
                        )
                    }
                } else {
                    Log.e("리프레쉬 토큰 만료 체크", "만료됨")
                    GlobalApplication.userDataBase.setAccessToken(null)
                    GlobalApplication.userDataBase.setRefreshToken(null)
                    GlobalApplication.userDataBase.setAccessTokenExpire(0)
                    GlobalApplication.userDataBase.setRefreshTokenExpire(0)
                }
            }
        }
    }

    //한번 회원가입을 진행하고나서 로그인을 계속 유지시키기 위한 method
    fun settingUserInfo() {
        //엑세스 토큰이 내장 DB에 저장되어져 있다면 로그인을 한 상태.
        Log.e("엑세스토큰 확인", GlobalApplication.userDataBase.getAccessToken().toString())
        Log.e("엑세스토큰만료시간 확인", GlobalApplication.userDataBase.getAccessTokenExpire().toString())
        GlobalApplication.userDataBase.getAccessTokenExpire().let { expire ->
            if (expire > 0) {
                checkExpireOfAccessToken(expire)
            }
        }
    }
}