package com.bouldersmart.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.CommonResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    Button btnSubmit;
    EditText etEmailForgot;
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_forgot_password);
        activity = ForgotPasswordActivity.this;
        init();
    }

    private void init() {
        initToolbar();
        etEmailForgot = findViewById(R.id.etEmailForgot);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);
        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.forgot_password));
        ivAddLocation.setVisibility(View.INVISIBLE);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
            if (Utills.isEmpty(etEmailForgot.getText().toString().trim())) {
                Utills.showAlert(activity, activity.getResources().getString(R.string.app_name),
                        activity.getResources().getString(R.string.error_email));
            } else {
                if (Utills.isNetworkAvailable(activity, true,
                        false)) {
                    forgotPassword();
                }
            }
        }
    }

    private void forgotPassword() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.forgotPassword(etEmailForgot.getText().toString().trim());
        call.enqueue(new Callback<CommonResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponseModel> call,
                                   @NonNull Response<CommonResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    CommonResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());

                                onBackPressed();
                            } else {
                                Utills.showAlert(activity, getString(R.string.app_name),
                                        model.getResponseMsg());
                            }
                        }
                    }
                } else {
                    Utills.showAlert(activity, getString(R.string.app_name), response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for forgot password.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
