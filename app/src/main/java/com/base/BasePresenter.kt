package com.base

import android.app.Activity
import androidx.databinding.ViewDataBinding

interface BasePresenter<T:ViewDataBinding> {

    /**
     * 액티비티가 갖는 컨택스가 있어야 현 액티비티의 상태부터 application 단의 접근이 가능합니다. 이를 통해서
     * 리소스 및 applicaion method 등에 접근하고 사용할 수 있습니다.
     */
    var activity: Activity
}