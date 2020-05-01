package com.bouldersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.LoginResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    TextView tvLoginReg;
    Button btnSignUp;
    EditText etFNameReg, etLNameReg, etEmailReg, etPasswordReg;
    String strIsManual = Constants.IS_MANUAL;
    String strIsFB = Constants.IS_NOT_FB;
    String strFbId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_sign_up);
        activity = SignUpActivity.this;

        init();
    }

    private void init() {
        tvLoginReg = findViewById(R.id.tvLoginReg);
        btnSignUp = findViewById(R.id.btnSignUp);
        etFNameReg = findViewById(R.id.etFNameReg);
        etLNameReg = findViewById(R.id.etLNameReg);
        etEmailReg = findViewById(R.id.etEmailReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        GetIntentData();
        // Here, click event set for UI interaction
        tvLoginReg.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("isFrom")) {
            if (intent.hasExtra("f_name")) {
                etFNameReg.setText(intent.getStringExtra("f_name"));
            }
            if (intent.hasExtra("f_name")) {
                etLNameReg.setText(intent.getStringExtra("l_name"));
            }
            if (intent.hasExtra("fbId")) {
                strFbId = intent.getStringExtra("fbId");
            }
            strIsFB = Constants.IS_FB;

            if (intent.hasExtra("email")) {
                String strEmail = intent.getStringExtra("email");
                if (strEmail != null && strEmail.length() != 0) {
                    etEmailReg.setText(strEmail);
                    etEmailReg.setEnabled(false);
                    strIsManual = Constants.EMAIL_FOUND;
                } else {
                    strIsManual = Constants.EMAIL_NOT_FOUND;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLoginReg:
                onBackPressed();
                break;
            case R.id.btnSignUp:
                if (isValid()) {
                    if (Utills.isNetworkAvailable(activity, true, false)) {
                        registerUser();
                    }
                }
                break;
        }
    }

    private void registerUser() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<LoginResponseModel> call = service.signUp(etFNameReg.getText().toString().trim(),
                etLNameReg.getText().toString().trim(), etEmailReg.getText().toString().trim(),
                Constants.getMD5(etPasswordReg.getText().toString().trim()), strIsManual, strIsFB,
                strFbId, Preferences.getStringName(Preferences.DEVICE_TOKEN), Constants.DEVICE_TYPE);
        call.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call,
                                   @NonNull Response<LoginResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    LoginResponseModel model = response.body();
                    if (model != null) {
                        if (model.getResponseCode().equals("1")) {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());
                            }
                            onBackPressed();
                        } else {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.showAlertWithFinis(activity, getString(R.string.app_name), model.getResponseMsg(),
                                        true);
                            }
                        }
                    }
                } else {
                    Utills.showAlert(activity, getString(R.string.app_name), response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for register.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private boolean isValid() {
        if (Utills.isEmpty(etFNameReg.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_fname));
            return false;
        } else if (Utills.isEmpty(etLNameReg.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_lname));
            return false;
        } else if (Utills.isEmpty(etEmailReg.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_email));
            return false;
        } else if (Utills.isEmpty(etPasswordReg.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_password));
            return false;
        } else if (etPasswordReg.getText().toString().trim().length() < 6) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_password_length));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
