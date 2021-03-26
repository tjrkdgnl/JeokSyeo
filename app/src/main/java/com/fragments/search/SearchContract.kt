package com.fragments.search

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapters.search.SearchAdapter
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding

interface SearchContract {

    interface BaseVIew{
        fun getViewBinding():SearchBinding

        /**
         * 사용자의 검색 리스트를 local db에서 불러와 셋팅합니다. 만약 사용자가 검색한 리스트가 없다면
         * "최근 검색어"가 없음을 나타내는 화면이 나옵니다.
         */
        fun initSearchAdapter():SearchAdapter

        /**
         * 서버에서 가져온 리스트를 어댑터에 값으로 새롭게 갱신시켜주는 역할을 합니다.
         */
        fun updateRelativeList(list:MutableList<String>,searchImg:Int = R.mipmap.resent_timer)

        /**
         * 서버로부터 받아온 검색 리스트로 어댑터 값을 갱신시켜주는 메서드입니다.
         */
        fun setSearchList(list:MutableList<AlcoholList>)

        /**
         * 서버로부터 받아온 페이징 단위의 데이터들을  어댑터에 추가시켜줍니다.
         */
        fun updatePaging(list:MutableList<AlcoholList>)

        /**
         * 유저가 검색 키워드를 가지고서 "검색"버튼을 클릭할 시, 서버로부터 키워드를 갖는 연관 검색어 리스트를
         * 받아오고 해당 리스트의 유무에 따라서 어댑터의 데이터를 갱신하도록합니다.
         */
        fun changeAdapter(keyword: String?)

        /**
         * 검색한 내용이 존재하지 않는다면, 유저에게 보여줄 화면을 셋팅하는 메서드입니다.
         * @param{ check}에 따라서 서버로부터 받아온 데이터가 있는지 없는지 bool값으로 전달합니다.
         * @param{ keyword}는 유저가 검색한 키워드에 대한 리스트 여부를 표시하기 위해서 전달됩니다.
         */
        fun checkSearchItem(check:Boolean, keyword: String?)

        /**
         * 현재 리스트 항목이 보여지는 주제가 무엇인지 정합니다.
         * @param{show}가 true라면, 사용자가 검색 버튼을 클릭하여, 키워드에 관한 연관 검색어 리스트 개수를
         * 표시됩니다. false라면, 기본적으로 현재 보여지는 리스트 항목이 "연관검색어"인지 "최근 검색어"인지를
         * 표시하도록합니다.
         *
         * @param{keyword, total}은 사용자가 검색 버튼을 클릭했을 때, 사용자가 검색한 키워드에 따른 연관 검색어의
         * 총 개수를 표시하기 위해서 사용됩니다.
         */
        fun checkKeywordListCount(show:Boolean, keyword:String ="", total:Int =0)
    }


    interface BasePresenter{
        var view: BaseVIew
        var activity:Activity
        /**
         * 페이징을 위해서 뷰에서 셋팅한 레이아웃매니저가 필요
         */
        var layoutManager:LinearLayoutManager

        fun detach()

        /**
         * 사용자가 검색한 키워드를 통해 서버로부터 연관 검색어 리스트를 가져오는 역할을 합니다.
         */
        fun setSearchResult(keyword:String?)

        /**
         * 사용자가 실시간으로 입력하는 키워드를 가지고서 연관된 리스트를 얻어오는 역할을 합니다.
         */
        fun setRelativeSearch(keyword:String)

    }
}