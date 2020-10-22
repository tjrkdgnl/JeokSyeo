package com.custom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.jeoksyeo.wet.activity.login.Login
import com.vuforia.engine.wet.R


object CustomDialog {
    fun loginDialog(context: Context, activityHandle: Int) {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val okButton = dialog.findViewById<Button>(R.id.dialog_okButton)
        val cancelButton = dialog.findViewById<Button>(R.id.dialog_cancelButton)
        okButton.setOnClickListener { v: View? ->
            val intent = Intent(context, Login::class.java)
            intent.putExtra("activityHandle", activityHandle)
            context.startActivity(intent)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { v: View? -> dialog.dismiss() }
    }

    fun customDialog(context: Context?): Dialog {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        return dialog
    }

    fun customDialog(context: Context?, activityHandle: Int): Dialog {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        return dialog
    }

    fun QnA_Dialog(context: Context?, plzMsg: Int) {
        var msgString = -1
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
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
