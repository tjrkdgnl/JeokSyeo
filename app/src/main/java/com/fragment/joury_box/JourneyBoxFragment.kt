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
    private var bindObj:JourneyBoxBinding?=null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindObj = DataBindingUtil.inflate(inflater, R.layout.journey_box,container,false)
        binding = bindObj!!

        binding.JourneyBoxWebView.settings.javaScriptEnabled= true
        binding.JourneyBoxWebView.settings.domStorageEnabled= true

        //클라이언트를 할당해야 app안에서 web을 띄울 수 있는 권한이 주어짐. 안그러면 web으로 넘어가버림
        binding.JourneyBoxWebView.webViewClient = object :WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                Log.e("current Url",url.toString())
            }
        }

        binding.JourneyBoxWebView.loadUrl(resources.getString(R.string.journey_box_url))
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj=null
    }
}