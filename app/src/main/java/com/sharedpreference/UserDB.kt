package com.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class UserDB(private val context: Context) {

    companion object {
        var instance: UserDB? = null
        private const val PREFERENCE_NAME = "user_db"
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
        private const val EXPIRE_ACCESS_TOKEN = "expire_accessToken"
        private const val EXPIRE_REFRESH_TOKEN = "expire_refreshToken"
        private const val MY_SEARCH = "my_search"
        private const val KEYWORD = "keyword"


        fun getInstance(context: Context): UserDB {
            if (instance == null) {
                instance = UserDB(context)
            }
            return instance as UserDB
        }
    }

    fun getSharedPreference(): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setAccessToken(accessToken: String?) {
        getSharedPreference().edit().putString(ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(): String? = getSharedPreference().getString(ACCESS_TOKEN, null)

    fun setRefreshToken(refreshToken: String?) {
        getSharedPreference().edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String? = getSharedPreference().getString(REFRESH_TOKEN, null)

    fun setAccessTokenExpire(expire: Long?) {
        expire?.let {
            getSharedPreference().edit().putLong(EXPIRE_ACCESS_TOKEN, it).apply()
        }
    }

    fun getAccessTokenExpire(): Long = getSharedPreference().getLong(EXPIRE_ACCESS_TOKEN, 0)

    fun setRefreshTokenExpire(expire: Long?) {
        expire?.let {

            getSharedPreference().edit().putLong(EXPIRE_REFRESH_TOKEN, it).apply()
        }
    }

    fun getRefreshTokenExpire(): Long = getSharedPreference().getLong(EXPIRE_REFRESH_TOKEN, 0)

    fun getKeywordList(): MutableList<String>? {
        val keywordJson = getSharedPreference().getString(MY_SEARCH, null)

        return keywordJson?.let {
            val jsonArray = JSONArray(keywordJson)
            val keywordList = mutableListOf<String>()
            for (i in jsonArray.length() - 1 downTo 0) {
                val jsonObject = jsonArray.getJSONObject(i)
                keywordList.add(jsonObject[KEYWORD] as String)
            }
            keywordList
        }
    }

    fun setKeyword(alcholKeyword: String) {
        val keywordString = getSharedPreference().getString(MY_SEARCH, null)

        //내장디비에 저장된 검색어가 있는지 확인
        keywordString?.let { string ->
            val keywordJSonList = JSONArray(string)

            val keywordList = mutableListOf<String>()
            var duplicate = false

            //저장된 jsonArray를 String array로 변환
            for (i in 0 until keywordJSonList.length()) {
                val jsonObject = keywordJSonList.getJSONObject(i)

                keywordList.add(jsonObject[KEYWORD] as String)
            }

            //새롭게 추가 될 키워드가 중복되는지 확인
            for (keyword in keywordList) {
                if (keyword == alcholKeyword) {
                    return
                }
            }

            //새롭게 키워드 추가 후에 다시 jsonArray로 변경해서 내장디비에 저장
            if (!duplicate) {
                keywordList.add(alcholKeyword)

                val jsonArray = JSONArray()

                for (keyword in keywordList) {
                    val jsonObject = JSONObject()

                    try {
                        jsonObject.put(KEYWORD, keyword)
                        jsonArray.put(jsonObject)
                    } catch (e: Exception) {
                        Log.e("JsonArray error", e.message.toString())
                    }
                }
                getSharedPreference().edit().putString(MY_SEARCH, jsonArray.toString()).apply()
            }
        } ?: createKeywordList(alcholKeyword) //저장된 내장디비가 없으면 새롭게 생성해서 저
    }

    private fun createKeywordList(keyword: String) {
        val jsonArray = JSONArray()

        val jsonObject = JSONObject()
        jsonObject.put(KEYWORD, keyword)

        jsonArray.put(jsonObject)

        getSharedPreference().edit().putString(MY_SEARCH, jsonArray.toString()).apply()
    }

    fun deleteKeyword(keyword: String):Boolean {
        val keywordString = getSharedPreference().getString(MY_SEARCH, null)
        var confirmDeleteCheck = false

        //내장디비에 저장된 검색어가 있는지 확인
        keywordString?.let { string ->
            val keywordJSonList = JSONArray(string)

            val keywordList = mutableListOf<String>()

            //저장된 jsonArray를 String array로 변환
            for (i in 0 until keywordJSonList.length()) {
                val jsonObject = keywordJSonList.getJSONObject(i)

                keywordList.add(jsonObject[KEYWORD] as String)
            }

            //키워드 삭제
            for(alcoholName in keywordList.withIndex()){
                if(alcoholName.value == keyword){
                    keywordList.removeAt(alcoholName.index)
                    confirmDeleteCheck =true
                    break
                }
            }

            if(keywordList.size ==0){
                getSharedPreference().edit().putString(MY_SEARCH,null).apply()
            }
            else{
                val jsonArray = JSONArray()
                //삭제 후 다시 저장.
                for (name in keywordList) {
                    val jsonObject = JSONObject()

                    try {
                        jsonObject.put(KEYWORD, name)
                        jsonArray.put(jsonObject)
                    } catch (e: Exception) {
                        Log.e("JsonArray error", e.message.toString())
                    }
                }
                getSharedPreference().edit().putString(MY_SEARCH, jsonArray.toString()).apply()
            }
        }
        return confirmDeleteCheck
    }
}