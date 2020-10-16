package model

import android.util.Log
import android.view.textclassifier.ConversationActions
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class UserInfo {
    var oauthId: String? = null
    var provider: String? = null
    var email: String? = null
    var nickName: String? = null
    var birthDay: String? = null
    var gender: String? = null
    var accessToken: String? = null
    var refreshToken: String? = null
    var profileURL: String? = null
    var infoMap = HashMap<String, Any>()
    val userInfoList = mutableListOf<Int>()

    val createUUID: String
        get() = UUID.randomUUID().toString()

    class Builder(private var id: String) {
        private var oauthId: String? = null
        private var provider: String? = null
        private var email: String? = null
        private var nickName: String? = null
        private var birthDay: String? = null
        private var gender: String? = null
        private var accessToken: String? = null
        private var refreshToken: String? = null
        private var profileImgFile: File? = null
        private var profileImgURL: String? = null

        fun setOAuthId(id: String?): Builder {
            this.oauthId = id
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
            userInfo.oauthId = this.oauthId
            userInfo.provider = this.provider
            userInfo.email = this.email
            userInfo.nickName = this.nickName
            userInfo.birthDay = this.birthDay
            userInfo.gender = this.gender
            userInfo.accessToken = this.accessToken
            userInfo.refreshToken = this.refreshToken
            userInfo.profileURL = this.profileImgURL

            Log.e("oauth_provider", userInfo.provider.toString())
            Log.e("oauth_id", userInfo.oauthId.toString())
            Log.e("email", userInfo.email.toString())
            Log.e("nickname", userInfo.nickName.toString())
            Log.e("birth", userInfo.birthDay.toString())
            Log.e("gender", userInfo.gender.toString())
            Log.e("profile_image_url", userInfo.profileURL.toString())

            if(userInfo.nickName ==null)
                userInfo.userInfoList.add(0)

            if(userInfo.birthDay ==null){
                userInfo.userInfoList.add(1)
            }

            if(userInfo.birthDay ==null){
                userInfo.userInfoList.add(2)
            }

            userInfo.provider?.let { userInfo.infoMap.put("oauth_provider", it) }
            userInfo.oauthId?.let { userInfo.infoMap.put("oauth_id", it) }
            userInfo.email?.let { userInfo.infoMap.put("email", it) }
            userInfo.nickName?.let { userInfo.infoMap.put("nickname", it) }
            userInfo.birthDay?.let { userInfo.infoMap.put("birth", it) }
            userInfo.gender?.let { userInfo.infoMap.put("gender", it) }
            userInfo.profileURL?.let { userInfo.infoMap.put("profile_image_url", it) }
            return userInfo
        }
    }
}