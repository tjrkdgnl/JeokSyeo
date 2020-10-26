package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alchol_ranking.AlcholList
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestActivity:AppCompatActivity() {
     var list:MutableList<AlcholList>? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:TestActivityBinding = DataBindingUtil.setContentView(this, R.layout.test_activity)

        CoroutineScope(Dispatchers.IO).launch {
          val value=  ApiGenerator.retrofit.create(ApiService::class.java)
                .getAlcholRanking(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
                .subscribe({
                    list = it.data?.alcholList?.toMutableList()

                },{t -> Log.e("에러임",t.message.toString())})

            withContext(Dispatchers.Main){
                Log.e("값",list?.size.toString())
            }
        }
    }
}