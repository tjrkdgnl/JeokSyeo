package com.custom

import android.os.SystemClock
import android.view.View
import android.widget.Toast

class OneClickListener(private var onOneClick:(View)->Unit):View.OnClickListener {
    private  val interval :Long = 300
    private var lastClick:Long = 0

    override fun onClick(v: View?) {
        if(SystemClock.elapsedRealtime()- lastClick < interval){
            Toast.makeText(v?.context,"처리 중입니다. 잠시 후에 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        lastClick = SystemClock.elapsedRealtime()
        onOneClick(v!!)
    }
}