package com.jeoksyeo.wet.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MainBinding
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.MyStatus
import model.UserInfo
import service.ApiGenerator
import service.ApiService

class MainActivity : AppCompatActivity(){
    lateinit var disposable: Disposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : MainBinding  = DataBindingUtil.setContentView(this, R.layout.main)

    }


    override fun onDestroy() {
        super.onDestroy()

    }
}