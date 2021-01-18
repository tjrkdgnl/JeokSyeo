package com.jeoksyeo.wet.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.application.GlobalApplication
import com.fragment.joury_box.JourneyBoxFragment
import com.fragment.main.MainFragment
import com.fragment.mypage.MyPageFragment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RealMainActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Contract.BaseView {

    private lateinit var binding: RealMainActivityBinding
    private lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.real_main_activity)

        presenter = Presenter().apply {
            view = this@MainActivity
            activity = this@MainActivity
        }

        //네트워크 감지
        presenter.setNetworkUtil()

        //main fragment set
        this.supportFragmentManager.beginTransaction().add(R.id.fragment_container, MainFragment())
            .commit()

        //링크로 들어왔을 때 핸들링
        with(Handler(Looper.getMainLooper())) {
            postDelayed({
                presenter.handleDeepLink()
            }, 300)
        }

        binding.navigationBottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.navigation_journey -> {
                    if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_journey){
                        replaceFragment(JourneyBoxFragment(),"journey")
                    }

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_journal -> {
                    //현재 보여지는 페이지라면 재 셋팅을 하지 않기위해서 핸들링
                    if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_journal){
                        replaceFragment(MainFragment(), "main")
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_myPage -> {
                    //현재 보여지는 페이지라면 재 셋팅을 하지 않기위해서 핸들링
                    if(  binding.navigationBottomBar.selectedItemId != R.id.navigation_myPage){
                        replaceFragment(MyPageFragment(), "mypage")
                    }

                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
        binding.navigationBottomBar.selectedItemId = R.id.navigation_journal
    }


    override fun onBackPressed() {
        val idx = this@MainActivity.supportFragmentManager.backStackEntryCount - 1
        val entry = this@MainActivity.supportFragmentManager.getBackStackEntryAt(idx)
        val fragmentName = entry.name

        //bottomNavigationView에 있는 목록은 곧장 앱 종료
        if (fragmentName == "main" || fragmentName == "mypage" || fragmentName == "journey") {
            finish()
        } else {
            //depth가 있는 경우에는 fragment stack에서 pop
            CoroutineScope(Dispatchers.IO).launch {
                fragmentName.let {
                    this@MainActivity.supportFragmentManager.popBackStack(
                        it, FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
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

    override fun getBinding(): RealMainActivityBinding {
        return binding
    }

    //메인 액티비티에 프래그먼트 교체
    override fun replaceFragment(fragment: Fragment, name: String) {
        this.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(name).commit()
    }
}