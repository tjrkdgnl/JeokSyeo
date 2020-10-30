package com.custom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.login.Login
import com.model.user.UserInfo
import com.nhn.android.naverlogin.OAuthLogin
import com.vuforia.engine.wet.R
import kotlinx.android.synthetic.main.main.*


object CustomDialog {
    fun loginDialog(context: Context, activityHandle: Int) {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)

        okButton.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putInt(GlobalApplication.ACTIVITY_HANDLING, activityHandle)
            GlobalApplication.instance.moveActivity(context, Login::class.java,
                0, bundle, GlobalApplication.ACTIVITY_HANDLING_BUNDLE)

            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun QnA_Dialog(context: Context?, plzMsg: Int) {
        var msgString = -1
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.text = "확인"
        when (plzMsg) {
            -1 -> msgString = R.string.plzQnAPart
            1 -> msgString = R.string.plzQnATitle
            2 -> msgString = R.string.plzQnAComment
        }
        contents.setText(msgString)
        okButton.setOnClickListener { v: View? -> dialog.dismiss() }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

}
