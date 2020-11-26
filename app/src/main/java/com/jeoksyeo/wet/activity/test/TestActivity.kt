package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.model.alcohol_ranking.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : AppCompatActivity() {
    var list: MutableList<AlcoholList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: TestActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.test_activity)


    }
}