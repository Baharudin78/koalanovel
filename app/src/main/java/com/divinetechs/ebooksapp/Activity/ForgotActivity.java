package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class ForgotActivity extends AppCompatActivity {

    EditText etEmail;
    String strEmail;
    TextView txtSend;
    LinearLayout lyBack;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.screenCapOff(ForgotActivity.this);
        Utils.HideNavigation(ForgotActivity.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.forgotactivity);

        Init();

        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = etEmail.getText().toString();
                if (TextUtils.isEmpty(strEmail)) {
                    Toasty.warning(ForgotActivity.this, "" + getResources().getString(R.string.enter_email_id), Toasty.LENGTH_SHORT).show();
                    return;
                }
                ForgotPassword();
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
            etEmail = findViewById(R.id.etEmail);
            txtSend = findViewById(R.id.txtSend);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void ForgotPassword() {
        Utils.ProgressBarShow(ForgotActivity.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.forgotpassword(strEmail);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utils.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Utils.AlertDialog(ForgotActivity.this, response.body().getMessage(), true, true);
                    } else {
                        Utils.AlertDialog(ForgotActivity.this, response.body().getMessage(), false, false);
                    }
                } catch (Exception e) {
                    Log.e("forgotpassword", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Utils.ProgressbarHide();
                Log.e("forgotpassword", "onFailure => " + t.getMessage());
                Utils.AlertDialog(ForgotActivity.this, t.getMessage(), false, false);
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
