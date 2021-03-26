package com.activities.favorite

import android.annotation.SuppressLint
import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.viewmodel.FavoriteViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.FavoriteActivityBinding

class FavoriteActivity : BaseActivity<FavoriteActivityBinding>(), FavoriteContract.FavoriteView {
    private lateinit var presenter: Presenter
    private lateinit var viewmodel: FavoriteViewModel
    override val layoutResID: Int = R.layout.favorite_activity

    @SuppressLint("SetTextI18n")
    override fun setOnCreate() {
        viewmodel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        presenter = Presenter().apply {
            viewObj = this@FavoriteActivity
            activity = this@FavoriteActivity
        }

        setHeaderInit()

        presenter.initTabLayout()
        presenter.initProfile()

        binding.favoriteTablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //선택되지 않은 탭 원래 색상으로 변경
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (tab?.customView as? TextView)?.setTextColor(
                        resources.getColor(
                            R.color.tabColor,
                            null
                        )
                    )
                } else {
                    (tab?.customView as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            this@FavoriteActivity,
                            R.color.tabColor
                        )
                    )
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                //선택된 탭의 텍스트 색 변경
                viewmodel.currentPosition.value = tab?.position
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    (tab?.customView as? TextView)?.setTextColor(
                        resources.getColor(
                            R.color.orange,
                            null
                        )
                    )
                } else {
                    (tab?.customView as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            this@FavoriteActivity,
                            R.color.orange
                        )
                    )
                }
            }
        })

        //뷰페이저 전환 시, 해당 타입에서 찜한 전체 주류 표시
        viewmodel.currentPosition.observe(this, Observer {
            if (it != -1) {
                binding.typeCountText.text = "총 ${viewmodel.alcoholTypeList[it]}개의 주류를 찜하셨습니다."
                binding.favoriteAlcoholTypeText.text = presenter.tabList[it]
            }
        })

        //모든 타입에서 몇개의 주류를 찜했는지 표시
        viewmodel.summaryCount.observe(this, Observer {
            binding.profileHeader.ratedCountText.text =
                "총 ${viewmodel.summaryCount.value}개의 주류를 찜하셨습니다."
        })
    }

    override fun destroyPresenter() {
        presenter.detach()
    }

    override fun getBindingObj(): FavoriteActivityBinding {
        return binding
    }

    override fun setHeaderInit() {
        //타이틀 변경
        binding.basicHeader.title.text = "내가 찜한 주류"

        //디바이스에 맞는 텍스트 크기 지정
        binding.basicHeader.title.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            GlobalApplication.instance.getCalculatorTextSize(16f)
        )

        //statusBar 색상변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.orange, null)
            } else {
                window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current, R.anim.current_to_right)
    }
}