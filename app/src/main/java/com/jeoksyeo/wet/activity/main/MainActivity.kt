package com.jeoksyeo.wet.activity.main

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.fragment.joury_box.JourneyBoxFragment
import com.fragment.main.MainFragment
import com.fragment.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.viewmodel.MainViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RealMainActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Contract.BaseView,BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: RealMainActivityBinding
    private  var bindObj: RealMainActivityBinding? =null
    private lateinit var presenter: Presenter
    private lateinit var viewModel:MainViewModel
    private var journeyBoxFragment:JourneyBoxFragment? =null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindObj= DataBindingUtil.setContentView(this, R.layout.real_main_activity)
        binding = bindObj!!

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        presenter = Presenter().apply {
            view = this@MainActivity
            activity = this@MainActivity
        }

        //네트워크 감지
        presenter.setNetworkUtil()

        //main fragment set
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, MainFragment())
            .commit()

        //링크로 들어왔을 때 핸들링
        with(Handler(Looper.getMainLooper())) {
            postDelayed({
                presenter.handleDeepLink()
            }, 300)
        }

        //키패드 유무에 따라 바텀네비게이션 hide or show
        viewModel.bottomNavigationViewVisiblity.observe(this, {
            bottomNavigationVisiblity(it)
        })

        binding.navigationBottomBar.setOnNavigationItemSelectedListener(this)
        //default bottomNavigationView 화면 설정
        binding.navigationBottomBar.selectedItemId = R.id.navigation_journal
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.navigation_journey -> {
                if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_journey){
                    if(journeyBoxFragment ==null){
                        journeyBoxFragment = JourneyBoxFragment()
                    }

                    journeyBoxFragment?.let {
                        replaceFragment(it,"journey")
                        showTheJourneyLoginToast()

                    }
                }
                return true
            }

            R.id.navigation_journal -> {
                //현재 보여지는 페이지라면 재 셋팅을 하지 않기위해서 핸들링
                if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_journal){
                    replaceFragment(MainFragment(), "main")
                    cancelTheJourneyLoginToast()
                }
                return true
            }

            R.id.navigation_myPage -> {
                //현재 보여지는 페이지라면 재 셋팅을 하지 않기위해서 핸들링
                if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_myPage){
                    replaceFragment(MyPageFragment(), "mypage")
                    cancelTheJourneyLoginToast()
                }
                return true
            }

            else -> {
                return  false
            }
        }
    }

    override fun onBackPressed() {
        val idx = this@MainActivity.supportFragmentManager.backStackEntryCount - 1


        if(idx>=0){
            val entry = this@MainActivity.supportFragmentManager.getBackStackEntryAt(idx)
            val fragmentName = entry.name

            //bottomNavigationView에 있는 목록은 곧장 앱 종료
            if(fragmentName =="journey"){

                journeyBoxFragment?.canGoBack()?.let { canGo->
                    if(canGo){
                        journeyBoxFragment?.goBack()

                    }
                    else{
                        finish()
                    }


                } ?: finish()
            }
            else {
                //depth가 있는 경우에는 fragment stack에서 pop
                CoroutineScope(Dispatchers.IO).launch {
                    fragmentName.let {fgName->
                        this@MainActivity.supportFragmentManager.popBackStack(
                            fgName, FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    }
                }
            }
        }
        else{
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //네트워크가 다시 연결 됐을 때,
        for (frag in this.supportFragmentManager.fragments) {
            if (frag is MainFragment) {
                frag.initApi(this)
                break
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)

    }

    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj=null
    }

    override fun getBinding(): RealMainActivityBinding {
        return binding
    }

    //메인 액티비티에 프래그먼트 교체
    override fun replaceFragment(fragment: Fragment, name: String,tag:String?) {
        this.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment,tag)
            .addToBackStack(name).commit()
    }

    //저니박스 로그인 토스트 띄우기
    override fun showTheJourneyLoginToast() {
        binding.journeyLoginToast.root.visibility = View.VISIBLE

        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.startOffset =10000
        alphaAnimation.duration = 500
        alphaAnimation.interpolator = AccelerateInterpolator()
        binding.journeyLoginToast.root.animation = alphaAnimation

        binding.journeyLoginToast.root.animation.start()

        binding.journeyLoginToast.root.animation.setAnimationListener(object :
            Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.journeyLoginToast.root.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }

    //저니박스 로그인 토스트 숨기기
    override fun cancelTheJourneyLoginToast() {
        binding.journeyLoginToast.root.animation?.cancel()
        binding.journeyLoginToast.root.visibility = View.INVISIBLE
    }

    //바텀 네비게이션 뷰 show or hide 설정
    override fun bottomNavigationVisiblity(check: Int) {
        if(check ==1){
            binding.navigationBottomBar
                .animate()
                .translationY(300f)
                .setDuration(300)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        binding.navigationBottomBar.visibility= View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }
                })
                .startDelay =300

        }
        else if(check ==0) {
            binding.navigationBottomBar
                .animate()
                .translationY(0f)
                .setDuration(300)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {
                        binding.navigationBottomBar.visibility= View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }
                })
                .start()
        }
    }
}