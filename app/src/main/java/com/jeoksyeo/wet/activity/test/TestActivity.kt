package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.model.alchol_ranking.AlcholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : AppCompatActivity() {
    var list: MutableList<AlcholList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: TestActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.test_activity)

        val calendar = Calendar.getInstance()

        val currentUTC = calendar.time
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val utc = simpleDateFormat.parse(simpleDateFormat.format(currentUTC))

        calendar.add(Calendar.DATE,-5)

        val fiveDay = simpleDateFormat.parse(simpleDateFormat.format(calendar.time))

        Log.e("fiveDay", fiveDay?.time.toString())
        Log.e("utc", utc?.time.toString())

    }
}