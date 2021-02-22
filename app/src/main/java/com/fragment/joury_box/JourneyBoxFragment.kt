package com.fragment.joury_box

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.MainViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.JourneyBoxBinding
import gun0912.tedkeyboardobserver.TedRxKeyboardObserver

class JourneyBoxFragment: BaseFragment<JourneyBoxBinding>() {
    override val layoutResID: Int = R.layout.journey_box
    private lateinit var viewModel: MainViewModel


    @SuppressLint("SetJavaScriptEnabled", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        TedRxKeyboardObserver(requireActivity())
            .listen()
            .subscribe({isShowing->
                if(isShowing){
                    viewModel.bottomNavigationViewVisiblity.value =1
                }
                else{
                    viewModel.bottomNavigationViewVisiblity.value =0
                }
            },{})

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
                    this.javaScriptEnabled = true
                }

                val dialog = Dialog(this@JourneyBoxFragment.requireActivity())
                dialog.setContentView(newWebView)
                dialog.show()

                newWebView.webChromeClient = object :WebChromeClient(){
                    override fun onCloseWindow(window: WebView?) {
                        dialog.dismiss()
                    }
                }

                (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }

        //webview 할당
        binding.JourneyBoxWebView.loadUrl(resources.getString(R.string.journey_box_url))

    }

    fun canGoBack() = binding.JourneyBoxWebView.canGoBack()

    fun goBack(){
        binding.JourneyBoxWebView.goBack()
    }

}