package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TestActivity : AppCompatActivity() {
    var disposable: Disposable? = null
    lateinit var binding: TestActivityBinding

    val retrofit = ApiGenerator.retrofit.create(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val interClass = InterPractice.InterClass(1,2)

        interClass.left



        binding = DataBindingUtil.setContentView(this, R.layout.test_activity)

        binding.button.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (ex()) {
                    Log.e("성공", "성공")
                    binding.text.text = "변경완료"
                } else {
                    Log.e("실패", "실패")
                }
            }
        }
    }

   suspend fun callMethod(): Boolean {
       var bool = executeMethod()

        return bool
    }

    suspend fun ex():Boolean{
      return  CoroutineScope(Dispatchers.IO).async {
            true
        }.await()
    }

    suspend fun executeMethod(): Boolean = suspendCoroutine {
        disposable = retrofit
            .getArea(GlobalApplication.userBuilder.createUUID, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({result->
                it.resume(true)
            }, { t: Throwable? ->Log.e("retrofit fail",t?.message.toString())})
    }
}

