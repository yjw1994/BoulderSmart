package com.bouldersmart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.bouldersmart.adapter.RoutesAdapter;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.RecyclerItemClickListener;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.LocationDataModel;
import com.bouldersmart.model.RouteDataModel;
import com.bouldersmart.model.RouteResponseModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class ClimbRoutesActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;

    LinearLayout llCommonError;
    TextView tvErrorMessage;
    Button btnRetry;
    RecyclerView rvRoutes;
    TextView tvTitleToolbar;
    ArrayList<RouteDataModel> list = new ArrayList<>();
    RoutesAdapter adapter;
    LocationDataModel model;
    ImageView ivAddLocation;
    String strUserId = "", strLocationId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_climb_routes);
        activity = ClimbRoutesActivity.this;

        init();
    }

    private void init() {
        initToolbar();
        initErrorLayout();
        GetIntentData();
        rvRoutes = findViewById(R.id.rvRoutes);

        Utills.setRecyclerView(activity, rvRoutes);

        GetRoutes();

        rvRoutes.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, (view, position) -> {
                    // TODO Handle item click
                    RouteDataModel routeModel = list.get(position);
                    startActivityForResult(new Intent(activity, AddCommentActivity.class)
                            .putExtra("model", model)
                            .putExtra("routeModel", routeModel), Constants.ADD_COMMENT_ACTIVITY);
                }));
    }

    private void GetRoutes() {
        if (Utills.isNetworkAvailable(activity, true,
                false)) {
            SendToServerRoutes();
        }
    }

    private void SendToServerRoutes() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<RouteResponseModel> call = service.getRoutes(strUserId, strLocationId);

        call.enqueue(new Callback<RouteResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<RouteResponseModel> call,
                                   @NonNull Response<RouteResponseModel> response) {
                hideProgressDialog();
                list.clear();
                if (response.isSuccessful()) {
                    RouteResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                list = model.getData();

                                if (list != null) {
                                    adapter = new RoutesAdapter(activity, list);
                                    rvRoutes.setAdapter(adapter);
                                }
                                if (list != null && list.size() > 0) {
                                    showView();
                                } else {
                                    hideView(activity.getResources().getString(R.string
                                            .currently_no_routes_available), false);
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
            public void onFailure(@NonNull Call<RouteResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for get routes.");
                hideView(t.getMessage(), true);
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                strUserId = model.getUser_id();
                strLocationId = model.getLocation_id();
                tvTitleToolbar.setText(model.getLocation_name() + " " +
                        activity.getResources().getString(R.string.routes));
                if (Preferences.getStringName(Preferences.USER_ID).equals(strUserId)) {
                    ivAddLocation.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initErrorLayout() {
        llCommonError = findViewById(R.id.llCommonError);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(this);
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
        ivBack.setOnClickListener(v -> onBackPressed());
        ivAddLocation.setOnClickListener(v ->
                startActivityForResult(new Intent(activity, AddClimbRouteActivity.class)
                        .putExtra("model", model), Constants.ADD_ROUTE_ACTIVITY));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                GetRoutes();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_ROUTE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                GetRoutes();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == Constants.ADD_COMMENT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                GetRoutes();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                GetRoutes();
            }
        }
    }

    private void showView() {
        rvRoutes.setVisibility(View.VISIBLE);
        llCommonError.setVisibility(View.GONE);
    }

    private void hideView(String message, boolean fromNetwork) {
        rvRoutes.setVisibility(View.GONE);
        llCommonError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
        if (fromNetwork) {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("routes_count", list.size() + "");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
