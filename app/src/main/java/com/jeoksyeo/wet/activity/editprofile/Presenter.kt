package com.jeoksyeo.wet.activity.editprofile

import android.content.Context
import android.util.Log
import android.view.View
import com.application.GlobalApplication
import com.error.ErrorManager
import com.service.ApiGenerator
import com.service.ApiService
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class Presenter : EditProfileContract.BasePresenter {
    override lateinit var view: EditProfileContract.BaseView
    private val compositeDisposable:CompositeDisposable = CompositeDisposable()

    override fun executeEditProfile(name: String?, gender: String?, birthday: String?, image: String?) {
        //프로필 수정하는 api 실행하기.
    }

    override fun checkNickName(context: Context,name: String) {
        val check = Pattern.matches("^\\w+|[가-힣]+$", name)

        if (name.isNotEmpty()) {
            if (check) {
                //api 설정
                compositeDisposable.add( ApiGenerator.retrofit.create(ApiService::class.java)
                    .checkNickName(GlobalApplication.userBuilder.createUUID, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //result=true면 닉네임 중복을 의미
                        if(!it.data?.result!!){
                            view.getView().checkNickNameText.visibility = View.VISIBLE
                            view.getView().checkNickNameText.text = context.getString(R.string.useNickName)
                            view.getView().insertNameLinearLayout.background = context.resources.getDrawable(R.drawable.bottom_line_green, null)
                            view.getView().checkNickNameText.setTextColor(context.resources.getColor(R.color.green, null))
                        }
                    }, {t ->Log.e(ErrorManager.NICKNAME_DUPLICATE,t.message.toString())}))

            } else {
                view.getView().checkNickNameText.visibility = View.VISIBLE
                view.getView().checkNickNameText.text = context.getString(R.string.dontUseNickName)
                view.getView().insertNameLinearLayout.background =
                    context.resources.getDrawable(R.drawable.bottom_line_red, null)
                view.getView().checkNickNameText.setTextColor(context.resources.getColor(R.color.red, null))
            }
        } else {
            view.getView().insertNameLinearLayout.background =
                context.resources.getDrawable(R.drawable.bottom_line, null)
            view.getView().checkNickNameText.visibility = View.INVISIBLE
        }
    }
}