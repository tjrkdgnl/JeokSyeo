package com.fragment.joury_box

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
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

        binding.JourneyBoxWebView.settings.apply {
            this.javaScriptEnabled= true
            this.setSupportMultipleWindows(true)
            this.javaScriptCanOpenWindowsAutomatically =true
            this.domStorageEnabled= true
        }

        //클라이언트를 할당해야 app안에서 web을 띄울 수 있는 권한이 주어짐. 안그러면 web으로 넘어가버림
        binding.JourneyBoxWebView.webViewClient =WebViewClient()



        //webview 내에서 팝업창 띄우기
        binding.JourneyBoxWebView.webChromeClient = object :WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {

                val newWebView = WebView(this@JourneyBoxFragment.requireContext())
                newWebView.settings.apply {
                    javaScriptEnabled = true
                }


                val dialog = Dialog(this@JourneyBoxFragment.requireActivity())
                dialog.setContentView(newWebView)
                dialog.show()

                newWebView.webChromeClient = object :WebChromeClient(){
                    override fun onCloseWindow(window: WebView?) {
                        dialog.dismiss()
                    }

                }

                newWebView.webViewClient = object :WebViewClient(){
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return false
                    }
                }

                (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }

        //webview 할당
        binding.JourneyBoxWebView.loadUrl(resources.getString(R.string.journey_box_url))
        return binding.root
    }

    fun canGoBack() = binding.JourneyBoxWebView.canGoBack()

    fun goBack(){
        binding.JourneyBoxWebView.goBack()
    }


    override fun onDestroy() {
        super.onDestroy()
        bindObj=null
    }
}