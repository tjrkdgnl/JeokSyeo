package com.model

import android.util.Log
import com.application.GlobalApplication
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class UserInfo {
    private var user_id: String? = null
    private var oauth_token: String? = null
    private var provider: String? = null
    private var email: String? = null
    private var nickName: String? = null
    private var birthDay: String? = null
    private var gender: String? = null
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var profileURL: String? = null
    private var infoMap = HashMap<String, Any>()
    private var address:String? =null

    fun getMap() = infoMap

    fun getAccessToken() = accessToken
    fun getRefreshToken() = refreshToken

    class Builder(private var id: String) {
        private var user_id: String? = null
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
        private var address:String? =null

        fun setOAuthId(user_id: String?): Builder {
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

        fun setAddress(address:String){
            this.address =address
        }

        //회원가입 1단계를 위해서 public get 설정
        fun getOAuthToken() = this.oauth_token
        fun getProvider() = this.provider
        val createUUID: String
            get() = UUID.randomUUID().toString()

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
            userInfo.address = this.address

            this.provider?.let { userInfo.infoMap.put(GlobalApplication.OAUTH_PROVIDER, it) }
            this.oauth_token?.let { userInfo.infoMap.put(GlobalApplication.OAUTH_TOKEN, it) }
            this.user_id?.let { userInfo.infoMap.put(GlobalApplication.USER_ID, it) }
            this.gender?.let { userInfo.infoMap.put(GlobalApplication.GENDER, it) }
            this.nickName?.let { userInfo.infoMap.put(GlobalApplication.NICKNAME, it) }
            this.birthDay?.let { userInfo.infoMap.put(GlobalApplication.BIRTHDAY, it) }
            this.address?.let { userInfo.infoMap.put(GlobalApplication.ADDRESS,it) }

            Log.e("user_id", userInfo.user_id.toString())
            Log.e("oauth_provider", userInfo.provider.toString())
            Log.e("oauth_token", userInfo.oauth_token.toString())
            Log.e("nickname", userInfo.nickName.toString())
            Log.e("birth", userInfo.birthDay.toString())
            Log.e("gender", userInfo.gender.toString())
            Log.e("map개수", userInfo.infoMap.size.toString())

            return userInfo
        }
    }
}