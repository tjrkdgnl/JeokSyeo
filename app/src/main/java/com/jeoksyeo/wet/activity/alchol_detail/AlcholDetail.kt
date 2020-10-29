package com.jeoksyeo.wet.activity.alchol_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.application.GlobalApplication
import com.model.alchol_detail.Alchol
import com.model.alchol_detail.Data
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcholDetailBinding
import javax.security.auth.login.LoginException

class AlcholDetail :AppCompatActivity(), AlcholDetailContract.BaseView , View.OnClickListener {
    private lateinit var binding: AlcholDetailBinding
    private lateinit var presenter:Presenter
    private  var alchol:Alchol? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.alchol_detail)
        binding.lifecycleOwner =this

        presenter = Presenter().apply {
            view = this@AlcholDetail
        }

        if(intent.hasExtra(GlobalApplication.MOVE_ALCHOL)){
            alchol = intent.getParcelableExtra<Alchol>(GlobalApplication.MOVE_ALCHOL)
            binding.alchol =alchol
            binding.executePendingBindings()
            setLike(alchol?.isLiked!!)
        }

        binding.AlcholDetailSelectedByMe.setOnClickListener(this)
    }

    override fun getView(): AlcholDetailBinding {
        return binding
    }

    override fun setLike(isLike: Boolean) {
        Log.e("like",isLike.toString())

        if(isLike){
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.full_heart)
        }
        else{
            binding.AlcholDetailSelectedByMe.setImageResource(R.mipmap.empty_heart)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.AlcholDetail_selectedByMe -> {
                alchol?.let {
                    it.isLiked?.let { like->
                        if(like)
                            presenter.cancelAlcholLike(it.alcholId!!)
                        else
                            presenter.executeLike(it.alcholId!!)
                    }
                }
            }
        }
    }
}