package com.fragment.joury_box

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.JourneyBoxBinding

class JourneyBoxFragment: Fragment() {
    private lateinit var binding:JourneyBoxBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.journey_box,container,false)

        binding.JourneyBoxWebView.settings.javaScriptEnabled= true
        binding.JourneyBoxWebView.webViewClient = object :WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                Log.e("current Url",url.toString())
            }
        }

        binding.JourneyBoxWebView.loadUrl(resources.getString(R.string.journey_box_url))

        return binding.root
    }
}