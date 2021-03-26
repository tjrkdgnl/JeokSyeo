package com.fragments.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapters.search.SearchAdapter
import com.adapters.search.SearchResultAdapter
import com.adapters.viewholder.NoRelativeSearchViewHolder
import com.adapters.viewholder.NoResentViewholder
import com.application.GlobalApplication
import com.base.BaseFragment
import com.model.alcohol_category.AlcoholList
import com.viewmodel.MainViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding
import gun0912.tedkeyboardobserver.TedRxKeyboardObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment private constructor() : BaseFragment<SearchBinding>(), SearchContract.BaseVIew, View.OnClickListener, TextWatcher,
    View.OnKeyListener {
    override val layoutResID: Int =  R.layout.search

    private lateinit var searchPresenter: Presenter
    private var searchAdapter: SearchAdapter? = null
    private var resultAdapter: SearchResultAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private val handler = Handler(Looper.getMainLooper())
    private var relativeCheck:Boolean =true
    private lateinit var activityContext: Context
    private lateinit var viewModel: MainViewModel

    companion object{
        fun newInstance():Fragment {
            return SearchFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //기본 셋팅
        init()

        //키패드 hide / show
        TedRxKeyboardObserver(requireActivity())
            .listen()
            .subscribe({isShow->
                if(isShow){
                    viewModel.bottomNavigationViewVisiblity.value =1
                }
                else{
                    viewModel.bottomNavigationViewVisiblity.value =0
                }
            },{})


        //포커싱에 따른 힌트 출력여부 결정
        binding.editTextSearch.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.editTextSearch.hint = ""

            } else{
                if(binding.editTextSearch.text.isEmpty()){
                    binding.editTextSearch.hint = "찾으시는 주류가 있으신가요?"

                }
            }
        }

        //imeOption 설정하여 키보드에서 바로 검색되도록 핸들링
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_GO ->{
                    if(binding.editTextSearch.text.isNotEmpty()){
                        binding.initEditText.visibility =View.VISIBLE
                        changeAdapter(binding.editTextSearch.text.toString())
                        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
                    }
                    true
                }
                else ->{false}
            }
        }

        /**
         * 리싸이클러뷰에서 아이템 이외의 공간 클릭 시, 키패드 숨기기
         * 또한 아이템 클릭 시, 아이템에 대한 세부정보로 이동하도록 핸들링
         */
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

    private fun init(){
        binding.initEditText.setOnClickListener(this)
        binding.imageButtonSearchBarBackButton.setOnClickListener(this)
        binding.imageViewSearchBarBtn.setOnClickListener(this)
        binding.editTextSearch.setOnKeyListener(this)
        binding.editTextSearch.addTextChangedListener(this)

        viewModel =ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        layoutManager = LinearLayoutManager(activityContext)

        searchPresenter = Presenter().apply {
            this.view = this@SearchFragment
            layoutManager = this@SearchFragment.layoutManager
            activity = requireActivity()
        }

        searchAdapter = initSearchAdapter()
        binding.recyclerViewSearchlist.adapter = searchAdapter
        binding.recyclerViewSearchlist.setHasFixedSize(false)
        binding.recyclerViewSearchlist.layoutManager = layoutManager

    }

    private fun hideKeyPad():Boolean{
        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
        return true
    }

    override fun getViewBinding(): SearchBinding {
        return binding
    }

    override fun initSearchAdapter(): SearchAdapter {
        return GlobalApplication.userDataBase.getKeywordList()?.let { lst ->
            SearchAdapter(activityContext, lst.toMutableList(), this)
        } ?: SearchAdapter(activityContext, mutableListOf<String>().apply {
            this.add("${SearchAdapter.NO_SEARCH}")
        }, this)
    }

    override fun updateRelativeList(list: MutableList<String>, searchImg: Int) {
        if (binding.recyclerViewSearchlist.adapter is SearchAdapter) {

        } else { //연관검색어 재 셋팅
            binding.recyclerViewSearchlist.adapter = searchAdapter
        }

        searchAdapter?.updateList(list, searchImg)
        searchAdapter?.notifyDataSetChanged()
    }

    override fun setSearchList(list: MutableList<AlcoholList>) {
        resultAdapter = SearchResultAdapter(activityContext, list, searchPresenter.exeuteProgressBar)
        binding.recyclerViewSearchlist.adapter = resultAdapter
    }

    override fun updatePaging(list: MutableList<AlcoholList>) {
        resultAdapter?.updateList(list.toMutableList())
    }

    override fun changeAdapter(keyword: String?) {
        this.relativeCheck =false
        searchPresenter.setSearchResult(keyword)
        binding.initEditText.visibility = View.VISIBLE //검색어 초기화 버튼 on
        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext) // 키패드는 감추기
    }

    @SuppressLint("SetTextI18n")
    override fun checkSearchItem(check: Boolean, keyword: String?) {
        if (check) {
            binding.noSearchItem.root.visibility = View.VISIBLE
            binding.recyclerViewSearchlist.visibility = View.INVISIBLE //검색어 리스트 숨기기
            keyword?.let {
                binding.noSearchItem.textViewAlcoholName.text= "\'${keyword}\'에 대한 검색결과가 없습니다." +
                        "\n정확한 검색어인지 확인하고 다시 검색해주세요."
            }
        } else {
            binding.noSearchItem.root.visibility = View.INVISIBLE   // 검색한 결과가 있는 경우 없음 안내 숨기기
            binding.recyclerViewSearchlist.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_searchBar_btn -> {
                if(binding.editTextSearch.text.isNotEmpty()){
                    //검색어 초기화 버튼 활성화
                    binding.initEditText.visibility =View.VISIBLE

                    //키워드에 대한 연관리스트 업데이트
                    changeAdapter(binding.editTextSearch.text.toString())

                    //키 패드 숨기기
                    GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
                }
            }

            R.id.imageButton_searchBarBackButton -> {
                //검색어 버튼에서 뒤로가기를 눌렀을 때 현재 프래그먼트 삭제
                CoroutineScope(Dispatchers.IO).launch {
                    (activity as FragmentActivity).supportFragmentManager.popBackStack()
                }
            }

            R.id.initEditText -> {
                binding.editTextSearch.setText("")
                GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext,false)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 300ms 이전이면 메세지 큐에 담겨져있는 Runnable 객체 모두 삭제하여 실행 안되도록 막음
        // 따라서 사용자가 키워드를 입력할 때마다 api를 호출하지 않고 일정 지연시간 동안 기다린 후에 호출하기 때문에
        // 과부하를 방지할 수 있음.
        s ?: return handler.removeCallbacksAndMessages(null)

        if(relativeCheck){
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                //사용자가 입력한 키워드를 초기화하는 버튼 표시 여부 결정
                if(!binding.initEditText.isVisible && s.isNotEmpty())
                    binding.initEditText.visibility = View.VISIBLE
                else if(binding.initEditText.isVisible && s.isEmpty()){
                    binding.initEditText.visibility=View.INVISIBLE
                }

                checkSearchItem(false,s.toString())
                searchPresenter.setRelativeSearch(s.toString())
            }, 300)
        }
        else{
            // 연관검색어는 한번만 클릭되므로, 한번 이후는 다시 연관검색어가 검색되도록 만들어야한다.
            relativeCheck=true
            binding.editTextSearch.isFocusable = true

        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
            return true
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun checkKeywordListCount(show:Boolean, keyword:String, total:Int){
        if(show){
            binding.searchKeyword.text ="\"$keyword"
            binding.keywordCount.text ="\"관련해서 총 ${total}개의 상품을 찾았습니다."
            binding.keywordResult.visibility =View.VISIBLE
            binding.textViewRecentSearch.visibility =View.INVISIBLE

        }
        else{
            binding.textViewRecentSearch.text = keyword
            binding.keywordResult.visibility =View.INVISIBLE
            binding.textViewRecentSearch.visibility =View.VISIBLE
        }
    }

    override fun detachPresenter() {
        searchPresenter.detach()
    }
}