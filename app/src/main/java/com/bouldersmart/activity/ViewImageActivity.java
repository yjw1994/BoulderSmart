package com.bouldersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bouldersmart.BoulderSmartApplication;
import com.bouldersmart.R;

public class ViewImageActivity extends BaseActivity {

    Activity activity;
    ImageView ivBetaView;
    TextView tvTitleToolbar;
    BoulderSmartApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_view_beta);
        activity = ViewImageActivity.this;
        application = (BoulderSmartApplication) getApplicationContext();
        init();
    }

    private void init() {
        initToolbar();

        ivBetaView = findViewById(R.id.ivBetaView);

        GetIntentData();
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.beta_image));
        ivAddLocation.setVisibility(View.INVISIBLE);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("from")) {
            String strIdentify = intent.getStringExtra("from");
            if (strIdentify != null && strIdentify.equals("beta")) {
                tvTitleToolbar.setText(activity.getResources().getString(R.string.beta_image));
            }else {
                tvTitleToolbar.setText(activity.getResources().getString(R.string.approach_directions));
            }
        }

        if (intent.hasExtra("image")) {
            String beta_image = intent.getStringExtra("image");
            if (beta_image != null) {
                try {
                    application.getmImageLoader().displayImage(beta_image, ivBetaView,
                            application.getDisplayImageOptionForBackground(
                                    activity.getResources().getDrawable(R.drawable.ic_gallery_32dp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
