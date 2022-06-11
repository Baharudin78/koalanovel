package com.divinetechs.ebooksapp.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.divinetechs.ebooksapp.Adapter.PackageAdapter;
import com.divinetechs.ebooksapp.Interface.ItemClickListener;
import com.divinetechs.ebooksapp.Model.PackageModel.PackageModel;
import com.divinetechs.ebooksapp.Model.PackageModel.Result;
import com.divinetechs.ebooksapp.R;
import com.divinetechs.ebooksapp.Utility.PrefManager;
import com.divinetechs.ebooksapp.Utility.Utils;
import com.divinetechs.ebooksapp.Webservice.AppAPI;
import com.divinetechs.ebooksapp.Webservice.BaseURL;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Package extends BottomSheetDialogFragment implements View.OnClickListener, ItemClickListener {

    PrefManager prefManager;

    RecyclerView rvPackage;
    List<Result> packageList;
    PackageAdapter packageAdapter;

    EditText etAmount;
    LinearLayout lyAddMoney;

    private BottomSheetBehavior mBehavior;

    public static Package newInstance() {
        return new Package();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.package2, null);

        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());
        prefManager = new PrefManager(getActivity());
        rvPackage = view.findViewById(R.id.rvPackage);

        lyAddMoney = view.findViewById(R.id.lyAddMoney);
        etAmount = view.findViewById(R.id.etAmount);

        GetPackage();

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    lyAddMoney.setVisibility(View.VISIBLE);
                } else {
                    lyAddMoney.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lyAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllPaymentActivity.class);
                intent.putExtra("TYPE", "Package");
                intent.putExtra("itemId", "");
                intent.putExtra("price", "" + etAmount.getText().toString().trim());
                intent.putExtra("desc", "Default amount");
                intent.putExtra("date", "");
                startActivity(intent);
                dismiss();
            }
        });

        return dialog;
    }

    private void GetPackage() {
        Utils.ProgressBarShow(getActivity());

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<PackageModel> call = bookNPlayAPI.get_package();
        call.enqueue(new Callback<PackageModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<PackageModel> call, Response<PackageModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            packageList = new ArrayList<>();
                            packageList = response.body().getResult();
                            Log.e("==>packageList", "" + packageList.size());

                            packageAdapter = new PackageAdapter(getActivity(), packageList, "fragment", Package.this, prefManager.getValue("currency_symbol"));
                            rvPackage.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            rvPackage.setAdapter(packageAdapter);
                            packageAdapter.notifyDataSetChanged();
                        } else {
                            rvPackage.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e) {
                    Log.e("get_package Exception =>", "" + e);
                }
                Utils.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<PackageModel> call, Throwable t) {
                Utils.ProgressbarHide();
            }
        });
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(int position) {
        Log.e("==>payckage_id", "" + packageList.get(position).getId());
        Log.e("==>amount", "" + packageList.get(position).getPrice());
        Log.e("==>short_description", "" + packageList.get(position).getPackageName());
        Log.e("==>currency_code", "" + prefManager.getValue("currency_code"));

        Intent intent = new Intent(getActivity(), AllPaymentActivity.class);
        intent.putExtra("TYPE", "Package");
        intent.putExtra("itemId", "" + packageList.get(position).getId());
        intent.putExtra("price", "" + packageList.get(position).getPrice());
        intent.putExtra("desc", "" + packageList.get(position).getPackageName());
        intent.putExtra("date", "" + packageList.get(position).getCreatedAt());
        startActivity(intent);
        dismiss();
    }

}
