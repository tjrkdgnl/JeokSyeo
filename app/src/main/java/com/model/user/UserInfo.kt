package com.model.user

import android.util.Log
import com.application.GlobalApplication
import java.util.*
import kotlin.collections.HashMap

class UserInfo {
    private var user_id: String? = null
    private var level: Int = 0
    private var oauth_token: String? = null //소셜 로그인 토큰값
    private var provider: String? = null
    private var email: String? = null
    lateinit var nickName: String
    lateinit var birthDay: String
    lateinit var gender: String
    private lateinit var address: String
    private var accessToken: String? = null
    private var refreshToken: String? = null
    var profileImg: List<Profile>? = null
    private var infoMap = HashMap<String, Any>()


    fun getAccessToken() = accessToken
    fun getRefreshToken() = refreshToken
    fun getProfile() = profileImg
    fun getProvider() = provider
    fun getLevel() = level

    fun init() {
        user_id = null
        oauth_token = null
        provider = null
        email = null
        accessToken = null
        refreshToken = null
        profileImg = null
        infoMap = HashMap<String, Any>()
    }

    class Builder() {
        private var user_id: String? = null
        private var oauth_token: String? = null
        private var provider: String? = null
        private var email: String? = null
        private lateinit var nickName: String
        private lateinit var birthDay: String
        private lateinit var gender: String
        private lateinit var address: String
        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var profileImg: List<Profile>? = null
        private var infoMap = HashMap<String, String?>()

        private var level: Int = 0


        fun getMap(): HashMap<String, String?> {
            infoMap.put(GlobalApplication.OAUTH_PROVIDER, provider)
            infoMap.put(GlobalApplication.OAUTH_TOKEN, oauth_token)
            infoMap.put(GlobalApplication.USER_ID, user_id)
            infoMap.put(GlobalApplication.GENDER, gender)
            infoMap.put(GlobalApplication.NICKNAME, nickName)
            infoMap.put(GlobalApplication.BIRTHDAY, birthDay)
            infoMap.put(GlobalApplication.ADDRESS, address)

            return infoMap
        }

        fun setOAuthId(user_id: String?): Builder {
            this.user_id = user_id
            return this
        }

        fun setLevel(level: Int): Builder {
            this.level = level
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

        fun setNickName(nickName: String): Builder {
            this.nickName = nickName
            return this
        }

        fun setBirthDay(birthday: String): Builder {
            this.birthDay = birthday
            return this
        }

        fun setGender(gender: String): Builder {
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

        fun setProfile(profileImg: List<Profile>?): Builder {
            this.profileImg = profileImg
            return this
        }

        fun setAddress(address: String): Builder {
            this.address = address

            return this
        }

        //회원가입 1단계를 위해서 public get 설정
        fun getOAuthToken() = this.oauth_token
        fun getProvider() = this.provider

        val createUUID: String
            get() {
                val uuid = UUID.randomUUID().toString()
                Log.e("UUID", uuid)
                return uuid
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
            userInfo.profileImg = this.profileImg
            userInfo.address = this.address
            userInfo.level = this.level


//
//            Log.e("oauth_provider", userInfo.provider.toString())
//            Log.e("oauth_token", userInfo.oauth_token.toString())
//            Log.e("user_id", userInfo.user_id.toString())
//            Log.e("nickname", userInfo.nickName.toString())
//            Log.e("birth", userInfo.birthDay.toString())
//            Log.e("gender", userInfo.gender.toString())
//            Log.e("address", this.address.toString())
//            Log.e("map개수", userInfo.infoMap.size.toString())

            return userInfo
        }
    }
}