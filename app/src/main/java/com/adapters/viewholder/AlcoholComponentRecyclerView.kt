package com.adapters.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.alcoholdetail.DepthComponentAdapter
import com.base.BaseViewHolder
import com.model.alcohol_detail.AlcoholComponentData
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholComponentItemRecyclerviewBinding

/**
 * 주류 상세 화면에서 주류 컴포넌트 중에서 한개 이상의 값으로 구성되어 리스트형식으로
 * 표현되어야 할 때, 이중 리싸이클러뷰 셋팅
 */
class AlcoholComponentRecyclerView(val parent:ViewGroup)
    :BaseViewHolder<AlcoholComponentData,AlcoholComponentItemRecyclerviewBinding>(R.layout.alcohol_component_item_recyclerview,parent) {

    override fun bind(data: AlcoholComponentData) {
        binding.componentListItem = data
        binding.executePendingBindings()

        //컴포넌트 대표 이미지 설정
        data.compo_image?.let {
            binding.componentBackground.setImageResource(it)
        }

        val lst = (data.contents as? List<String>)?.toMutableList()
        var dummyCheck =false
        if(lst?.size !=0 ){
            if(lst?.size!! <=2)   //가운데 정렬을 위해서 더미 값 두개 추가
                dummyCheck =true
            binding.compoentRecyclerView.adapter = DepthComponentAdapter(parent.context,lst,dummyCheck)
            binding.compoentRecyclerView.setHasFixedSize(true)
            binding.compoentRecyclerView.layoutManager =LinearLayoutManager(parent.context)
        }
    }
}
