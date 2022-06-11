package com.divinetechs.ebooksapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.Functions;
import com.divinetechs.ebooksapp.Utility.PermissionUtils;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private CardView card_view_allow_permission;
    PermissionUtils takePermissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.HideNavigation(PermissionActivity.this);
        Utils.screenCapOff(PermissionActivity.this);
        if (PrefManager.getInstance(this).isNightModeEnabled() == true) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_permission);
        PrefManager.forceRTLIfSupported(getWindow(), PermissionActivity.this);

        this.card_view_allow_permission = findViewById(R.id.card_view_allow_permission);
        this.card_view_allow_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        takePermissionUtils = new PermissionUtils(PermissionActivity.this, mPermissionResult);
        if (takePermissionUtils.isStoragePermissionGranted()) {
            finish();
        } else {
            takePermissionUtils.showStoragePermissionDailog(getString(R.string.we_need_storage_permission_for_save_video));
        }
    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(PermissionActivity.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(PermissionActivity.this, getString(R.string.we_need_storage_permission_for_save_video));
                    } else if (allPermissionClear) {
                        finish();
                    }
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPermissionResult.unregister();
    }

}
