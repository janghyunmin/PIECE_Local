package com.bsstandard.piece.data.repository;

import com.bsstandard.piece.data.datamodel.dmodel.SmsAuthModel;
import com.bsstandard.piece.data.dto.SmsVerificationDTO;
import com.bsstandard.piece.retrofit.RetrofitClient;
import com.bsstandard.piece.retrofit.RetrofitService;
import com.bsstandard.piece.widget.utils.LogUtil;
import com.bsstandard.piece.widget.utils.SingleLiveEvent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * packageName    : com.bsstandard.piece.data.repository
 * fileName       : VerifyRepository
 * author         : piecejhm
 * date           : 2022/06/24
 * description    : 인증번호 검증 repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/06/24        piecejhm       최초 생성
 */
public class VerifyRepository {
    private static RetrofitService mInstance;
    private static VerifyRepository verifyRepository;
    private final SingleLiveEvent<SmsVerificationDTO> verifyData = new SingleLiveEvent<>();

    public static VerifyRepository getInstance() {
        if(verifyRepository == null) {
            verifyRepository = new VerifyRepository();
        }
        return verifyRepository;
    }
    public VerifyRepository() {
        mInstance = RetrofitClient.getService();
    }

    // 한번씩만 실행하게 SingleLiveEvent 사용 - jhm 2022/06/24
    public SingleLiveEvent<SmsVerificationDTO> getVerifyData(SmsAuthModel smsAuthModel){
        Call<SmsVerificationDTO> smsVerificationDTOCall = mInstance.PostVerification(smsAuthModel);
        smsVerificationDTOCall.enqueue(new Callback<SmsVerificationDTO>() {
            @Override
            public void onResponse(Call<SmsVerificationDTO> call, Response<SmsVerificationDTO> response) {
                if(response.body().getData()!=null){
                    LogUtil.logE("인증번호 검증 실행..");
                    verifyData.setValue(response.body());
                } else {
                    LogUtil.logE("post /member/call_sms_verification error " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SmsVerificationDTO> call, Throwable t) {
                LogUtil.logE("post /member/call_sms_verification error..." + t);
                verifyData.postValue(null);
            }
        });
        return verifyData;
    }


}
