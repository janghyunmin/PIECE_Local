package com.bsstandard.piece.view.event

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.lifecycle.ViewModelProvider
import com.bsstandard.piece.R
import com.bsstandard.piece.base.BaseActivity
import com.bsstandard.piece.data.viewmodel.EventViewModel
import com.bsstandard.piece.databinding.ActivityEventBinding
import com.bsstandard.piece.view.adapter.event.EventAdapter
import com.bsstandard.piece.view.webview.EventDetailWebView
import com.bsstandard.piece.widget.utils.LogUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 *packageName    : com.bsstandard.piece.view.event
 * fileName       : EventActivity
 * author         : piecejhm
 * date           : 2022/09/11
 * description    : 이벤트 리스트 Activity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/09/11        piecejhm       최초 생성
 */

@AndroidEntryPoint
class EventActivity : BaseActivity<ActivityEventBinding>(R.layout.activity_event){
    private lateinit var vm: EventViewModel
    val mContext: Context = this@EventActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this@EventActivity)[EventViewModel::class.java]
        vm.viewInit(binding.eventRv)

        binding.eventVm = vm
        binding.lifecycleOwner = this@EventActivity

        setStatusBarIconColor(true) // 상태바 아이콘 true : 검정색
        setStatusBarBgColor("#ffffff") // 상태바 배경색상 설정
        setNaviBarIconColor(true) // 네비게이션 true : 검정색
        setNaviBarBgColor("#ffffff") // 네비게이션 배경색



        vm.getEvent()
        vm.eventAdapter.setOnItemClickListener(object : EventAdapter.OnItemClickListener {
            override fun onItemClick(v: View, eventId: String, eventImagePath: String) {
                LogUtil.logE("eventId : $eventId")
                LogUtil.logE("eventImagePath : $eventImagePath")
                val intent = Intent(mContext, EventDetailWebView::class.java)
                intent.putExtra("eventId", eventId)
                overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                startActivity(intent)

            }
        })
    }


    /** Util start **/
    /**
     * 상태바 아이콘 색상 지정
     * @param isBlack true : 검정색 / false : 흰색
     */
    private fun setStatusBarIconColor(isBlack: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android os 12에서 사용 가능

            window.insetsController?.let {
                it.setSystemBarsAppearance(
                    if (isBlack) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // minSdk 6.0부터 사용 가능
            window.decorView.systemUiVisibility = if (isBlack) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                // 기존 uiVisibility 유지
                window.decorView.systemUiVisibility
            }

        } // end if

    }

    /**
     * 상태바 배경 색상 지정
     * @param colorHexValue #ffffff 색상 값
     */
    private fun setStatusBarBgColor(colorHexValue: String) {

        // 상태바 배경색은 5.0부터 가능하나, 아이콘 색상은 6.0부터 변경 가능
        // -> 아이콘/배경색 모두 바뀌어야 의미가 있으므로 6.0으로 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.parseColor(colorHexValue)

        } // end if
    }

    /**
     * 내비바 아이콘 색상 지정
     * @param isBlack true : 검정색 / false : 흰색
     */
    private fun setNaviBarIconColor(isBlack: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android os 12에서 사용 가능

            window.insetsController?.let {
                it.setSystemBarsAppearance(
                    if (isBlack) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 내비바 아이콘 색상이 8.0부터 가능하므로 커스텀은 동시에 진행해야 하므로 조건 동일 처리.
            window.decorView.systemUiVisibility =
                if (isBlack) {
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

                } else {
                    // 기존 uiVisibility 유지
                    // -> 0으로 설정할 경우, 상태바 아이콘 색상 설정 등이 지워지기 때문
                    window.decorView.systemUiVisibility

                } // end if

        } // end if
    }

    /**
     * 내비바 배경 색상 설정
     * @param colorHexValue #ffffff 색상 값
     */
    private fun setNaviBarBgColor(colorHexValue: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 내비바 배경색은 8.0부터 지원한다.
            window.navigationBarColor = Color.parseColor(colorHexValue)

        } // end if

    }

}