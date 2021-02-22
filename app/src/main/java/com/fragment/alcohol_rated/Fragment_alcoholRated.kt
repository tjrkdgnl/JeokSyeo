package com.fragment.alcohol_rated

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

class Fragment_alcoholRated: BaseFragment<FragmentAlcholRatedBinding>(), FragmentRated_Contract.BaseView {
    override val layoutResID: Int = R.layout.fragment_alchol_rated
    private var position =0
    private lateinit var presenter:Presenter


    companion object{
        fun newInstance(posittion:Int):Fragment{
            val fragment = Fragment_alcoholRated()
            val bundle = Bundle()
            bundle.putInt("position",posittion)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
           position= it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = Presenter().apply {
            this.view =this@Fragment_alcoholRated
            activity = requireActivity()
            position =this@Fragment_alcoholRated.position
//            smoothScrollListener = this@Fragment_alcoholRated.smoothScrollListener
            viewmodel = ViewModelProvider(requireActivity()).get(RatedViewModel::class.java)
        }

        presenter.initRatedList()

    }

    override fun getBinding(): FragmentAlcholRatedBinding {
        return binding
    }

}