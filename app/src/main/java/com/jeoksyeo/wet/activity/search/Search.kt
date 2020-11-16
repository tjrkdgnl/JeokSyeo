package com.jeoksyeo.wet.activity.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcohol_category.ListAdapter
import com.adapter.search.SearchAdapter
import com.application.GlobalApplication
import com.model.alcohol_category.AlcoholList
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding
import io.reactivex.disposables.CompositeDisposable

class Search : AppCompatActivity(), View.OnClickListener, TextWatcher, SearchContract.BaseVIew,
    View.OnKeyListener {
    private lateinit var binding: SearchBinding
    private val compositeDisposable = CompositeDisposable()
    private var searchAdapter: SearchAdapter? = null
    private var listAdapter: ListAdapter? = null
    private lateinit var presenter: Presenter
    private lateinit var layoutManager: LinearLayoutManager
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.search)

        layoutManager = LinearLayoutManager(this)
        presenter = Presenter().apply {
            view = this@Search
            layoutManager = this@Search.layoutManager
        }

        binding.editTextSearch.setOnKeyListener(this)
        hideKeypad(binding.editTextSearch)
        binding.editTextSearch.addTextChangedListener(this)

        searchAdapter = initSearchAdapter()
        binding.recyclerViewSearchlist.adapter = searchAdapter
        binding.recyclerViewSearchlist.setHasFixedSize(true)
        binding.recyclerViewSearchlist.layoutManager = layoutManager
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_searchBar_btn -> {
                changeAdapter(binding.editTextSearch.text.toString())
            }

            R.id.imageButton_searchBarBackButton -> {
                finish()
                overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 300ms 이전이면 메세지 큐에 담겨져있는 Runnable 객체 모두 삭제하여 실행 안되도록 막음
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(Runnable {
            presenter.pageNum = 1
            noSearchItem(false)
            presenter.setRelativeSearch(s.toString())
        }, 300)

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
    override fun updateRelativeList(list: MutableList<String>,searchImg:Int) {
       if(binding.recyclerViewSearchlist.adapter is SearchAdapter){

       }
        else{ //연관검색어 재 셋팅
           binding.recyclerViewSearchlist.adapter = searchAdapter
       }

        searchAdapter?.updateList(list,searchImg)
    }

    //키워드 검색 api의 결과로 받은 리스트를 리스트어댑터의 파라미터로 받아서 셋팅하는 메서드
    override fun setSearchList(list: MutableList<AlcoholList>) {
        listAdapter = ListAdapter(this, list, presenter.exeuteProgressBar)
        binding.recyclerViewSearchlist.adapter = listAdapter
    }

    //검색결과 페이지네이션을 위한 메서드
    override fun updatePaging(list: MutableList<AlcoholList>) {
        listAdapter?.updateList(list.toMutableList())
    }

    //연관검색어에서 검색결과로 어댑터 전환
    //프레젠터를 통해서 키워드에 대한 검색 결과 api를 실행
    override fun changeAdapter(keyword: String?) {
        presenter.setSearchResult(keyword)
    }

    override fun noSearchItem(check: Boolean) {
        if (check) {
            binding.noSearchItem.root.visibility = View.VISIBLE
            binding.recyclerViewSearchlist.visibility = View.INVISIBLE
        } else {
            binding.noSearchItem.root.visibility = View.INVISIBLE
            binding.recyclerViewSearchlist.visibility = View.VISIBLE

        }
    }


    fun hideKeypad(edit_nickname: EditText) {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(edit_nickname.windowToken, 0)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            hideKeypad(binding.editTextSearch)
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