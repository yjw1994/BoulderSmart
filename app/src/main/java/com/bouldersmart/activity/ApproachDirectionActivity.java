package com.bouldersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.adapter.ApproachAdapter;
import com.bouldersmart.adapter.RoutesAdapter;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.ApproachDataModel;
import com.bouldersmart.model.CommonResponseModel;
import com.bouldersmart.model.ListApproachResponseModel;
import com.bouldersmart.model.LocationDataModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class ApproachDirectionActivity extends BaseActivity implements View.OnClickListener,
        ApproachAdapter.ClickListener {

    Activity activity;
    RecyclerView rvApproach;
    LinearLayout llCommonError;
    TextView tvErrorMessage;
    Button btnRetry;
    ArrayList<ApproachDataModel> list = new ArrayList<>();
    LocationDataModel model;
    ImageView ivAddLocation;
    ApproachAdapter adapter;
    String strUserId = "", strLocationId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_approach_directions);
        activity = ApproachDirectionActivity.this;

        init();
    }

    private void init() {
        initToolbar();
        initErrorLayout();

        rvApproach = findViewById(R.id.rvApproach);
        Utills.setRecyclerView(activity, rvApproach);

        GetIntentData();
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                strUserId = model.getUser_id();
                strLocationId = model.getLocation_id();
                if (Preferences.getStringName(Preferences.USER_ID).equals(model.getUser_id())) {
                    ivAddLocation.setVisibility(View.VISIBLE);
                }
                GetApproaches();
            }
        }
    }

    private void GetApproaches() {
        if (Utills.isNetworkAvailable(activity, true,
                false)) {
            SendToServer();
        }
    }

    private void SendToServer() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<ListApproachResponseModel> call = service.getapproach(strLocationId, strUserId);

        call.enqueue(new Callback<ListApproachResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ListApproachResponseModel> call,
                                   @NonNull Response<ListApproachResponseModel> response) {
                hideProgressDialog();
                list.clear();
                if (response.isSuccessful()) {
                    ListApproachResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                list = model.getData();
                                if (list != null) {
                                    adapter = new ApproachAdapter(activity, list, ApproachDirectionActivity.this);
                                    rvApproach.setAdapter(adapter);
                                }
                                if (list != null && list.size() > 0) {
                                    showView();
                                } else {
                                    hideView(activity.getResources().getString(R.string
                                            .no_approach_available), false);
                                }
                            } else {
                                hideView(model.getResponseMsg(), false);
                            }
                        }
                    }
                } else {
                    hideView(response.message(), true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListApproachResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for get approach.");
                hideView(t.getMessage(), true);
                t.printStackTrace();
            }
        });
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivAddLocation = findViewById(R.id.ivAddLocation);
        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.approach_directions));
        ivAddLocation.setVisibility(View.INVISIBLE);
        ivAddLocation.setImageResource(R.drawable.ic_add_32dp);
        ivBack.setOnClickListener(v -> onBackPressed());
        ivAddLocation.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, AddApproachActivity.class)
                    .putExtra("model", model), Constants.ADD_APPROACH_ACTIVITY);
        });
    }

    private void initErrorLayout() {
        llCommonError = findViewById(R.id.llCommonError);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_APPROACH_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                GetApproaches();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void showView() {
        rvApproach.setVisibility(View.VISIBLE);
        llCommonError.setVisibility(View.GONE);
    }

    private void hideView(String message, boolean fromNetwork) {
        rvApproach.setVisibility(View.GONE);
        llCommonError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
        if (fromNetwork) {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void ButtonClicked(String identify, int position) {
        if (list != null) {
            ApproachDataModel model = list.get(position);
            if (identify.equals("delete")) {
                Utills.showCustomDialog(activity,
                        activity.getResources().getString(R.string.delete),
                        activity.getResources().getString(R.string.delete_approach),
                        activity.getResources().getString(R.string.yes), (dialog, which) -> {
                            if (Utills.isNetworkAvailable(activity, true,
                                    false)) {
                                deleteApproach(model.getApproach_id());
                            }
                        }, activity.getResources().getString(R.string.no), null,
                        false, true);
            } else if (identify.equals("details")) {
                if (model.getImage() != null && model.getImage().length() != 0) {
                    startActivity(new Intent(activity, ViewImageActivity.class)
                            .putExtra("from", "approach")
                            .putExtra("image", model.getImage()));
                } else {
                    Utills.showAlert(activity, activity.getResources().getString(R.string.app_name),
                            activity.getResources().getString(R.string.approach_image_not_available));
                }
            }
        }
    }

    private void deleteApproach(String strApproachId) {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.deleteApproach(Preferences.getStringName
                (Preferences.USER_ID), strApproachId);
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
                                GetApproaches();
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
                Logger.e("API Error", "Unable to submit for delete approach.");
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
