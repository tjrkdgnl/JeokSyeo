package com.jeoksyeo.wet.activity.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.search.SearchAdapter
import com.adapter.search.SearchResultAdapter
import com.adapter.viewholder.NoRelativeSearchViewHolder
import com.adapter.viewholder.NoResentViewholder
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.login.Login
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding
import io.reactivex.disposables.CompositeDisposable

class Search : AppCompatActivity(), View.OnClickListener, TextWatcher, SearchContract.BaseVIew,
    View.OnKeyListener {
    private lateinit var binding: SearchBinding
    private val compositeDisposable = CompositeDisposable()
    private var searchAdapter: SearchAdapter? = null
    private var resultAdapter: SearchResultAdapter? = null
    private lateinit var presenter: Presenter
    private lateinit var layoutManager: LinearLayoutManager
    private val handler = Handler(Looper.getMainLooper())
    private var relativeCheck:Boolean =true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.search)

        layoutManager = LinearLayoutManager(this)
        presenter = Presenter().apply {
            view = this@Search
            layoutManager = this@Search.layoutManager
            activity =this@Search
        }
        presenter.setNetworkUtil()

        binding.editTextSearch.setOnKeyListener(this)
        binding.editTextSearch.addTextChangedListener(this)
        searchAdapter = initSearchAdapter()
        binding.recyclerViewSearchlist.adapter = searchAdapter
        binding.recyclerViewSearchlist.setHasFixedSize(false)
        binding.recyclerViewSearchlist.layoutManager = layoutManager


        //포커싱에 따른 힌트 출력여부 결정
        binding.editTextSearch.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.editTextSearch.hint = ""
            } else{
                if(binding.editTextSearch.text.isEmpty())
                    binding.editTextSearch.hint = "찾으시는 주류가 있으신가요?"
            }
        }

        //imeOption 설정하여 키보드에서 바로 검색되도록 핸들링
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_GO ->{
                    if(binding.editTextSearch.text.isNotEmpty()){
                        binding.initEditText.visibility =View.VISIBLE
                        changeAdapter(binding.editTextSearch.text.toString(),false)
                        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this@Search)
                    }
                    true
                }
                else ->{false}
            }
        }


        binding.recyclerViewSearchlist.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val childView = rv.findChildViewUnder(e.x,e.y) //클릭한 영역으로 아이템 뷰를 리턴

                childView?.let {
                    val childViewholder = rv.findContainingViewHolder(childView)!!

                    //최근에 검색한 화면이 없을때, 연관검색어가 없는 화면일 때, 키패드 숨기기
                    return if(childViewholder is NoResentViewholder || childViewholder is NoRelativeSearchViewHolder) {
                        hideKeyPad()
                        true
                    }
                    else{//아이템을 클릭할 경우, 터치리스너를 가로채면 안됨.
                        false
                    }
                } ?:  hideKeyPad() //아이템 이외의 범위를 클릭했을 경우 키패드 숨기기

                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = Search::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    private fun hideKeyPad():Boolean{
        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this@Search)
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_searchBar_btn -> {
                if(binding.editTextSearch.text.isNotEmpty()){
                    binding.initEditText.visibility =View.VISIBLE
                    changeAdapter(binding.editTextSearch.text.toString(),false)
                    GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this)
                }
            }

            R.id.imageButton_searchBarBackButton -> {
                finish()
                overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
            }

            R.id.initEditText -> {
                binding.editTextSearch.setText("")
                GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this,false)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 300ms 이전이면 메세지 큐에 담겨져있는 Runnable 객체 모두 삭제하여 실행 안되도록 막음
        s ?: return handler.removeCallbacksAndMessages(null)

        if(relativeCheck){
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                if(!binding.initEditText.isVisible && s.isNotEmpty())
                    binding.initEditText.visibility = View.VISIBLE
                else if(binding.initEditText.isVisible && s.isEmpty()){
                    binding.initEditText.visibility=View.INVISIBLE
                }

                noSearchItem(false,s.toString())
                presenter.setRelativeSearch(s.toString())
            }, 300)
        }
        else{
            relativeCheck=true
            binding.editTextSearch.isFocusable = true
            // 연관검색어는 한번만 클릭되므로, 한번 이후는 다시 연관검색어가 검색되도록 만들어야한다.
        }
    }

    //바인딩 객체 전달
    override fun getView(): SearchBinding {
        return binding
    }


    override fun initSearchAdapter(): SearchAdapter {
        return GlobalApplication.userDataBase.getKeywordList()?.let { lst ->
            SearchAdapter(this, lst.toMutableList(), this)
        } ?: SearchAdapter(this, mutableListOf<String>().apply {
            this.add("-1")
        }, this)
    }

    //사용자가 입력한 키워드에 따르는 연관겁색어 업데이트
    override fun updateRelativeList(list: MutableList<String>, searchImg: Int) {
        if (binding.recyclerViewSearchlist.adapter is SearchAdapter) {

        } else { //연관검색어 재 셋팅
            binding.recyclerViewSearchlist.adapter = searchAdapter
        }

        searchAdapter?.updateList(list, searchImg)
        searchAdapter?.notifyDataSetChanged()
    }

    //키워드 검색 api의 결과로 받은 리스트를 리스트어댑터의 파라미터로 받아서 셋팅하는 메서드
    override fun setSearchList(list: MutableList<AlcoholList>) {
        resultAdapter = SearchResultAdapter(this, list, presenter.exeuteProgressBar)
        binding.recyclerViewSearchlist.adapter = resultAdapter
    }

    //검색결과 페이지네이션을 위한 메서드
    override fun updatePaging(list: MutableList<AlcoholList>) {
        resultAdapter?.updateList(list.toMutableList())
    }

    //연관검색어에서 검색결과로 어댑터 전환
    //프레젠터를 통해서 키워드에 대한 검색 결과 api를 실행
    override fun changeAdapter(keyword: String?,relativeCheck:Boolean) {
        this.relativeCheck =relativeCheck
        presenter.setSearchResult(keyword)
        binding.initEditText.visibility = View.VISIBLE //검색어 초기화 버튼 on
        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this) // 키패드는 감추
    }

    @SuppressLint("SetTextI18n")
    override fun noSearchItem(check: Boolean, keyword: String?) {
        if (check) {
            binding.noSearchItem.root.visibility = View.VISIBLE
            binding.recyclerViewSearchlist.visibility = View.INVISIBLE
            keyword?.let {
                binding.noSearchItem.textViewAlcoholName.text= "\'${keyword}\'에 대한 검색결과가 없습니다." +
                        "\n정확한 검색어인지 확인하고 다시 검색해주세요."
            }
        } else {
            binding.noSearchItem.root.visibility = View.INVISIBLE
            binding.recyclerViewSearchlist.visibility = View.VISIBLE
        }
    }


    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            GlobalApplication.instance.keyPadSetting(binding.editTextSearch,this)
            return true
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}