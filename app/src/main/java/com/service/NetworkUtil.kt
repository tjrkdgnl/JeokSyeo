package com.service

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
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.alcohol_category.AlcoholCategory
import com.jeoksyeo.wet.activity.main.MainActivity

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

    private val handler = Handler(Looper.getMainLooper())

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
            networkCheck = false
            if (GlobalApplication.instance.activityClass == MainActivity::class.java ||
                GlobalApplication.instance.activityClass == AlcoholCategory::class.java
            ) {

                Log.e("화면 갱신", GlobalApplication.instance.activityClass.toString())
                val intent = Intent(context, GlobalApplication.instance.activityClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(intent)
            }

        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)

        Log.e("연결 끊킴","끊킴")
        if(!networkCheck)
            networkCheck = true

        Toast.makeText(context, "네트워크 연결을 시도해주세요.", Toast.LENGTH_SHORT).show()
    }
}