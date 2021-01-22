package com.fragment.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.search.SearchAdapter
import com.adapter.search.SearchResultAdapter
import com.adapter.viewholder.NoRelativeSearchViewHolder
import com.adapter.viewholder.NoResentViewholder
import com.application.GlobalApplication
import com.fragment.alcohol_category.AlcoholCategoryFragment
import com.fragment.main.MainFragment
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.alcohol_category.AlcoholList
import com.viewmodel.MainViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.SearchBinding
import gun0912.tedkeyboardobserver.TedRxKeyboardObserver
import io.reactivex.disposables.CompositeDisposable

class SearchFragment private constructor() : Fragment(), SearchContract.BaseVIew, View.OnClickListener, TextWatcher,
    View.OnKeyListener {
    private lateinit var binding:SearchBinding
    private var bindObj:SearchBinding?=null
    private lateinit var searchPresenter: Presenter
    private val compositeDisposable = CompositeDisposable()
    private var searchAdapter: SearchAdapter? = null
    private var resultAdapter: SearchResultAdapter? = null
    private lateinit var layoutManager: LinearLayoutManager
    private val handler = Handler(Looper.getMainLooper())
    private var relativeCheck:Boolean =true
    private lateinit var activityContext: Context
    private var className:String? =null
    private lateinit var viewModel: MainViewModel
    private var hintChecking =false

    companion object{

        fun newInstance(className:String):Fragment {
            val args = Bundle()
            args.putString("className",className)

            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            className = it.getString("className")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindObj = DataBindingUtil.inflate(layoutInflater, R.layout.search,container,false)
        binding = bindObj!!

        binding.initEditText.setOnClickListener(this)
        binding.imageButtonSearchBarBackButton.setOnClickListener(this)
        binding.imageViewSearchBarBtn.setOnClickListener(this)

        viewModel =ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        layoutManager = LinearLayoutManager(activityContext)
        searchPresenter = Presenter().apply {
            view = this@SearchFragment
            layoutManager = this@SearchFragment.layoutManager
            activity = requireActivity()
        }

        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextSearch.setOnKeyListener(this)
        binding.editTextSearch.addTextChangedListener(this)

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

                viewModel.bottomNavigationViewVisiblity.value= 1


            } else{
                if(binding.editTextSearch.text.isEmpty()){
                    binding.editTextSearch.hint = "찾으시는 주류가 있으신가요?"
                    viewModel.bottomNavigationViewVisiblity.value= 0
                }
            }
        }

        searchAdapter = initSearchAdapter()
        binding.recyclerViewSearchlist.adapter = searchAdapter
        binding.recyclerViewSearchlist.setHasFixedSize(false)
        binding.recyclerViewSearchlist.layoutManager = layoutManager


        //imeOption 설정하여 키보드에서 바로 검색되도록 핸들링
        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_GO ->{
                    if(binding.editTextSearch.text.isNotEmpty()){
                        binding.initEditText.visibility =View.VISIBLE
                        changeAdapter(binding.editTextSearch.text.toString(),false)
                        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
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
            this.add("-1")
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

    override fun changeAdapter(keyword: String?, relativeCheck: Boolean) {
        this.relativeCheck =relativeCheck
        searchPresenter.setSearchResult(keyword)
        binding.initEditText.visibility = View.VISIBLE //검색어 초기화 버튼 on
        GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext) // 키패드는 감추

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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView_searchBar_btn -> {
                if(binding.editTextSearch.text.isNotEmpty()){
                    binding.initEditText.visibility =View.VISIBLE
                    changeAdapter(binding.editTextSearch.text.toString(),false)
                    GlobalApplication.instance.keyPadSetting(binding.editTextSearch,activityContext)
                }
            }

            R.id.imageButton_searchBarBackButton -> {
                if(className =="main"){
                    (activity as? MainActivity)?.replaceFragment(MainFragment(),"main")
                }
                else if (className =="category"){
                    (activity as? MainActivity)?.replaceFragment(AlcoholCategoryFragment(),"category")
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
                searchPresenter.setRelativeSearch(s.toString())
            }, 300)
        }
        else{
            relativeCheck=true
            binding.editTextSearch.isFocusable = true
            // 연관검색어는 한번만 클릭되므로, 한번 이후는 다시 연관검색어가 검색되도록 만들어야한다.
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

    override fun onDestroy() {
        super.onDestroy()
        bindObj=null
        searchPresenter.detach()
        compositeDisposable.dispose()
    }
}