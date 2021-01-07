package com.jeoksyeo.wet.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.application.GlobalApplication
import com.fragment.main.MainFragment
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.RealMainActivityBinding

class MainActivity:AppCompatActivity(), Contract.BaseView {

    private lateinit var binding:RealMainActivityBinding

    private lateinit var presenter:Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.real_main_activity)


        presenter = Presenter().apply {
            view = this@MainActivity
            activity = this@MainActivity
        }

        presenter.setNetworkUtil()


        this.supportFragmentManager.beginTransaction().add(R.id.fragment_container,MainFragment()).commit()


        with(Handler(Looper.getMainLooper())){
            postDelayed({
                presenter.handleDeepLink()
            },300)
        }

        binding.navigationBottomBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_journey->{

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_journal->{
                    replaceFragment(MainFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_myPage->{

                    return@setOnNavigationItemSelectedListener true
                }

                else->{

                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
        binding.navigationBottomBar.selectedItemId = R.id.navigation_journal
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //네트워크가 다시 연결 됐을 때,
        (this.supportFragmentManager.fragments[0] as MainFragment).initApi(this)

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

    override fun replaceFragment(fragment: Fragment) {
        this.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit()
        this.supportFragmentManager.beginTransaction().addToBackStack(null)
    }
}