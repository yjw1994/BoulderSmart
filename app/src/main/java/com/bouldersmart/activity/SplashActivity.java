package com.bouldersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.bouldersmart.R;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.model.LoginDataModel;

public class SplashActivity extends BaseActivity {

    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_splash);

        activity = SplashActivity.this;

        init();
    }

    private void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginDataModel model = Preferences.GetLoginObject();
                if (model != null) {
                    if (model.getUser_id() != null && model.getUser_id().length() != 0) {
                        startActivity(new Intent(activity, HomeActivity.class));
                        finish();
                    } else {
                        loginRedirect();
                    }
                } else {
                    loginRedirect();
                }
            }
        }, 3000);
    }

    private void loginRedirect() {
        startActivity(new Intent(activity, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
