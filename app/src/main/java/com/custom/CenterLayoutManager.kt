package com.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CenterLayoutManager :LinearLayoutManager {

    constructor(context: Context): super(context){

    }

    constructor(context:Context, orientation:Int,  reverseLayout:Boolean) : this(context){

    }

    constructor(context:Context, arrt:AttributeSet ,orientation:Int,  reverseLayout:Boolean) :this(context, orientation, reverseLayout){

    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        var smoothScroller = CenterSmoothScroller(recyclerView?.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}