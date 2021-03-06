package com.bsstandard.piece.view.passcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bsstandard.piece.R;
import com.bsstandard.piece.data.datamodel.dmodel.MemberPinModel;
import com.bsstandard.piece.data.datamodel.dmodel.join.ConsentModel;
import com.bsstandard.piece.data.datamodel.dmodel.join.DeviceInfoModel;
import com.bsstandard.piece.data.datamodel.dmodel.join.JoinModel;
import com.bsstandard.piece.data.datamodel.dmodel.join.NotificationInfoModel;
import com.bsstandard.piece.data.datamodel.dmodel.consent.ConsentList;
import com.bsstandard.piece.data.datasource.shared.PrefsHelper;
import com.bsstandard.piece.data.dto.AuthPinDTO;
import com.bsstandard.piece.data.dto.JoinDTO;
import com.bsstandard.piece.data.viewmodel.AuthPinPutViewModel;
import com.bsstandard.piece.data.viewmodel.AuthPinViewModel;
import com.bsstandard.piece.data.viewmodel.MemberJoinViewModel;
import com.bsstandard.piece.databinding.ActivityPasscodeBinding;
import com.bsstandard.piece.view.join.JoinActivity;
import com.bsstandard.piece.view.join.JoinSuccessActivity;
import com.bsstandard.piece.view.main.MainActivity;
import com.bsstandard.piece.widget.utils.CustomDialog;
import com.bsstandard.piece.widget.utils.CustomDialogListener;
import com.bsstandard.piece.widget.utils.CustomDialogPassCodeListener;
import com.bsstandard.piece.widget.utils.DeviceInfoUtil;
import com.bsstandard.piece.widget.utils.Division;
import com.bsstandard.piece.widget.utils.LogUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * packageName    : com.bsstandard.piece.view.passcode
 * fileName       : PassCodeActivity
 * author         : piecejhm
 * date           : 2022/06/26
 * description    : ?????????????????? ?????? ??? ????????? activity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/06/26        piecejhm       ?????? ??????
 */
public class PassCodeActivity extends AppCompatActivity {


    /**
     * ?????? - jhm 2022/05/09
     **/
    Context context;
    public String PushToken = "";
    private ActivityPasscodeBinding binding;
    private PassCodeViewModel passCodeViewModel;
    private ArrayList<String> passCodeModelList = new ArrayList<>(); // ????????? ?????????????????? ?????? ArrayList
    private ArrayList<String> passCodeVerifyList = new ArrayList<>(); // ????????? ?????????????????? ?????? ArrayList
    public CustomDialog customDialog;


    // ???????????? ?????? Model - jhm 2022/06/30
    private MemberJoinViewModel memberJoinViewModel;
    private JoinModel joinModel;
    ArrayList<ConsentModel> consent = new ArrayList<>();

    // ???????????? ??? ?????? ????????? ????????? ????????? - jhm 2022/07/03
    private String user_name = "";
    private String pinNumber;
    private String inputPinNumber;
    private String memberId;

