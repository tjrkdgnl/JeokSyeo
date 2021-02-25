package com.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.application.GlobalApplication
import com.activities.login.Login
import com.vuforia.engine.wet.R


object CustomDialog {

    fun createCustomDialog(context: Context): Dialog {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_twobutton)
        dialog.show()
        return dialog
    }

    @SuppressLint("SetTextI18n")
    fun loginDialog(context: Context, activityHandle: Int, sessionCheck: Boolean = false) {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_twobutton)
        dialog.show()

        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        val contents = dialog.findViewById<TextView>(R.id.dialog_contents)

        if (sessionCheck) {
            contents.text = "세션이 만료되었습니다.\n재로그인 해주세요"
        }

        okButton.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle()
            bundle.putInt(GlobalApplication.ACTIVITY_HANDLING, activityHandle)
            GlobalApplication.instance.moveActivity(
                context, Login::class.java,
                0, bundle, GlobalApplication.ACTIVITY_HANDLING_BUNDLE
            )
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    fun versionDialog(context: Context) {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_onebutton)
        dialog.show()

        val oneButton = dialog.findViewById<Button>(R.id.onebutton)

        oneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.playStore_url)))
            (context as Activity).startActivity(intent)
            dialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun networkErrorDialog(context: Context) {
        val dialog = Dialog(context, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_network)
        dialog.show()

        val oneButton = dialog.findViewById<Button>(R.id.onebutton)

        oneButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun QnA_Dialog(context: Context?, plzMsg: Int) {
        var msgString = -1
        val dialog = Dialog(context!!, R.style.custom_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_twobutton)
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
        okButton.setOnClickListener { dialog.dismiss() }
        cancelButton.setOnClickListener { dialog.dismiss() }
    }

}
