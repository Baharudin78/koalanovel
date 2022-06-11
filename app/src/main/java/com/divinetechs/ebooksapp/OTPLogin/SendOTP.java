package com.divinetechs.ebooksapp.OTPLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.divinetechs.ebooksapp.Activity.Registration;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import es.dmoral.toasty.Toasty;

public class SendOTP extends AppCompatActivity {

    private PrefManager prefManager;

    LinearLayout lyBack;
    TextView txtSendOtp, txtRegister;
    TextInputEditText editTextPhone, editTextCountryCode;
    CountryCodePicker countryCodePicker;

    String mobileNumber, coutryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.HideNavigation(SendOTP.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_otp_send);
        Utils.screenCapOff(SendOTP.this);
        PrefManager.forceRTLIfSupported(getWindow(), SendOTP.this);

        init();

        txtSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = editTextPhone.getText().toString().trim();
                coutryCode = countryCodePicker.getSelectedCountryCode();
                Log.e("coutryCode", "" + coutryCode);

                if (TextUtils.isEmpty(mobileNumber)) {
                    Toasty.warning(SendOTP.this, "" + getResources().getString(R.string.enter_your_mobile_no), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(coutryCode)) {
                    Toasty.warning(SendOTP.this, "" + getResources().getString(R.string.enter_coutry_code), Toasty.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SendOTP.this, com.divinetechs.ebooksapp.OTPLogin.OTPVerification.class);
                intent.putExtra("entryFrom", "SendOTP");
                intent.putExtra("mobile", "+" + coutryCode + mobileNumber);
                startActivity(intent);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendOTP.this, Registration.class));
                finish();
            }
        });

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendOTP.this.finish();
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(SendOTP.this);

            lyBack = findViewById(R.id.lyBack);
            editTextPhone = findViewById(R.id.editTextPhone);
            countryCodePicker = findViewById(R.id.edtCountryCodePicker);
            txtSendOtp = findViewById(R.id.txtSendOtp);
            txtRegister = findViewById(R.id.txtRegister);

            countryCodePicker.setCountryForNameCode("IN");

        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}