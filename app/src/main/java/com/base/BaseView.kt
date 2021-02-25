package com.base

import androidx.databinding.ViewDataBinding

interface BaseView <T:ViewDataBinding>{
    /**
     * presenter가 액티비티에서 생성한 binding 객체를 사용할 수 있도록 객체 값을 리턴하는 메서드입니다.
     */
    fun  getBindingObj() : T
}