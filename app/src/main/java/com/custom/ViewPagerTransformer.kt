package com.custom

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2

import com.vuforia.engine.wet.R

class ViewPagerTransformer(context: Context) : ViewPager2.PageTransformer {
    private val pageMargin: Float = context.resources.getDimensionPixelOffset(R.dimen.pageMargin).toFloat()
    private val pageOffset: Float = context.resources.getDimensionPixelOffset(R.dimen.offset).toFloat()

    //   val r = 1 - Math.abs(position)
    //   page.scaleY = 0.85f + r * 0.15f

    override fun transformPage(page: View, position: Float) {
        val myOffset = position * -(2 * pageOffset + pageMargin + 15)

        val scaleFactor = Math.max(0.77f, 0.83f - Math.abs(position * 0.14285715f))

        if (position < -1) {
//            이전 page의 위치
//            page.setTranslationX(-myOffset);
        } else if (position <= 1) {
            //현재 페이지부터 다음 페이지까지의 범위 0~1
            //현재페이지는 position =0

            //현재 페이지와 다음 페이지 위치 셋팅 초기화
            page.translationX = myOffset

            //가로 세로의 비율값 셋팅
            page.scaleX = scaleFactor + 0.05f
            page.scaleY = scaleFactor + 0.1f
        } else {
            //다음 페이지 scale 셋팅
            page.translationX = myOffset
            page.scaleX = scaleFactor + 0.05f
            page.scaleY = scaleFactor + 0.1f
        }
    }
}