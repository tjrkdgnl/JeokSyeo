package com.service

import android.util.Base64
import android.util.Log
import com.application.GlobalApplication
import com.error.ErrorManager
import com.model.user.UserInfo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object JWTUtil {
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"
    private var compositeDisposable =CompositeDisposable()

    //엑세스토큰 JWT decode
    fun decodeAccessToken(
        accessToken: String?
    ) {
        try {
            val split = accessToken?.split(".")
//            Log.e("Decode accessToken", getJson(split?.get(1)))
            jsonParsing(getJson(split?.get(1)), ACCESS_TOKEN)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    //리프레쉬토큰 JWT decode
    fun decodeRefreshToken(
        refreshToken: String?
    ) {
        try {
            val split = refreshToken?.split(".")
//            Log.e("Decode refreshToken", getJson(split?.get(1)))
            jsonParsing(getJson(split?.get(1)), REFRESH_TOKEN)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    //json value get
    private fun getJson(str: String?): String {
        val decodedByte = Base64.decode(str, Base64.URL_SAFE)

        return String(decodedByte, Charset.defaultCharset()) //default charset은 안드로이드 기준 항상 UTF-8
    }

    //json parsing
    private fun jsonParsing(
        json: String,
        token: String
    ) {
        val jsonObject = JSONObject(json)

        if (token == ACCESS_TOKEN) { //엑세스 토큰 안에 존재하는 만료시간을 내장 디비에 저장
            GlobalApplication.userDataBase.setAccessTokenExpire(jsonObject.getLong("exp")  * 1000L)

        } else {
            GlobalApplication.userDataBase.setRefreshTokenExpire(jsonObject.getLong("exp") * 1000L)
        }
    }

    private fun getCurrentUTC(): Long {
        val currentUTC = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val utc = simpleDateFormat.parse(simpleDateFormat.format(currentUTC))

        return utc?.time ?: 0
    }

    //토큰 만료시간을 체크
    private suspend fun checkExpireOfAccessToken(expire: Long): Boolean =
        suspendCoroutine { coroutineResult ->
            val accessTokenExpire: Long = expire
            val currentUTC = getCurrentUTC()

            //엑세스 토큰이 만료되었거나, 만료되기 1시간 전이라면 리프레쉬 토큰을 통해 갱신
            if(accessTokenExpire <= currentUTC || accessTokenExpire <= currentUTC+3600000L){
                Log.e("엑세스토큰", "만료")

                //엑세스토큰이 만료되었음으로 리프레쉬토큰 유효성검사 실시
                GlobalApplication.userDataBase.getRefreshTokenExpire().let { refreshTokenExpire ->

                    //리프레쉬 토큰이 유효하다면 서버로부터 엑세스 토큰 및 리프레쉬 토큰 재갱신
                    if (refreshTokenExpire > currentUTC) {
                        Log.e("리프레쉬 토큰", "유효함")
                        val map = HashMap<String, Any>()

                        try {
                            GlobalApplication.userDataBase.getRefreshToken()?.let { refreshToken ->
                                map[GlobalApplication.REFRESH_TOKEN] = refreshToken
                                compositeDisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
                                    .refreshToken(GlobalApplication.userBuilder.createUUID, map)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe ( { userData ->
                                        userData?.data?.token?.let { token ->
                                            token.accessToken?.let { accessToken ->
                                                decodeAccessToken(accessToken)
                                                coroutineResult.resume(true)
                                            }
                                        }
                                    },{e ->
                                        Log.e(ErrorManager.TOKEN_REFRESH,e.message.toString())
                                        coroutineResult.resume(false)

                                    }))
                            }
                        } catch (e: Exception) {
                            Log.e(ErrorManager.TOKEN_REFRESH +"catch",e.message.toString())
                            coroutineResult.resume(false)
                        }
                    } else {
                        Log.e("리프레쉬 토큰 만료 체크", "만료됨")
                        GlobalApplication.userDataBase.setAccessToken(null)
                        GlobalApplication.userDataBase.setRefreshToken(null)
                        GlobalApplication.userDataBase.setAccessTokenExpire(0)
                        GlobalApplication.userDataBase.setRefreshTokenExpire(0)
                        GlobalApplication.userInfo = UserInfo()
                        coroutineResult.resume(false)
                    }
                }

            }
            else if (accessTokenExpire > currentUTC) {
                Log.e("엑세스토큰", "유효함")
                coroutineResult.resume(true)
            }
        }

    //한번 회원가입을 진행하고나서 로그인을 계속 유지시키기 위한 method
    suspend fun settingUserInfo(): Boolean {
        //엑세스 토큰이 내장 DB에 저장되어져 있다면 로그인을 한 상태.
        var check = false
        GlobalApplication.userDataBase.getAccessToken()?.let {
            //0이 포함되는 이유는 만료시간 default 0이기때문에 초기화를 진행하기 위함
            val expire = GlobalApplication.userDataBase.getAccessTokenExpire()
            if (expire >= 0) {
                check = checkExpireOfAccessToken(expire)
            }
        }
        return check
    }
}