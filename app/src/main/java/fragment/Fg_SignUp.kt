package fragment

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeoksyeo.wet.activity.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FgSignupBinding

class Fg_SignUp : Fragment(), TextWatcher {
    private lateinit var viewmodel: SignUpViewModel
    private lateinit var binding: FgSignupBinding
    var item: String = ""
    var info: String? = null
    private var check =false
    companion object {
        fun newInstance(info: String): Fg_SignUp {
            val fragment = Fg_SignUp()
            val bundle = Bundle()
            bundle.putString("info", info)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { info = it.getString("info") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fg_signup, container, false)
        binding.lifecycleOwner =this
        viewmodel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.userInfoHead.text = info
        binding.insertInfoEditText.hint = info + "를 입력해 주세요."
        binding.insertInfoEditText.addTextChangedListener(this)

        return binding.root
    }


    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s.toString().isEmpty()){
            viewmodel.setButtonState(false)
            check =false
        }
        else{
            if(!check){

                viewmodel.setButtonState(true)
                check =true
            }
        }
        item = s.toString()
    }
}