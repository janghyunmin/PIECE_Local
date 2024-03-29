package com.bsstandard.piece.view.portfolioDetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsstandard.piece.R
import com.bsstandard.piece.base.BaseActivity
import com.bsstandard.piece.databinding.ActivityPortfoliodetailBinding
import com.bsstandard.piece.view.adapter.portfolio.PortfolioDetailCompositionAdapter
import com.bsstandard.piece.view.adapter.portfolio.PortfolioDetailEvidenceAdapter
import com.bsstandard.piece.widget.utils.ConvertMoney
import com.bsstandard.piece.widget.utils.IndentLeadingMarginSpan
import com.bsstandard.piece.widget.utils.LogUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.products_item_composition_layout.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 *packageName    : com.bsstandard.piece.view.portfolioDetail
 * fileName       : TestActivity
 * author         : piecejhm
 * date           : 2022/07/21
 * description    : 포트폴리오 상세 Activity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/07/21        piecejhm       최초 생성
 */

@AndroidEntryPoint
class PortfolioDetailActivity :
    BaseActivity<ActivityPortfoliodetailBinding>(R.layout.activity_portfoliodetail) {

    val args: PortfolioDetailActivityArgs by navArgs()
    var portfolioId: String = "";
    private lateinit var vm: PortfolioDetailViewModel
    private var disposable: Disposable? = null
    private var selectedItemPosition = -1
    private var selectedLayout: ConstraintLayout? = null
    private lateinit var portfolioDetailCompositionAdapter: PortfolioDetailCompositionAdapter

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.apply {
            lifecycleOwner = this@PortfolioDetailActivity
            binding.activity = this@PortfolioDetailActivity
            binding.portfolioDetailViewModel = portfolioDetailViewModel

            vm = ViewModelProvider(this@PortfolioDetailActivity)[PortfolioDetailViewModel::class.java]

            // statusBar 공통 - jhm 2022/06/13
            setStatusBar()
            setStatusBarIconColor(false) // 상태바 아이콘 true : 검정색
            setStatusBarBgColor("#00000000") // 상태바 배경색상 설정
            postponeEnterTransition()

            portfolioId = args.portfolioId
            LogUtil.logE("넘겨받은 portfolioId : $portfolioId")

            val portfolioPath = args.portfolioImagePath
            LogUtil.logE("넘겨받은 portfolioImagePath $portfolioPath")


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Glide.with(this@PortfolioDetailActivity)
                    .load(portfolioPath)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }
                    })
                    .into(binding.portfolioImg)
            }


            // 포트폴리오 상세 슬라이드시 toolbar icon 색상 변경 로직 - jhm 2022/08/16
            // 현재 접힌 상태에서의 BottomSheet 귀퉁이의 둥글기 저장
            val cornerRadius = bottomSheet.radius
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheet.setCardBackgroundColor(resources.getColor(R.color.c_ffffff))
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    setStatusBar()
                    setStatusBarIconColor(false) // 상태바 아이콘 true : 검정색
                    setStatusBarBgColor("#ffffff") // 상태바 배경색상 설정
                    postponeEnterTransition()

                    if (newState == STATE_EXPANDED) {
                        LogUtil.logE("완전히 펼쳐진 상태")
                        binding.topLayout.setBackgroundColor(resources.getColor(R.color.c_ffffff))
                        Glide.with(applicationContext).load(R.drawable.arrow_left)
                            .into(binding.backImg)
                        Glide.with(applicationContext).load(R.drawable.share_icon)
                            .into(binding.shareImg)


                    } else if (newState == STATE_COLLAPSED) {
                        LogUtil.logE("접혀있는 상태")
                        binding.topLayout.setBackgroundColor(resources.getColor(R.color.trans))
                        Glide.with(applicationContext).load(R.drawable.arrow_left_white)
                            .into(binding.backImg)
                        Glide.with(applicationContext).load(R.drawable.share_icon_white)
                            .into(binding.shareImg)
                    }
                }

                override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                    // slideOffset 접힘 -> 펼쳐짐: 0.0 ~ 1.0
                    if (slideOffset >= 0) {
                        // 둥글기는 펼칠수록 줄어들도록
                        binding.bottomSheet.radius = cornerRadius - (cornerRadius * slideOffset)
                        // 내용의 투명도도 같이 조절...
//                        binding.fragment.alpha = Math.min(slideOffset * 2F, 1F)
                    }
                }
            })

            vm.getPortfolioDetail(portfolioId)
            vm.detailResponse.observe(this@PortfolioDetailActivity, Observer {

                LogUtil.logE("portfolio Detail response : ${it.data.createdAt}")


                // 판매 현황 상태값 - jhm 2022/08/19
                when (it.data.recruitmentState) {
                    "PRS0101" -> binding.status.text = "오픈예정"
                    "PRS0102" -> binding.status.text = "판매 중"
                    "PRS0103" -> binding.status.text = "조각완판"
                    "PRS0104" -> binding.status.text = "매각대기"
                    "PRS0105" -> binding.status.text = "매각진행"
                    "PRS0106" -> binding.status.text = "매각완료"
                    "PRS0107" -> binding.status.text = "정산중"
                    "PRS0108" -> binding.status.text = "분배완료"
                    "PRS0109" -> binding.status.text = "일시중지"
                    "PRS0110" -> binding.status.text = "기한만료"
                    "PRS0111" -> binding.status.text = "수익분배"
                }

                // 판매 누적 금액 - jhm 2022/08/19
                binding.sellPrice.text = "" // 판매 누적 금액 액수 - jhm 2022/08/19

                // 판매 누적 퍼센트 - jhm 2022/08/19

                // 총 판매 금액 - jhm 2022/08/19
                val amount = it.data.recruitmentAmount
                val toLongAmount: Long = amount.toLong()
                LogUtil.logE("toLong " + ConvertMoney().getNumKorString(toLongAmount))
                binding.price.text = ConvertMoney().getNumKorString(toLongAmount) + "만원"


                // 판매 기간 - jhm 2022/08/19
                val startDate = it.data.recruitmentBeginDate
                val startFormatDate = getStartFormatDate(startDate)
                val endFormatDate = getEndDateFormatDate(startDate)

                binding.date.text = "$startFormatDate ~ $endFormatDate"


                // 구매 포인트 RecyclerView 연결 - jhm 2022/08/18
                vm.viewInitHorizontal(binding.purchaseGuidesRv, 1)


                // 판매 정보 - jhm 2022/08/19
                // 판매 정보 - 운용 기간 - jhm 2022/08/19
                val operDate = DateBetweenEndAndStart(
                    it.data.recruitmentBeginDate,
                    it.data.dividendsExpecatationDate
                )
                var yearFormat = ""
                if (operDate < 365.toString()) {
                    yearFormat = operDate + "일"
                    LogUtil.logE("yearFormat $yearFormat")

                } else if (operDate >= 365.toString()) {
                    yearFormat = "1년"
                }
                binding.operDate.text = yearFormat


                // 판매 정보 - 예상 수익률 - jhm 2022/08/19
                binding.rateText.text = it.data.expectationProfitRate + "%"


                // 판매 정보 - 판매 수량 - jhm 2022/08/19
                binding.amountText.text = it.data.totalPieceVolume + " 피스"


                // 판매 정보 - 판매 단위 - jhm 2022/08/19
                binding.unitText.text = StringDiv(
                    it.data.recruitmentAmount.toInt(),
                    it.data.totalPieceVolume.toInt()
                ).toString() + " 원"


                // 안정성 등급 점수 String -> Int 형변환 후 set - jhm 2022/08/18
                val stabilityPoint = it.data.stabilityPoint
                val IntStaPoint: Int = stabilityPoint.toInt()

                // 환금성 등급 점수 String -> Int 형변환 후 set - jhm 2022/08/18
                val cashabilityPoint = it.data.cashabilityPoint
                val IntCashPoint = cashabilityPoint.toInt()

                // 수익성 등급 점수 String -> Int 형 변환 후 set - jhm 2022/08/18
                val profitabilityPoint = it.data.profitabilityPoint
                val IntProPoint = profitabilityPoint.toInt()

                // 포트폴리오 등급 set text - jhm 2022/08/19
                binding.grade.text = it.data.generalGrade + "등급"

                // 종합등급 progressbar setting - jhm 2022/08/19
                binding.progressbar1.progress = IntStaPoint
                binding.progressbar2.progress = IntCashPoint
                binding.progressbar3.progress = IntProPoint
                binding.score1.text = stabilityPoint
                binding.score2.text = cashabilityPoint
                binding.score3.text = profitabilityPoint


                // 포트폴리오 구성 - jhm 2022/08/21
                binding.infoTitle.text = it.data.title // 포트폴리오 제목 - jhm 2022/08/21
                vm.viewInitVertical(binding.productsRv) // 포트폴리오 구성 - jhm 2022/08/22


                // 총 판매 금액 - jhm 2022/08/22
                binding.allAmount.text = ConvertMoney().getNumKorString(toLongAmount) + "만원"


                // 구매 가능 금액 - jhm 2022/08/22
                val maxAmount = it.data.maxPurchaseAmount
                val toLongMaxAmout: Long = maxAmount.toLong()
                binding.purchaseAmount.text =
                    "최소 " + it.data.minPurchaseAmount + "원 ~ 최대 " + ConvertMoney().getNumKorString(
                        toLongMaxAmout
                    ) + "만 원"
                var pieceAmount =
                    StringDiv(it.data.recruitmentAmount.toInt(), it.data.totalPieceVolume.toInt())
                var min = StringDiv(it.data.minPurchaseAmount.toInt(), pieceAmount)
                var max = StringDiv(it.data.maxPurchaseAmount.toInt(), pieceAmount)
                binding.purchaseAmountCount.text = "최소 " + min + "피스 ~ 최대 " + max + "피스"

                // 운용기간 - jhm 2022/08/22
                binding.operDateDetail.text = "$yearFormat (조기 분배 가능)"

                // 포트폴리오 구성 RecyclerView 연결 - jhm 2022/08/18
                //vm.viewInitHorizontal(binding.compositionRv, 2)

                // 포트폴리오 구성 썸네일 있는부분 - jhm 2022/08/22
                // 썸네일 radius - jhm 2022/08/24
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transforms(
                    CenterCrop(),
                    RoundedCorners(20)
                )
                for (i in 0 until it.data.products.size) {
                    Glide.with(applicationContext)
                        .load(it.data.products[i].representThumbnailImagePath).apply(requestOptions)
                        .into(binding.imagePath)
                    binding.productionYear.text = it.data.products[i].productionYear
                    binding.productTitle.text = it.data.products[i].title
                    binding.author.text = it.data.products[i].author
                    binding.productMaterial.text = it.data.products[i].productMaterial
                    binding.productSize.text = it.data.products[i].productSize
                }

                // 포트폴리오 구성 - jhm 2022/08/25
                val adapter = PortfolioDetailCompositionAdapter(vm, context = applicationContext)
                binding.compositionRv.adapter = adapter
                binding.compositionRv.layoutManager =  LinearLayoutManager(application, RecyclerView.HORIZONTAL, false)

                adapter.setItemClickListener(object : PortfolioDetailCompositionAdapter.OnItemClickListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onClick(v: View, position: Int) {
                        LogUtil.logE("position $position")

                        Glide.with(applicationContext)
                            .load(it.data.products[position].representThumbnailImagePath).apply(requestOptions)
                            .into(binding.imagePath)
                        binding.productionYear.text = it.data.products[position].productionYear
                        binding.productTitle.text = it.data.products[position].title
                        binding.author.text = it.data.products[position].author
                        binding.productMaterial.text = it.data.products[position].productMaterial
                        binding.productSize.text = it.data.products[position].productSize

                        adapter.notifyDataSetChanged()
                    }
                })

                val documentAdapter = PortfolioDetailEvidenceAdapter(vm, context = applicationContext)
                binding.evidenceRv.adapter = documentAdapter
                binding.evidenceRv.layoutManager = LinearLayoutManager(application, RecyclerView.HORIZONTAL,false)

            })

            // 유의사항 text 정렬 - jhm 2022/08/25
            binding.contentText.text = SpannableStringBuilder(applicationContext.getText(R.string.portfolio_detail_notice_text)).apply {
                setSpan(IndentLeadingMarginSpan(), 0, length, 0)
            }

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        LogUtil.logE("PortfolioDetailActivity onStart..")
    }

    // 포트폴리오 상세 상단 뒤로가기 화살표 onClick - jhm 2022/08/12
    fun onBackButton() {
        LogUtil.logE("뒤로가기 onClick..")
        finish()
    }

    // 포트폴리오 상세 상단 공유하기 onClick - jhm 2022/08/12
    fun onShareButton() {
        LogUtil.logE("공유하기 onClick..")
    }




    // 포트폴리오 판매 시작 날짜 format - jhm 2022/08/21
    @SuppressLint("SimpleDateFormat")
    fun getStartFormatDate(strDate: String): String {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")//old format
            val dateFormat2 = SimpleDateFormat("yyyy.MM.dd")//require new formate
            val objDate = dateFormat.parse(strDate)

            return dateFormat2.format(objDate)
        } catch (e: Exception) {
            return ""
        }
    }

    // 포트폴리오 판매 종료 날짜 format - jhm 2022/08/21
    fun getEndDateFormatDate(endDate: String): String {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")//old format
            val objDate = dateFormat.parse(endDate)

            // 시작날짜로부터 +3일 - jhm 2022/08/21
            val cal = Calendar.getInstance()
            cal.time = objDate
            val df: DateFormat = SimpleDateFormat("yyyy.MM.dd")
            cal.add(Calendar.DATE, +3)
            df.format(cal.time)

            return df.format(cal.time)
        } catch (e: Exception) {
            return ""
        }
    }


    private fun DateBetweenEndAndStart(start: String, end: String): String {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd")

            val startDate = dateFormat.parse(start).time
            val endDate = dateFormat.parse(end).time

            // 시작일로부터 일수 구하기 로직 - jhm 2022/08/21
//        val today = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }.time.time


            val operDate = (endDate - startDate) / (24 * 60 * 60 * 1000)
            return operDate.toString()
        } catch (ex: Exception) {
            return ""
        }
    }

    // 판매단위 - jhm 2022/08/21
    fun StringDiv(amount: Int, volume: Int): Int {
        LogUtil.logE("amount : $amount")
        LogUtil.logE("volume : $volume")
        var result = (amount).div(volume)
        LogUtil.logE("result : $result")
        return result
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.let { disposable!!.dispose() }
    }


    /** Util start **/
    /**
     * 상태바 아이콘 색상 지정
     * @param isBlack true : 검정색 / false : 흰색
     */

    private fun setStatusBar() {
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

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
    /** Util end **/


}