package com.fragment.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.favorite.FavoriteAdapter
import com.application.GlobalApplication
import com.custom.GridSpacingItemDecoration
import com.error.ErrorManager
import com.model.favorite.AlcoholList
import com.service.ApiGenerator
import com.service.ApiService
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FragmentFavoriteBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Fragment_favorite : Fragment() {
    private lateinit var binding:FragmentFavoriteBinding
    private var position= 0
    private lateinit var disposable :Disposable
    private lateinit var viewmodel:FavoriteViewModel
    companion object{
        fun newInstance(position:Int):Fragment{
            val fragmentFavorite = Fragment_favorite()
            val bundle = Bundle()
            bundle.putInt("position",position)
            fragmentFavorite.arguments = bundle
            return fragmentFavorite
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position =it.getInt("position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite,container,false)
        binding.lifecycleOwner=this

        viewmodel =ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)

        disposable = ApiGenerator.retrofit.create(ApiService::class.java)
            .getMyFavoriteAlcohol(GlobalApplication.userBuilder.createUUID,GlobalApplication.userInfo.getAccessToken(),
            GlobalApplication.instance.getRatedType(position),GlobalApplication.PAGINATION_SIZE,1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                it.data?.summary?.reviewCount?.let {
                    viewmodel.alcoholTypeList[position] = it
                    viewmodel.currentPosition.value = position
                }

                it.data?.alcoholList?.let {list->
                    binding.favoriteRecyclerView.setHasFixedSize(true)

                    if(list.isNotEmpty()){
                        binding.favoriteRecyclerView.adapter = FavoriteAdapter(list.toMutableList())
                        binding.favoriteRecyclerView.addItemDecoration( GridSpacingItemDecoration(2
                            , requireContext().resources.getDimensionPixelSize(R.dimen.grid_layout_margin), true, 0))
                        binding.favoriteRecyclerView.layoutManager =GridLayoutManager(requireContext(),2)
                    }
                    else{
                        val alcohol = AlcoholList()
                        alcohol.type =-1
                        binding.favoriteRecyclerView.adapter = FavoriteAdapter(mutableListOf<AlcoholList>().apply {
                            this.add(alcohol)
                        })
                        binding.favoriteRecyclerView.layoutManager =LinearLayoutManager(requireContext())
                    }



                }

            },{t->
                Log.e(ErrorManager.FAVORITE,t.message.toString())

            })

        return binding.root
    }
}