package com.base

import android.app.Activity
import android.content.Context

interface DefaultPresenter {

    fun initNavigationItemSet(context: Context, activity: Activity, provider:String?)

    fun checkLogin(context:Context,provider:String?)

    fun detachView()

}