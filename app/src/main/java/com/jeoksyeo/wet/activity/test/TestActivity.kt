package com.jeoksyeo.wet.activity.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.TestActivityBinding

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: TestActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.test_activity)


        binding.button.setOnClickListener {
            throw RuntimeException("test crash")
        }

    }
}