    // ???????????? ????????? ?????? ????????? ?????? - jhm 2022/07/04
    private AuthPinViewModel authPinViewModel;
    private AuthPinPutViewModel authPinPutViewModel;
    private MemberPinModel memberPinModel;
    public String isJoin;
    public String pinNumChk;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passcode);
        binding.setLifecycleOwner(this);

        passCodeViewModel = ViewModelProviders.of(this).get(PassCodeViewModel.class);
        binding.setPasscode(passCodeViewModel);


        passCodeModelList.clear();
        passCodeVerifyList.clear();
        binding.passcodeTitleVerify.setVisibility(View.INVISIBLE); // ??????????????? ???????????? ????????? text

        InitFirebaseToken(); // Firebase Token - jhm 2022/07/01
        PinNumRandom(); // ?????? ???????????? 0~9 ?????? ?????? - jhm 2022/06/27
        PinNumberClear(); // ?????? ???????????? ????????? ?????? ????????? - jhm 2022/06/27
        KeyPadOnClick(); // ????????? ?????? ?????? - jhm 2022/06/27
        AutoLogin(); // ???????????? ?????? ??? ????????????????????? ???????????? ?????? - jhm 2022/07/03


        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinNumberDelete(); // ?????? ??????????????? ????????? ?????? - jhm 2022/06/27
            }
        });

    }

    // ???????????? ?????? - jhm 2022/07/01
    private void InitFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                Log.d("TAG", "retrieve token successful : " + token);
                PushToken = token;
            } else{
                Log.w("TAG", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
            //handle e
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task -> Log.v("TAG", "This is the token : " + task.getResult()));
    }



    // ???????????? ???????????? ????????? ?????? ????????????. - jhm 2022/06/28
    public void PinNumRandom() {
        List<String> padList = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            padList.add(String.valueOf(index));
        }
        Collections.shuffle(padList);
        passCodeViewModel.Code0.setValue(padList.get(0));
        passCodeViewModel.Code1.setValue(padList.get(1));
        passCodeViewModel.Code2.setValue(padList.get(2));
        passCodeViewModel.Code3.setValue(padList.get(3));
        passCodeViewModel.Code4.setValue(padList.get(4));
        passCodeViewModel.Code5.setValue(padList.get(5));
        passCodeViewModel.Code6.setValue(padList.get(6));
        passCodeViewModel.Code7.setValue(padList.get(7));
        passCodeViewModel.Code8.setValue(padList.get(8));
        passCodeViewModel.Code9.setValue(padList.get(9));
    }

    public void KeyPadOnClick() {
        binding.code0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code0.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code1.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code2.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code3.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code4.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code5.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code6.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code7.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code8.getText().toString());
                }
                PinNumInput();
            }
        });
        binding.code9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passCodeModelList.size() <= 6) {
                    passCodeModelList.add(binding.code9.getText().toString());
                }
                PinNumInput();
            }
        });
    }

    // ???????????? ????????? ????????? ?????? ??????
    public void PinNumInput() {
        binding.passcodeTitleVerify.setVisibility(View.INVISIBLE);
        setActiveChk(); // UI Update - jhm 2022/06/28

        // ????????? ?????? 6????????????
        if (passCodeModelList.size() == 6) {
            isJoin = PrefsHelper.read("isJoin", "false");
            pinNumChk = PrefsHelper.read("pinNumChk", "false");

            // ???????????? ???????????? ????????????. - jhm 2022/07/06
            inputPinNumber = passCodeModelList.get(0) + passCodeModelList.get(1) + passCodeModelList.get(2)
                    + passCodeModelList.get(3) + passCodeModelList.get(4) + passCodeModelList.get(5);
            PrefsHelper.removeKey(context, "inputPinNumber");
            PrefsHelper.write("inputPinNumber", inputPinNumber); // ?????? ?????? ??? ?????? ?????? - jhm 2022/07/06
            LogUtil.logE("inputPinNumber : " + inputPinNumber);


            // ????????? ????????? ????????? - jhm 2022/07/05
            if (isJoin.equals("false")) {
                // ???????????? ?????? ??????
                // ?????? ???????????? ???????????? ?????? - jhm 2022/07/06
                PinNumTwoStep();

            } else {
                // ????????? ??????????????? ????????? ???????????? ????????????.
                LogUtil.logE("??????????????? ");
                String sharedNum = PrefsHelper.read("pinNumber", "");
                LogUtil.logE("inputPinNumber : " + inputPinNumber); // ?????? ????????? ????????? - jhm 2022/07/06
                LogUtil.logE("sharedNum : " + sharedNum);

                // ???????????? ????????? - jhm 2022/07/06
                if(pinNumChk.equals("true")){
                    PinNumberUpdate();
                } else {
                    PinNumOneStep();
                }
            }
        }
    }


    // ?????? ???????????? ????????? ?????? 1??? - jhm 2022/07/05
    public void PinNumOneStep() {
        setActiveReset();
        setActiveChk();
        KeyPadOnClick();
        PinNumberClear();

        // ?????? PIN ?????? ??????
        authPinViewModel = ViewModelProviders.of(this).get(AuthPinViewModel.class);
        authPinViewModel.getAuthData().observe(binding.getLifecycleOwner(), new Observer<AuthPinDTO>() {
            @Override
            public void onChanged(AuthPinDTO authPinDTO) {
                if (authPinDTO != null) {
                    if (authPinDTO.getStatus().equals("OK")) {
                        Intent go_success = new Intent(context, MainActivity.class);
                        startActivity(go_success);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        PinNumberReset();
                    }
                } else {
                    PinNumberReset();
                }
            }
        });


    }

    // ?????????????????? ????????? Dialog - jhm 2022/07/05
    public void PinNumberReset() {
        // ????????? ???????????? ????????? - jhm 2022/07/05
        CustomDialogListener customDialogListener = new CustomDialogListener() {
            @Override
            public void onOkButtonClicked() {
            }
        };
        CustomDialogPassCodeListener customDialogPassCodeListener = new CustomDialogPassCodeListener() {
            @Override
            public void onCancleButtonClicked() {
                LogUtil.logE("?????? onclick..");
                customDialog.dismiss();

                // ?????? ?????? ?????? ??? ?????? - jhm 2022/07/04
                passCodeModelList.clear(); // ?????? ArrayList ????????? - jhm 2022/06/28
                setActiveReset();
                binding.passcodeTitleVerify.setVisibility(View.VISIBLE); // ??????????????? ???????????? ????????? text
                KeyPadOnClick();
            }

            @Override
            public void onRetryPassCodeButtonClicked() {
                LogUtil.logE("???????????? ?????????");
                PrefsHelper.write("pinNumChk", "true");
                pinNumber = "";
                passCodeModelList.clear(); // ?????? ArrayList ????????? - jhm 2022/06/28
                customDialog.dismiss();
                Intent go_join = new Intent(context, JoinActivity.class);
                context.startActivity(go_join);
                finish();
            }
        };
        customDialog = new CustomDialog(context, Division.DIALOG_C_PASSWORD, customDialogListener, customDialogPassCodeListener);
        customDialog.show();
        LogUtil.logE("?????????????????? ????????? ??????..");
    }


    // ????????? ????????? ????????? ?????? pinNumber ?????? - jhm 2022/07/05
    public void PinNumTwoStep() {
        PinNumRandom(); // ????????? ????????? ??? ?????? ????????? - jhm 2022/06/28
        setActiveReset(); // UI ?????? - jhm 2022/06/28
        setActiveChk(); // UI ?????? - jhm 2022/07/05
        PinNumberValidate(Division.PIN_VIEWTYPE_1); // PinNumber - jhm 2022/07/05
    }

    // ????????? ????????? ????????? ???????????? ???????????? ????????? ?????? - jhm 2022/07/05
    public void PinNumberUpdate() {
        PinNumRandom(); // ????????? ????????? ??? ?????? ????????? - jhm 2022/06/28
        setActiveReset(); // UI ?????? - jhm 2022/06/28
        setActiveChk(); // UI ?????? - jhm 2022/07/05
        PinNumberValidate(Division.PIN_VIEWTYPE_2); // PinNumber - jhm 2022/07/05
    }


    // ???????????? ?????? ?????? ??? ????????? ?????? ?????? - jhm 2022/06/28
    public void PinNumberValidate(String division) {
        if (passCodeVerifyList.size() == 0) {
            passCodeVerifyList.addAll(passCodeModelList);
            passCodeModelList.clear(); // ?????? ArrayList ????????? - jhm 2022/06/28
            setActiveReset();
            KeyPadOnClick();
            binding.passcodeTitleC.setText(context.getString(R.string.passcode_title_re));
        } else {
            System.out.println("passCodeVerifyList ???????????? 0 ??? ??????");
            // ???????????? Array ????????? ???????????? ?????? - jhm 2022/06/30
            if (passCodeModelList.size() == passCodeVerifyList.size()) {
                // ???????????? ??????????????? ?????? ?????? ?????? - jhm 2022/06/30
                // ?????? ??????????????? - jhm 2022/06/30
                if (passCodeModelList.containsAll(passCodeVerifyList)) {
                    System.out.println("??? ?????? ??????");

                    System.out.println(passCodeModelList.toString());
                    System.out.println(passCodeVerifyList.toString());

                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();

                    user_name = PrefsHelper.read("name", "");
                    PrefsHelper.write("pinNumber", inputPinNumber);
                    pinNumber = PrefsHelper.read("pinNumber", ""); // ???????????? ??????????????? ???????????? ????????????.

                    LogUtil.logE("user_name : " + user_name);
                    LogUtil.logE("pinNumber : " + pinNumber);
                    // ?????? ID - jhm 2022/06/30
                    String deviceId = DeviceInfoUtil.getDeviceId(context);
                    PrefsHelper.write("deviceId", deviceId);


                    LogUtil.logE("division : " + division);
                    if (division.equals("??????")) {
                        LogUtil.logE("????????? ??????.." + division);
                        DeviceInfoModel deviceInfo = new DeviceInfoModel(
                                deviceId,
                                "MDO0101",
                                "",
                                "",
                                PushToken);

                        NotificationInfoModel notificationInfo = new NotificationInfoModel(
                                "Y",
                                "Y",
                                "Y",
                                "Y"
                        );

                        ArrayList<ConsentList> consentList = bundle.getParcelableArrayList("consentList");
                        for (int index = 0; index < consentList.size(); index++) {
                            consent.add(new ConsentModel(
                                    consentList.get(index).getConsentCode(),
                                    "Y"
                            ));
                        }

                        // ?????? ???????????? Join ?????? - jhm 2022/06/30
                        memberJoinViewModel = ViewModelProviders.of(this).get(MemberJoinViewModel.class);

                        // db??? ???????????? insert ??? "-" ?????? - jhm 2022/07/06
                        String userBirth = bundle.getString("birthDay");
                        userBirth = userBirth.substring(0, 4) + "-" + userBirth.substring(4, 6) + "-" + userBirth.substring(6, 8);


                        joinModel = new JoinModel(
                                user_name,
                                pinNumber,
                                bundle.getString("cellPhoneNo"),
                                userBirth,
                                bundle.getString("ci"),
                                bundle.getString("di"),
                                bundle.getString("gender"),
                                bundle.getString("isFido"),
                                deviceInfo,
                                notificationInfo,
                                consent
                        );

                        Gson gson = new Gson();
                        System.out.println("joinModel : " + gson.toJson(joinModel));
                        // ???????????? ?????? - jhm 2022/06/30
                        memberJoinViewModel.postCallJoinData(joinModel);
                        // ?????? ??? ?????? ???????????? ?????????. - jhm 2022/06/30
                        memberJoinViewModel.joinData.observe(binding.getLifecycleOwner(), new Observer<JoinDTO>() {
                            @Override
                            public void onChanged(JoinDTO joinDTO) {
                                LogUtil.logE("joinVO : " + gson.toJson(joinDTO));


                                // ?????? ??? Shared??? ????????? ????????????. - jhm 2022/07/03
                                PrefsHelper.init(context);
                                PrefsHelper.write("memberId", joinDTO.getData().getMemberId());
                                PrefsHelper.write("name", user_name);
                                PrefsHelper.write("pinNumber", pinNumber);

                                PrefsHelper.write("isJoin", "true"); // ??????????????? ?????? - jhm 2022/07/06
                                PrefsHelper.write("pinNumChk", "false"); // ???????????? ????????? ?????? - jhm 2022/07/06

                                PrefsHelper.removeKey(context,"accessToken");
                                PrefsHelper.write("accessToken", joinDTO.getData().getAccessToken());


                            }
                        });

                        authPinPutViewModel = ViewModelProviders.of(this).get(AuthPinPutViewModel.class);
                        String _memberId = PrefsHelper.read("memberId", "");
                        pinNumber = PrefsHelper.read("pinNumber", "");
                        LogUtil.logE("????????? ?????? : " + pinNumber);


                        memberPinModel = new MemberPinModel(_memberId, pinNumber);
                        authPinPutViewModel.putCallAuthPinData(memberPinModel);
                        authPinPutViewModel.authPinData.observe(binding.getLifecycleOwner(), new Observer<AuthPinDTO>() {
                            @Override
                            public void onChanged(AuthPinDTO authPinDTO) {
                                LogUtil.logE("authPinDTO : " + authPinDTO.getStatus());
                                PrefsHelper.write("pinNumber", pinNumber);

                            }
                        });

                    } else {
                        authPinPutViewModel = ViewModelProviders.of(this).get(AuthPinPutViewModel.class);
                        String _memberId = PrefsHelper.read("memberId", "");
                        pinNumber = PrefsHelper.read("pinNumber", "");
                        LogUtil.logE("????????? ?????? : " + pinNumber);


                        memberPinModel = new MemberPinModel(_memberId, pinNumber);
                        authPinPutViewModel.putCallAuthPinData(memberPinModel);
                        authPinPutViewModel.authPinData.observe(binding.getLifecycleOwner(), new Observer<AuthPinDTO>() {
                            @Override
                            public void onChanged(AuthPinDTO authPinDTO) {
                                LogUtil.logE("authPinDTO : " + authPinDTO.getStatus());
                                PrefsHelper.write("pinNumber", pinNumber);

                            }
                        });
                        PrefsHelper.write("pinNumChk", "false");
                    }

                    // ???????????? ?????? ??? ?????? ?????? ?????? - jhm 2022/07/01
                    Intent go_success = new Intent(context, JoinSuccessActivity.class);
                    go_success.putExtra("name", user_name);
                    startActivity(go_success);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                // ???????????? ??????????????? ?????? ????????? - jhm 2022/06/30
                else {
                    // ???????????? ?????? ???????????????. - jhm 2022/06/30
                    System.out.println("???????????? ????????? ?????? ??????");
                    binding.passcodeTitleC.setText(context.getString(R.string.passcode_title_c));
                    binding.passcodeTitleVerify.setVisibility(View.VISIBLE);
                    passCodeModelList.clear();
                    passCodeVerifyList.clear();
                    setActiveReset();
                    KeyPadOnClick();
                }
            }
        }
    }

    // ?????????????????? ??????????????? ???????????? ????????? - jhm 2022/06/27
    public void PinNumberDelete() {
        if (passCodeModelList.size() > 0) {
            LogUtil.logE("size 0 ?????? ???");

            try {
                int index = passCodeModelList.size() - 1;
                passCodeModelList.remove(index);
                setActiveChk();
                System.out.println(passCodeModelList);
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
            }
        }
    }

    // ????????? ?????????????????? ?????? ????????? - jhm 2022/06/27
    public void PinNumberClear() {
        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passCodeModelList.clear();
                setActiveChk();
            }
        });
    }


    public void setActiveReset() {
        binding.passcode1.setActivated(false);
        binding.passcode2.setActivated(false);
        binding.passcode3.setActivated(false);
        binding.passcode4.setActivated(false);
        binding.passcode5.setActivated(false);
        binding.passcode6.setActivated(false);
    }

    // ?????????????????? Ui - jhm 2022/06/28
    public void setActiveChk() {
        if (passCodeModelList.size() == 0) {
            binding.passcode1.setActivated(false);
            binding.passcode2.setActivated(false);
            binding.passcode3.setActivated(false);
            binding.passcode4.setActivated(false);
            binding.passcode5.setActivated(false);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 1) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(false);
            binding.passcode3.setActivated(false);
            binding.passcode4.setActivated(false);
            binding.passcode5.setActivated(false);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 2) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(true);
            binding.passcode3.setActivated(false);
            binding.passcode4.setActivated(false);
            binding.passcode5.setActivated(false);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 3) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(true);
            binding.passcode3.setActivated(true);
            binding.passcode4.setActivated(false);
            binding.passcode5.setActivated(false);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 4) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(true);
            binding.passcode3.setActivated(true);
            binding.passcode4.setActivated(true);
            binding.passcode5.setActivated(false);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 5) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(true);
            binding.passcode3.setActivated(true);
            binding.passcode4.setActivated(true);
            binding.passcode5.setActivated(true);
            binding.passcode6.setActivated(false);
        } else if (passCodeModelList.size() == 6) {
            binding.passcode1.setActivated(true);
            binding.passcode2.setActivated(true);
            binding.passcode3.setActivated(true);
            binding.passcode4.setActivated(true);
            binding.passcode5.setActivated(true);
            binding.passcode6.setActivated(true);
        }
    }


    public void AutoLogin() {
        memberId = PrefsHelper.read("memberId", "");
        LogUtil.logE("memberId : " + memberId);
        // memberId??? ????????? ????????? ?????? ???????????? ??????????????? ????????? ???????????? ?????? ?????? ?????? - jhm 2022/07/03
        if (!memberId.equals("")) {
            passCodeModelList.clear();
            setActiveReset();
            KeyPadOnClick();
        }
    }
}
