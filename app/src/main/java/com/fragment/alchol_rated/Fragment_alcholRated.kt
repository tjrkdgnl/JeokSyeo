package com.fragment.alchol_rated

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alchol_rated.AlcholRatedAdapter
import com.custom.CenterLayoutManager
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

class Fragment_alcholRated:Fragment() {
    private var position =0
    private lateinit var binding:FragmentAlcholRatedBinding
    private lateinit var alcholRatedAdapter: AlcholRatedAdapter
    private lateinit var smoothScrollListener: SmoothScrollListener

    interface  SmoothScrollListener{
        fun moveScroll(position:Int)
    }

    companion object{
        fun newInstance(posittion:Int):Fragment{
            val fragment = Fragment_alcholRated()
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

        //아이템 클릭 시, 해당 아이템으로 스크롤 이동
        smoothScrollListener = object : SmoothScrollListener {
            override fun moveScroll(position: Int) {
                with(Handler()){
                    this.postDelayed(Runnable {
                        binding.ratedRecyclerView.smoothScrollToPosition(position)
                    },300L)
                }
            }
        }

        alcholRatedAdapter= AlcholRatedAdapter(mutableListOf("전통주","사케","맥주","위스키","와인"),smoothScrollListener)
        binding.ratedRecyclerView.adapter =alcholRatedAdapter
        binding.ratedRecyclerView.setHasFixedSize(true)
        binding.ratedRecyclerView.layoutManager = CenterLayoutManager(requireContext())

        return binding.root
    }
}