package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class Test :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val i = 3

        a(i)

        Log.e("인티저",i.toString())

    }

    fun a(i:Int){

    }
}