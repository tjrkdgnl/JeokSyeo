package com.adapter.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.base.BaseViewHolder
import com.model.area.AreaList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.LocationItemBinding

class LocationViewHolder(parent:ViewGroup):BaseViewHolder<AreaList,LocationItemBinding>(R.layout.location_item,parent) {
    override fun bind(data: AreaList) {
        //view.name = name으로 직접 연결한다면 반드시 executePendingBindings을 통해서 결합을 시켜줘야한다.
        //안그러면.... 갱신이 제대로 이뤄지지않는다...
        //이유는 뷰 업데이트가 일어날 경우, 다음 프레임에 view를 그리는 계산을 미루게 되면 잘못 측정된 값으로
        //업데이트를 시킨다. 때문에 executePendingBindings과 setVariable을 이용하여 사용하여 동기적으로 업데이트하여 다음 프레임으로 넘긴다.
//            view.executePendingBindings()
        binding.area = data
        binding.executePendingBindings()
    }
}