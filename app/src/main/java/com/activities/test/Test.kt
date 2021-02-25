package com.activities.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.application.GlobalApplication
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.databinding.TestActivityBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.await
import kotlin.coroutines.resume

class Test : AppCompatActivity() {
    var disposable: Disposable? = null

    val retrofit = ApiGenerator.retrofit.create(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        CoroutineScope(Dispatchers.Main.immediate).launch {

            val def = getMethod()

            val value = def.await()


            Log.e("완료", "전달받은 값 ${value.data?.areaList?.size}") }
    }

    fun getMethod() = CoroutineScope(Dispatchers.IO).async {

        Log.e("비동기 작업", "진행")

        val testApi = retrofit.callbackGetArea(GlobalApplication.userBuilder.createUUID, null)

        testApi.await()
    }
}