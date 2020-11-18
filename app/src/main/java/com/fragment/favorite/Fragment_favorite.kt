package com.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.adapter.favorite.FavoriteAdapter
import com.custom.GridSpacingItemDecoration
import com.model.favorite.AlcoholList
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding

class Fragment_favorite : Fragment(), FavoriteContract.BaseView {
    private lateinit var binding: FragmentFavoriteBinding
    private var position = 0
    private lateinit var presenter: Presenter
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
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

    override fun updateList(lst: MutableList<AlcoholList>) {
        favoriteAdapter.pageUpdate(lst)
    }

    override fun setAdapter(favoriteAdapter: FavoriteAdapter) {
        this.gridLayoutManager = GridLayoutManager(requireContext(),2)
        this.favoriteAdapter = favoriteAdapter

        binding.favoriteRecyclerView.setHasFixedSize(false)
        binding.favoriteRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                requireContext().resources.getDimensionPixelSize(R.dimen.grid_layout_margin),
                true,
                0
            )
        )

        binding.favoriteRecyclerView.layoutManager = this.gridLayoutManager
        binding.favoriteRecyclerView.adapter = this.favoriteAdapter
    }

    override fun getGridLayoutManager(): GridLayoutManager {
        return this.gridLayoutManager
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
}