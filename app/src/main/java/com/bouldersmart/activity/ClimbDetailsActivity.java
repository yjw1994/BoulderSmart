package com.bouldersmart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bouldersmart.BoulderSmartApplication;
import com.bouldersmart.R;
import com.bouldersmart.common.Constants;
import com.bouldersmart.model.LocationDataModel;

public class ClimbDetailsActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    Button btnListRoutes, btnApproachDirections;
    LocationDataModel model;
    ImageView ivLocationImage;
    BoulderSmartApplication application;
    TextView tvTitleToolbar, tvCountRoute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_climb_details);
        activity = ClimbDetailsActivity.this;
        application = (BoulderSmartApplication) getApplicationContext();
        init();
    }

    private void init() {
        initToolbar();
        btnListRoutes = findViewById(R.id.btnListRoutes);
        btnApproachDirections = findViewById(R.id.btnApproachDirections);
        ivLocationImage = findViewById(R.id.ivLocationImage);
        tvCountRoute = findViewById(R.id.tvCountRoute);

        GetIntentData();
        // Here, click event of UI interactions
        btnListRoutes.setOnClickListener(this);
        btnApproachDirections.setOnClickListener(this);
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);
        ivAddLocation.setVisibility(View.INVISIBLE);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                try {
                    application.getmImageLoader().displayImage(model.getCover_image(), ivLocationImage,
                            application.getDisplayImageOptionForBackground(activity.getResources()
                                    .getDrawable(R.drawable.ic_gallery_32dp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tvTitleToolbar.setText(model.getLocation_name());
                tvCountRoute.setText(model.getListof_routes().size() + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnListRoutes:
                if (model != null) {
                    startActivityForResult(new Intent(activity, ClimbRoutesActivity.class)
                                    .putExtra("model", model),
                            Constants.CLIMB_DETAILS_ACTIVITY);
                }
                break;
            case R.id.btnApproachDirections:
                startActivity(new Intent(activity, ApproachDirectionActivity.class)
                        .putExtra("model", model));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CLIMB_DETAILS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                if (data != null) {
                    if (data.hasExtra("routes_count")) {
                        String result = data.getStringExtra("routes_count");
                        tvCountRoute.setText(result);
                    }
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
