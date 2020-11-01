package com.jeoksyeo.wet.activity.alchol_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.custom.CustomDialog
import com.model.alchol_detail.Alchol
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholDetailBinding

class AlcholDetail : AppCompatActivity(), AlcholDetailContract.BaseView, View.OnClickListener {
    private lateinit var binding: AlcholDetailBinding
    private lateinit var presenter: Presenter
    private var alchol: Alchol? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_detail)
        binding.lifecycleOwner = this

        presenter = Presenter().apply {
            view = this@AlcholDetail
        }

        if (intent.hasExtra(GlobalApplication.ALCHOL_BUNDLE)) {
            val bundle = intent.getBundleExtra(GlobalApplication.ALCHOL_BUNDLE)
            alchol = bundle?.getParcelable(GlobalApplication.MOVE_ALCHOL)
            alchol?.let {
                binding.alchol = alchol
                binding.executePendingBindings()
                alchol?.let {
                    it.isLiked?.let {like->
                        setLike(like)
                    }
                    presenter.initComponent(this, it)
                }
            }
        }

        binding.AlcholDetailSelectedByMe.setOnClickListener(this)
    }

    override fun getView(): AlcholDetailBinding {
        return binding
    }

    override fun setLike(isLike: Boolean) {
        Log.e("like", isLike.toString())

        if (isLike) {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.full_heart)
        } else {
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.empty_heart)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.AlcholDetail_selectedByMe -> {
                if (GlobalApplication.userInfo.getProvider() != null) {
                    alchol?.let {
                        it.alcholId?.let { alcholId ->
                            it.isLiked?.let { like ->
                                if (like)
                                    presenter.cancelAlcholLike(alcholId)
                                else
                                    presenter.executeLike(alcholId)
                            }
                        }

                    }
                } else {
                    CustomDialog.loginDialog(this, GlobalApplication.ACTIVITY_HANDLING_DETAIL)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}