package com.fragments.alcohol_rated

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentAlcholRatedBinding

class Fragment_alcoholRated: BaseFragment<FragmentAlcholRatedBinding>(), FragmentRated_Contract.FragmentRatedView {
    override val layoutResID: Int = R.layout.fragment_alchol_rated
    private var typePosition =0
    private lateinit var presenter:Presenter

    /**
     * 팩토리 메서드를 통해서 fragment가 파라미터를 입력받아 생성되도록 작성
     * 해당 메서드를 통해서 viewPagerAdapter는 각 타입의 포지션을 넘겨준다.
     */
    companion object{
        fun newInstance(typePosition:Int):Fragment{
            val fragment = Fragment_alcoholRated()
            val bundle = Bundle()
            bundle.putInt("position",typePosition)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
           typePosition= it.getInt("position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = Presenter().apply {
            this.viewObj =this@Fragment_alcoholRated
            activity = requireActivity()
            typePosition =this@Fragment_alcoholRated.typePosition
            viewmodel = ViewModelProvider(requireActivity()).get(RatedViewModel::class.java)
        }

        presenter.initRatedList()

    }

    override fun detachPresenter() {
        presenter.detach()
    }

    override fun getBindingObj(): FragmentAlcholRatedBinding {
        return binding
    }
}