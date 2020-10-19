package com.model

import android.util.Log
import com.application.GlobalApplication
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class UserInfo {
    var user_id:String? =null
    var oauth_token: String? = null
    var provider: String? = null
    var email: String? = null
    var nickName: String? = null
    var birthDay: String? = null
    var gender: String? = null
    var accessToken: String? = null
    var refreshToken: String? = null
    var profileURL: String? = null
    var infoMap = HashMap<String, Any>()

    val createUUID: String
        get() = UUID.randomUUID().toString()

    fun settingMap(){
        this.user_id?.let { infoMap.put("user_id",it) }
        this.provider?.let {infoMap.put("oauth_provider", it) }
        this.oauth_token?.let { infoMap.put("oauth_token", it) }
        this.nickName?.let {infoMap.put(GlobalApplication.NICKNAME, it) }
        this.birthDay?.let { infoMap.put(GlobalApplication.BIRTHDAY, it) }
        this.gender?.let { infoMap.put(GlobalApplication.GENDER, it) }
        Log.e("생일",this.birthDay.toString())
    }


    class Builder(private var id: String) {
        private var user_id:String? =null
        private var oauth_token: String? = null
        private var provider: String? = null
        private var email: String? = null
        private var nickName: String? = null
        private var birthDay: String? = null
        private var gender: String? = null
        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var profileImgFile: File? = null
        private var profileImgURL: String? = null

        fun setOAuthId(user_id:String?):Builder{
            this.user_id = user_id
            return this
        }

        fun setOAuthToken(oauth_token: String?): Builder {
            this.oauth_token = oauth_token
            return this
        }

        fun setProvider(provider: String?): Builder {
            this.provider = provider
            return this
        }

        fun setEmail(email: String?): Builder {
            this.email = email
            return this
        }

        fun setNickName(nickName: String?): Builder {
            this.nickName = nickName
            return this
        }

        fun setBirthDay(birthday: String?): Builder {
            this.birthDay = birthday
            return this
        }

        fun setGender(gender: String?): Builder {
            this.gender = gender
            return this
        }

        fun setAccessToken(accessToken: String?): Builder {
            this.accessToken = accessToken
            return this
        }

        fun setRefreshToken(refreshToken: String?): Builder {
            this.refreshToken = refreshToken
            return this
        }

        fun setProfileFile(profileFile: File?): Builder {
            this.profileImgFile = profileFile
            return this
        }

        fun setProfileImgURL(profileImg: String?): Builder {
            this.profileImgURL = profileImg
            return this
        }

        fun build(): UserInfo {
            var userInfo = UserInfo()
            userInfo.user_id = this.user_id
            userInfo.oauth_token = this.oauth_token
            userInfo.provider = this.provider
            userInfo.email = this.email
            userInfo.nickName = this.nickName
            userInfo.birthDay = this.birthDay
            userInfo.gender = this.gender
            userInfo.accessToken = this.accessToken
            userInfo.refreshToken = this.refreshToken
            userInfo.profileURL = this.profileImgURL

            this.user_id?.let { userInfo.infoMap.put("user_id",it) }
            this.provider?.let { userInfo.infoMap.put("oauth_provider", it) }
            this.oauth_token?.let { userInfo.infoMap.put("oauth_token", it) }
            this.email?.let { userInfo.infoMap.put("email", it) }
            this.nickName?.let { userInfo.infoMap.put(GlobalApplication.NICKNAME, it) }
            this.birthDay?.let { userInfo.infoMap.put(GlobalApplication.BIRTHDAY, it) }
            this.gender?.let { userInfo.infoMap.put(GlobalApplication.GENDER, it) }
            this.profileImgURL?.let { userInfo.infoMap.put("profile_image_url", it) }

            Log.e("user_id", userInfo.user_id.toString())
            Log.e("oauth_provider", userInfo.provider.toString())
            Log.e("oauth_token", userInfo.oauth_token.toString())
            Log.e("email", userInfo.email.toString())
            Log.e("nickname", userInfo.nickName.toString())
            Log.e("birth", userInfo.birthDay.toString())
            Log.e("gender", userInfo.gender.toString())
            Log.e("profile_image_url", userInfo.profileURL.toString())
            Log.e("map개수",userInfo.infoMap.size.toString())

            return userInfo
        }
    }
}