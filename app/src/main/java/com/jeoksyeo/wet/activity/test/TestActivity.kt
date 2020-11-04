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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class TestActivity : AppCompatActivity() {
    var list: MutableList<AlcholList>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: TestActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.test_activity)

        Log.e("value",exexcuteRx().toString())
    }

    fun exexcuteRx() :Int{
        var value = 0
        val dispo = ApiGenerator.retrofit.create(ApiService::class.java)
            .getAlcholRanking(GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken())
            .subscribeOn(Schedulers.io())
            .blockingSingle()

        return value
    }


//
//     fun executeCor():Int{
//        var value =0
//
//        CoroutineScope(Dispatchers.Main).launch {
//           val i = async(Dispatchers.IO) {
//               ApiGenerator.retrofit.create(ApiService::class.java)
//                   .getAlcholRanking(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken())
//                   .subscribe({
//                       list = it.data?.alcholList?.toMutableList()
//                       value = list?.size!!
//                   },{t -> Log.e("에러임",t.message.toString())})
//               value
//           }
//            Log.e("성공",i.await().toString())
//        }
//         return value
//    }
}