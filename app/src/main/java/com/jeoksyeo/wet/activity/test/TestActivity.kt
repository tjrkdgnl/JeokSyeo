package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.area.GetAreaData
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: TestActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.test_activity)

        var disposable: Disposable? = null
        val job = Job()

        var bool = false

        CoroutineScope(Dispatchers.IO + job).async {
            delay(1000L)
            disposable = ApiGenerator.retrofit.create(ApiService::class.java)
                .getArea(GlobalApplication.userBuilder.createUUID, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: GetAreaData ->
                    bool = true
                    binding.text.text = bool.toString()
                }, { t: Throwable? -> t?.stackTrace })

            disposable!!.dispose()
        }
    }
}