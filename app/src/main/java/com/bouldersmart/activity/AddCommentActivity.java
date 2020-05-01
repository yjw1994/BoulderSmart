package com.bouldersmart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.adapter.CommentAdapter;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.AddCommentResponseModel;
import com.bouldersmart.model.CommentResponseModel;
import com.bouldersmart.model.CommnetDataModel;
import com.bouldersmart.model.CommonResponseModel;
import com.bouldersmart.model.LocationDataModel;
import com.bouldersmart.model.RouteDataModel;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class AddCommentActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    RelativeLayout rlSendComment;
    EditText etComment;
    RecyclerView rvComments;
    TextView tvCommentCount, tvTitleToolbar,tvUserGrades;
    ImageView ivAddLocation;
    ScaleRatingBar srbRouteComment;
    Button btnClimbingBeta;
    ArrayList<CommnetDataModel> list = new ArrayList<>();
    CommentAdapter adapter;
    String strUserId = "", strRouteId = "";
    RouteDataModel routeModel;
    RelativeLayout rlCommentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_add_comment);
        activity = AddCommentActivity.this;

        init();
    }

    private void init() {
        initToolbar();

        rlSendComment = findViewById(R.id.rlSendComment);
        etComment = findViewById(R.id.etComment);
        rvComments = findViewById(R.id.rvComments);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvUserGrades = findViewById(R.id.tvUserGrades);
        srbRouteComment = findViewById(R.id.srbRouteComment);
        btnClimbingBeta = findViewById(R.id.btnClimbingBeta);
        rlCommentView = findViewById(R.id.rlCommentView);

        GetIntentData();

        Utills.setRecyclerView(activity, rvComments);

        if (Utills.isNetworkAvailable(activity, true,
                false)) {
            getComments();
        }

        srbRouteComment.setOnRatingChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser)
                if (Utills.isNetworkAvailable(activity, true,
                        false)) {
                    applyRating(rating);
                }
        });
        // Here, click event set for UI interaction
        rlSendComment.setOnClickListener(this);
        btnClimbingBeta.setOnClickListener(this);
    }

    private void getComments() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommentResponseModel> call = service.getComments(strRouteId,
                Preferences.getStringName(Preferences.USER_ID));

        call.enqueue(new Callback<CommentResponseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<CommentResponseModel> call,
                                   @NonNull Response<CommentResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    CommentResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                list = model.getData();
                                if (model.getGrade_opinion() != null)
                                    tvUserGrades.setText(model.getGrade_opinion());
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getComments() != null) {
                                        if (list.get(i).getComments().length() == 0) {
                                            list.remove(i);
                                        }
                                    }
                                }
                                if (list != null) {
                                    checkComments(model.getUser_ratting());

                                    adapter = new CommentAdapter(activity, list);
                                    rvComments.setAdapter(adapter);

                                    tvCommentCount.setText("(" + list.size() + ")");
                                }
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
            public void onFailure(@NonNull Call<CommentResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for get comments.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void checkComments(CommnetDataModel dataModel) {

        try {
            if (dataModel != null) {
//                rlCommentView.setVisibility(View.GONE);
                if (dataModel.getRatting() != null) {
                    if (dataModel.getRatting().length() != 0) {
                        if (!dataModel.getRatting().equals("0"))
                            srbRouteComment.setRating(Float.parseFloat(dataModel.getRatting()));
                    }
                }
//                srbRouteComment.setIsIndicator(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ivAddLocation = findViewById(R.id.ivAddLocation);
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        ivAddLocation.setVisibility(View.INVISIBLE);
        ivAddLocation.setImageResource(R.drawable.ic_delete_32dp);
        ivBack.setOnClickListener(v -> onBackPressed());
        ivAddLocation.setOnClickListener(v -> Utills.showCustomDialog(activity,
                activity.getResources().getString(R.string.delete),
                activity.getResources().getString(R.string.delete_route_message),
                activity.getResources().getString(R.string.yes), (dialog, which) -> {
                    if (Utills.isNetworkAvailable(activity, true,
                            false)) {
                        deleteRoute();
                    }
                }, activity.getResources().getString(R.string.no), null,
                false, true));
    }

    @SuppressLint("SetTextI18n")
    private void GetIntentData() {
        Intent intent = getIntent();
        String strRouteName = "";
        if (intent.hasExtra("routeModel")) {
            routeModel = (RouteDataModel) intent.getSerializableExtra("routeModel");
            if (routeModel != null) {
                strRouteName = routeModel.getRoute_name();
                strRouteId = routeModel.getRoute_id();
            }
        }

        if (intent.hasExtra("model")) {
            LocationDataModel model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                strUserId = model.getUser_id();

                tvTitleToolbar.setText(strRouteName + " " + activity.getResources().getString(R.string
                        .by) + " " + model.getFname() + " " + model.getLname());

                if (Preferences.getStringName(Preferences.USER_ID).equals(strUserId)) {
                    ivAddLocation.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlSendComment:
                if (isValidComment()) {
                    if (Utills.isNetworkAvailable(activity, true,
                            false)) {
                        addComments();
                    }
                }
                break;
            case R.id.btnClimbingBeta:
                if (routeModel != null) {
                    if (routeModel.getPhoto() != null && routeModel.getPhoto().length() != 0) {
                        startActivity(new Intent(activity, ViewImageActivity.class)
                                .putExtra("from", "beta")
                                .putExtra("image", routeModel.getPhoto()));
                    } else {
                        Utills.showAlert(activity, activity.getResources().getString(R.string.app_name),
                                activity.getResources().getString(R.string.beta_image_not_available)
                                        + " " + routeModel.getRoute_name() + " " +
                                        activity.getResources().getString(R.string.route));
                    }
                }
                break;
        }
    }

    private void addComments() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<AddCommentResponseModel> call = service.giveRouteRatting(Preferences.getStringName
                        (Preferences.USER_ID), strRouteId, String.valueOf(srbRouteComment.getRating()),
                etComment.getText().toString().trim());
        call.enqueue(new Callback<AddCommentResponseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AddCommentResponseModel> call,
                                   @NonNull Response<AddCommentResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    AddCommentResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());
                                CommnetDataModel dataModel = model.getData();
                                if (dataModel != null) {
                                    list.add(0, dataModel);
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                tvCommentCount.setText("(" + list.size() + ")");
                                etComment.setText("");
                                Utills.hideKeyBoard(etComment, activity);
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
            public void onFailure(@NonNull Call<AddCommentResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for give comment.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void applyRating(float rating) {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.giveRouteRattingOnly(Preferences.getStringName
                (Preferences.USER_ID), strRouteId, String.valueOf(rating));
        call.enqueue(new Callback<CommonResponseModel>() {
            @SuppressLint("SetTextI18n")
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
                Logger.e("API Error", "Unable to submit for give ratting.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void deleteRoute() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.deleteRoute(Preferences.getStringName
                (Preferences.USER_ID), strRouteId);
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

                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();

                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
                Logger.e("API Error", "Unable to submit for delete route.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private boolean isValidComment() {
        if (Utills.isEmpty(etComment.getText().toString().trim())) {
            Utills.showAlert(activity, activity.getResources().getString(R.string.app_name),
                    activity.getResources().getString(R.string.error_comment));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
