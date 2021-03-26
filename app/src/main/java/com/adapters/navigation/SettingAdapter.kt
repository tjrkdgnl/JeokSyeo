package com.adapters.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.activities.agreement.Agreement
import com.activities.editprofile.EditProfile
import com.activities.login.apple.AppleLogin
import com.activities.login.google.GoogleLogin
import com.activities.login.kakao.KakaoLogin
import com.activities.login.naver.NaverLogin
import com.adapters.viewholder.SettingAgreementViewHolder
import com.adapters.viewholder.SettingViewHolder
import com.application.GlobalApplication
import com.model.setting.SettingItem

class SettingAdapter(
    private val context: Context,
    private val activity: Activity,
    private val lst: MutableList<SettingItem>,
    private val provider:String?) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
      return when(viewType){
          0 ->{
              SettingViewHolder(parent)
          }
          1 ->{
              SettingAgreementViewHolder(parent)
          }

          else-> throw RuntimeException("알 수 없는 뷰 타입 에러")
      }
    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SettingViewHolder){
            holder.bind(lst[position])

            when(position){
                4->{  //회원탈퇴를 눌렀을 때
                    holder.getViewBinding().settingVersionText.text =""
                    holder.getViewBinding().settingLinearLayout.setOnClickListener{
                        checkDelete()
                    }
                }
            }
        }
        else if( holder is SettingAgreementViewHolder){
            holder.bind(lst[position])

            when(position){
                0 ->{
                    holder.getViewBinding().agreementSettingLayout.setOnClickListener{
                        GlobalApplication.instance.moveActivity(context, EditProfile::class.java)}
                    }
                1->{
                    holder.getViewBinding().agreementSettingLayout.setOnClickListener{
                        val  bundle = Bundle()
                        bundle.putInt(GlobalApplication.AGREEMENT_INFO,0)
                        GlobalApplication.instance.moveActivity(context,
                            Agreement::class.java,0,bundle,
                            GlobalApplication.AGREEMENT,1)
                    }
                }

                2->{
                    holder.getViewBinding().agreementSettingLayout.setOnClickListener{
                        val  bundle = Bundle()
                        bundle.putInt(GlobalApplication.AGREEMENT_INFO,1)
                        GlobalApplication.instance.moveActivity(context,
                            Agreement::class.java,0,bundle,
                            GlobalApplication.AGREEMENT,1)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return lst[position].type
    }

    //회원 탈퇴에 대한 핸들링
    private fun checkDelete(){
        provider?.let {
            when (provider) {
                "NAVER" -> {NaverLogin(context).naverDelete() }
                "KAKAO" -> {KakaoLogin(context).kakaoDelete() }
                "GOOGLE" -> { GoogleLogin(activity).googleDelete() }
                "APPLE" -> { AppleLogin(activity).appleDelete()}
            }
        }
    }
}