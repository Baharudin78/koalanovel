package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {

    private PrefManager prefManager;

    LinearLayout lyBack, lyPassVisibility;
    TextView txt_registration, txt_signup, txtPassVisible;
    EditText et_fullname, et_email, et_password, et_phone;
    ImageView iv_icon;

    String str_fullname, str_email, str_password, str_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.HideNavigation(Registration.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.registration);
        Utils.screenCapOff(Registration.this);
        PrefManager.forceRTLIfSupported(getWindow(), Registration.this);

        Init();

        lyPassVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    Toasty.warning(Registration.this, "" + getResources().getString(R.string.enter_password), Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (et_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    txtPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_invisible));
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    txtPassVisible.setBackground(getResources().getDrawable(R.drawable.ic_pass_visible));
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        txt_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_fullname = et_fullname.getText().toString();
                str_email = et_email.getText().toString();
                str_password = et_password.getText().toString();
                str_phone = et_phone.getText().toString();

                if (TextUtils.isEmpty(str_fullname)) {
                    Toasty.warning(Registration.this, "" + getResources().getString(R.string.enter_fullname), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(str_email)) {
                    Toasty.warning(Registration.this, "" + getResources().getString(R.string.enter_email), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(str_password)) {
                    Toasty.warning(Registration.this, "" + getResources().getString(R.string.enter_password), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(str_phone)) {
                    Toasty.warning(Registration.this, "" + getResources().getString(R.string.enter_phone_number), Toasty.LENGTH_SHORT).show();
                    return;
                }

                SignUp();
            }
        });

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void Init() {
        try {
            prefManager = new PrefManager(this);

            lyBack = findViewById(R.id.lyBack);
            lyPassVisibility = findViewById(R.id.lyPassVisibility);
            txtPassVisible = findViewById(R.id.txtPassVisible);

            txt_registration = findViewById(R.id.txt_registration);
            et_fullname = findViewById(R.id.et_fullname);
            et_email = findViewById(R.id.et_email);
            et_password = findViewById(R.id.et_password);
            et_phone = findViewById(R.id.et_phone);
            txt_signup = findViewById(R.id.txt_signup);

            iv_icon = findViewById(R.id.iv_icon);
        } catch (Exception e) {
            Log.e("Init Exception =>", "" + e);
        }
    }

    private void SignUp() {
        Utils.ProgressBarShow(Registration.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.Registration(str_fullname, str_email, str_password, str_phone);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
//                        Log.e("email =>", "" + response.body().getResult().get(0).getEmail());
//
//                        prefManager.setFirstTimeLaunch(false);
//                        Utils.storeUserCred(Registration.this,
//                                "" + response.body().getResult().get(0).getId(),
//                                "" + response.body().getResult().get(0).getType(),
//                                "" + response.body().getResult().get(0).getEmail(),
//                                "" + response.body().getResult().get(0).getFullname(),
//                                "" + response.body().getResult().get(0).getMobile());
//                        Log.e("LoginId ==>", "" + prefManager.getLoginId());

                        Toasty.success(Registration.this, "" + response.body().getMessage(), Toasty.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Utils.AlertDialog(Registration.this, response.body().getMessage(), false, false);
                    }
                } catch (Exception e) {
                    Log.e("Registration", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("Registration", "onFailure => " + t.getMessage());
                Utils.ProgressbarHide();
                Utils.AlertDialog(Registration.this, t.getMessage(), false, false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.ProgressbarHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.ProgressbarHide();
    }

}
