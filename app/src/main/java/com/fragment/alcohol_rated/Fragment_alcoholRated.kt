package com.fragment.alcohol_rated

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcohol_rated.AlcoholRatedAdapter
import com.custom.CenterLayoutManager
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBindingImpl

class Fragment_alcoholRated:Fragment(), FragmentRated_Contract.BaseView {
    private var position =0
    private lateinit var binding:FragmentAlcholRatedBinding
    private lateinit var alcoholRatedAdapter: AlcoholRatedAdapter
    private lateinit var smoothScrollListener: SmoothScrollListener
    private lateinit var presenter:Presenter
    private lateinit var viewmodel:RatedViewModel

    interface  SmoothScrollListener{
        fun moveScroll(position:Int)
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alchol_rated,container,false)
        binding.lifecycleOwner =this

        viewmodel = ViewModelProvider(requireActivity()).get(RatedViewModel::class.java)
        //아이템 클릭 시, 해당 아이템으로 스크롤 이동
        smoothScrollListener = object : SmoothScrollListener {
            override fun moveScroll(position: Int) {
                with(Handler(Looper.getMainLooper())){
                    this.postDelayed( {
                        binding.ratedRecyclerView.smoothScrollToPosition(position)
                    },300L)
                }
            }
        }

        presenter = Presenter().apply {
            view =this@Fragment_alcoholRated
            activity = requireActivity()
            position =this@Fragment_alcoholRated.position
            smoothScrollListener = this@Fragment_alcoholRated.smoothScrollListener
            viewmodel = this@Fragment_alcoholRated.viewmodel
        }

        presenter.initRatedList(requireContext())

        return binding.root
    }

    override fun getBinding(): FragmentAlcholRatedBinding {
        return binding
    }


}