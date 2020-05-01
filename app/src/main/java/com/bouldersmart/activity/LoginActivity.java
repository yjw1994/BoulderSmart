package com.bouldersmart.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.CommonResponseModel;
import com.bouldersmart.model.LoginDataModel;
import com.bouldersmart.model.LoginResponseModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    TextView tvSignUp, tvForgot;
    Button btnLogin;
    EditText etEmailLogin, etPasswordLogin;
    private CallbackManager callbackManager;
    RelativeLayout rlFacebook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_login);
        activity = LoginActivity.this;

        init();
    }

    private void init() {
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgot = findViewById(R.id.tvForgot);
        btnLogin = findViewById(R.id.btnLogin);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        rlFacebook = findViewById(R.id.rlFacebook);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            Logger.e("--boulder-token--", "login-class-->" + deviceToken);
            Preferences.SetStringName(Preferences.DEVICE_TOKEN, deviceToken);
            // Do whatever you want with your token now
            // i.e. store it on SharedPreferences or DB
            // or directly send it to server
        });
        // for facebook login
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                getFacebookUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.printStackTrace();
            }
        });

        // Here, click event set for UI interaction
        tvSignUp.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        rlFacebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignUp:
                startActivity(new Intent(activity, SignUpActivity.class));
                break;
            case R.id.btnLogin:
                if (isValid()) {
                    if (Utills.isNetworkAvailable(activity, true, false)) {
                        loginUser();
                    }
                }
                break;
            case R.id.tvForgot:
                startActivity(new Intent(activity, ForgotPasswordActivity.class));
                break;
            case R.id.rlFacebook:
                facebookClick();
                break;
        }
    }

    private void facebookClick() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
    }

    private void getFacebookUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (json_object, response) -> {
                    Logger.e("facebook json object ----> ", json_object + "");
                    String strEmail = "";
                    String facebook_photo_url = "", strFName = "", strLName = "";

                    if (json_object != null) {

                        String strFbId = json_object.optString("id");
                        String name = json_object.optString("name");
                        strEmail = json_object.optString("email");

                        if (json_object.optJSONObject("picture") != null) {
                            if (json_object.optJSONObject("picture").optJSONObject("data") != null) {
                                facebook_photo_url = json_object.optJSONObject("picture").optJSONObject("data")
                                        .optString("url");
                            }
                        }

                        String[] userName = name.split(" ");
                        for (int i = 0; i < userName.length; i++) {
                            if (i == 0) {
                                strFName = userName[i];
                            } else if (i == 1) {
                                strLName = userName[i];
                            }
                        }

                        if (Utills.isNetworkAvailable(activity, true,
                                false)) {
                            isRegister(strFbId, strEmail, strFName, strLName);
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(300).height(300)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void isRegister(String strFbId, String strEmail, String strFName, String strLName) {
        ShowProgressDialog(activity, getString(R.string.loading));

        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<LoginResponseModel> call = service.isRegister(Constants.IS_FB, strFbId, strEmail,
                Constants.DEVICE_TYPE, Preferences.getStringName(Preferences.DEVICE_TOKEN));
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

                            LoginDataModel dataModel = model.getData();
                            if (dataModel != null) {
                                Preferences.SetStringName(Preferences.USER_ID, dataModel.getUser_id());
                                Preferences.SetStringName(Preferences.TOKEN_LOGIN, dataModel.getToken());
                                Preferences.SetLoginDetails(dataModel);

                                startActivity(new Intent(activity, HomeActivity.class));
                                activity.finish();
                            }
                        } else if (model.getResponseCode().equals("2")) {
                            ResendDialog(model.getResponseMsg(), strFbId, strEmail);
                        } else if (model.getResponseCode().equals("3")) {
                            Intent intent = new Intent(activity, SignUpActivity.class);
                            intent.putExtra("isFrom", "socialLogin");
                            intent.putExtra("f_name", strFName);
                            intent.putExtra("l_name", strLName);
                            intent.putExtra("email", strEmail);
                            intent.putExtra("fbId", strFbId);
                            startActivity(intent);
                        } else {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.showAlert(activity, getString(R.string.app_name), model.getResponseMsg());
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
                Logger.e("API Error", "Unable to submit for is register.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void ResendDialog(String responseMsg, String strFbId, String strEmail) {
        Utills.showCustomDialog(activity, activity.getResources().getString(R
                        .string.app_name), responseMsg,
                activity.getResources().getString(R.string.re_send),
                (dialog, which) -> {
                    if (Utills.isNetworkAvailable(activity, true,
                            false)) {
                        reSendConfirm(strEmail, strFbId);
                    }
                }, activity.getResources().getString(R.string.no), null,
                false, true);
    }

    private void loginUser() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<LoginResponseModel> call = service.login(etEmailLogin.getText().toString().trim(),
                Constants.getMD5(etPasswordLogin.getText().toString().trim()), Constants.DEVICE_TYPE,
                Preferences.getStringName(Preferences.DEVICE_TOKEN));
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
                            LoginDataModel dataModel = model.getData();
                            if (dataModel != null) {
                                Preferences.SetStringName(Preferences.USER_ID, dataModel.getUser_id());
                                Preferences.SetStringName(Preferences.TOKEN_LOGIN, dataModel.getToken());
                                Preferences.SetLoginDetails(dataModel);

                                startActivity(new Intent(activity, HomeActivity.class));
                                finish();
                            }
                        } else if (model.getResponseCode().equals("2")) {
                            ResendDialog(model.getResponseMsg(), "", etEmailLogin.getText().toString().trim());
                        } else {
                            if (!model.getResponseMsg().isEmpty()) {
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
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for login.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void reSendConfirm(String email, String strFbId) {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.resendConfirm(email, strFbId);
        call.enqueue(new Callback<CommonResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponseModel> call,
                                   @NonNull Response<CommonResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    CommonResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            Utills.showAlert(activity, getString(R.string.app_name),
                                    model.getResponseMsg());
                        }
                    }
                } else {
                    Utills.showAlert(activity, getString(R.string.app_name), response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for re-send confirm.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isValid() {
        if (Utills.isEmpty(etEmailLogin.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_email));
            return false;
        } else if (Utills.isEmpty(etPasswordLogin.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_password));
            return false;
        } else if (etPasswordLogin.getText().toString().trim().length() < 6) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_password_length));
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utills.RequestPermission(activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
