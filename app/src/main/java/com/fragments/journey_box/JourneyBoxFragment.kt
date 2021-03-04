package com.fragments.journey_box

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

        //웹상에서 키패드가 올라올 시, 바텀네비게이션 뷰를 hide or show하기 위해서 선언
        //키패드 감지하는 기능을 만들어놓은 라이브러리를 사용하여 감지
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
            this.javaScriptEnabled= true    //웹뷰가 자바스크립트를 사용할 수 있도록 허용
            this.domStorageEnabled= true    //javaScript의 dom 객체사용 허가
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

                //webChromeClient를 셋팅함으로써 웹뷰 내에서 팝업창을 띄울 수 있음.
                //chromeClient를 통해서 이니시스 결제모듈을 띄움
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

        //webview 할당 -> 기본 webview 띄우기
        binding.JourneyBoxWebView.loadUrl(resources.getString(R.string.journey_box_url))
    }

    override fun detachPresenter() {

    }

    //웹뷰의 depth를 확인
    fun canGoBack() = binding.JourneyBoxWebView.canGoBack()

    //웹뷰 뒤로가기 실행
    fun goBack(){
        binding.JourneyBoxWebView.goBack()
    }
}