package com.fragments.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.base.BaseFragment
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

class Fragment_favorite : BaseFragment<FragmentFavoriteBinding>(), FavoriteContract.FavoriteView {
    override val layoutResID: Int = R.layout.fragment_favorite
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = Presenter().apply {
            this.viewObj = this@Fragment_favorite
            viewModel = ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)
            activity = requireActivity()
            position = this@Fragment_favorite.position
        }

        presenter.getMyAlcohol()

    }

    override fun detachPresenter() {
        presenter.detach()
    }

    override fun getBindingObj(): FragmentFavoriteBinding {
        return binding
    }
}