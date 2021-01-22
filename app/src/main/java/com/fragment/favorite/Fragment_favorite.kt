package com.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

class Fragment_favorite : Fragment(), FavoriteContract.BaseView {
    private lateinit var binding: FragmentFavoriteBinding
    private var bindObj: FragmentFavoriteBinding? =null

    private var position = 0
    private lateinit var presenter: Presenter

    companion object {
        fun newInstance(position: Int): Fragment {
            val fragmentFavorite = Fragment_favorite()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragmentFavorite.arguments = bundle
            return fragmentFavorite
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindObj =  DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        binding =bindObj!!
        binding.lifecycleOwner = this


        presenter = Presenter().apply {
            view = this@Fragment_favorite
            viewModel = ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)
            context = requireContext()
            position = this@Fragment_favorite.position
        }

        presenter.getMyAlcohol()

        return binding.root
    }

    override fun getBinding(): FragmentFavoriteBinding {
        return binding
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        bindObj =null
    }
}