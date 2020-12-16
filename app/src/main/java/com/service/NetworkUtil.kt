package com.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.alcohol_category.AlcoholCategory
import com.jeoksyeo.wet.activity.main.MainActivity
import com.vuforia.engine.wet.R
import java.util.zip.Inflater


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkUtil(val context: Context) : ConnectivityManager.NetworkCallback() {

    private var networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private var taskInfo: MutableList<ActivityManager.AppTask>

    private var networkCheck = false


    init {
        taskInfo = activityManager.appTasks
    }

    fun register() {
        this.connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unRegister() {
        this.connectivityManager.unregisterNetworkCallback(this)
    }


    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        if (networkCheck) {
            if(GlobalApplication.instance.getActivityBackground()){
                Toast.makeText(GlobalApplication.instance,"네트워크가 연결되었습니다.",Toast.LENGTH_SHORT).show()
            }

            networkCheck = false
            if (GlobalApplication.instance.activityClass == MainActivity::class.java) {
                val intent = Intent(context, GlobalApplication.instance.activityClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(intent)
            }
        }
    }


    override fun onLost(network: Network) {
        super.onLost(network)

        if(!networkCheck){
            networkCheck = true
            if(GlobalApplication.instance.getActivityBackground()){
                GlobalApplication.instance.getToastView()?.let {
                    val toast = Toast(GlobalApplication.instance)
                    toast.setGravity(Gravity.BOTTOM  ,0,100)
                    toast.view = it
                    toast.duration = Toast.LENGTH_SHORT
                    toast.show()
                }
            }
        }
    }
}