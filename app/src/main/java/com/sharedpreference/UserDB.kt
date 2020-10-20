package com.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import kotlin.math.exp

class UserDB(private val context: Context) {

    companion object{
        var instance:UserDB? =null
        private const val PREFERENCE_NAME = "user_db"
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
        private const val EXPIRE_ACCESS_TOKEN = "expire_accessToken"
        private const val EXPIRE_REFRESH_TOKEN = "expire_refreshToken"

        fun getInstance(context: Context):UserDB{
            if(instance==null){
                instance =UserDB(context)
            }
            return instance as UserDB
        }
    }

    fun getSharedPreference():SharedPreferences{
        return context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
    }
    fun setAccessToken(accessToken:String?) {
        getSharedPreference().edit().putString(ACCESS_TOKEN,accessToken).apply()
    }
    fun getAccessToken():String? = getSharedPreference().getString(ACCESS_TOKEN,null)

    fun setRefreshToken(refreshToken:String?){
        getSharedPreference().edit().putString(REFRESH_TOKEN,refreshToken).apply()
    }
    fun getRefreshToken():String? = getSharedPreference().getString(REFRESH_TOKEN,null)

    fun setAccessTokenExpire(expire:Long?) {
        expire?.let {
            getSharedPreference().edit().putLong(EXPIRE_ACCESS_TOKEN, it).apply()
        }
    }
    fun getAccessTokenExpire():Long = getSharedPreference().getLong(EXPIRE_ACCESS_TOKEN,0)

    fun setRefreshTokenExpire(expire:Long?) {
        expire?.let {

            getSharedPreference().edit().putLong(EXPIRE_REFRESH_TOKEN,it).apply()
        }
    }
    fun getRefreshTokenExpire():Long = getSharedPreference().getLong(EXPIRE_REFRESH_TOKEN,0)
}