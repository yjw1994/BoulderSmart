package com.bouldersmart.activity;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.bouldersmart.adapter.RoutesAdapter;
import com.bouldersmart.adapter.VoucherAdapter;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.LoginDataModel;
import com.bouldersmart.model.PurchaseVoucherResponseModel;
import com.bouldersmart.model.VoucherDataModel;
import com.bouldersmart.model.VoucherResponseModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class VouchersActivity extends BaseActivity implements View.OnClickListener,
        VoucherAdapter.VoucherInterface {

    Activity activity;
    RecyclerView rvVouchers;
    LinearLayout llCommonError;
    TextView tvErrorMessage;
    Button btnRetry;
    ArrayList<VoucherDataModel> list = new ArrayList<>();
    VoucherAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_vouchers);
        activity = VouchersActivity.this;
        init();
    }

    private void init() {
        customToolbar();
        initErrorLayout();
        rvVouchers = findViewById(R.id.rvVouchers);
        Utills.setRecyclerView(activity, rvVouchers);

        SendToServer();
    }

    private void customToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);
        ivAddLocation.setVisibility(View.INVISIBLE);
        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.vouchers));

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void SendToServer() {
        if (Utills.isNetworkAvailable(activity, true,
                false)) {
            SendToServerVouchers();
        }
    }

    private void SendToServerVouchers() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<VoucherResponseModel> call = service.getVouchers(Preferences
                .getStringName(Preferences.USER_ID));

        call.enqueue(new Callback<VoucherResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<VoucherResponseModel> call,
                                   @NonNull Response<VoucherResponseModel> response) {
                hideProgressDialog();
                list.clear();
                if (response.isSuccessful()) {
                    VoucherResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                list = model.getData();
                                adapter = new VoucherAdapter(activity, list
                                        , VouchersActivity.this);
                                rvVouchers.setAdapter(adapter);

                                if (list.size() > 0) {
                                    showView();
                                } else {
                                    hideView(activity.getResources().getString(R.string
                                            .no_voucher_found), false);
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
            public void onFailure(@NonNull Call<VoucherResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for vouchers.");
                hideView(t.getMessage(), true);
                t.printStackTrace();
            }
        });
    }

    private void initErrorLayout() {
        llCommonError = findViewById(R.id.llCommonError);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(this);
    }

    private void showView() {
        rvVouchers.setVisibility(View.VISIBLE);
        llCommonError.setVisibility(View.GONE);
    }

    private void hideView(String message, boolean fromNetwork) {
        rvVouchers.setVisibility(View.GONE);
        llCommonError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
        if (fromNetwork) {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRetry:
                SendToServer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void voucherPurchased(String identify, int position) {
        VoucherDataModel model = list.get(position);
        switch (identify) {
            case "purchase":
                Utills.showCustomDialog(activity, activity.getResources().getString(R.string
                                .purchase_voucher), activity.getResources()
                                .getString(R.string.purchase_message), activity.getResources()
                                .getString(R.string.yes), (dialog, which) -> {
                            if (Utills.isNetworkAvailable(activity, true,
                                    false)) {
                                SendToServerPurchase(model);
                            }
                        }, activity.getResources().getString(R.string.no), null,
                        false, true);
                break;
        }
    }

    private void SendToServerPurchase(VoucherDataModel model) {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<PurchaseVoucherResponseModel> call = service.purchaseVouchers(Preferences
                .getStringName(Preferences.USER_ID), model.getVoucher_id(), model.getPoints());

        call.enqueue(new Callback<PurchaseVoucherResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<PurchaseVoucherResponseModel> call,
                                   @NonNull Response<PurchaseVoucherResponseModel> response) {
                hideProgressDialog();
                list.clear();
                if (response.isSuccessful()) {
                    PurchaseVoucherResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                LoginDataModel dataModel = Preferences.GetLoginObject();
                                if (dataModel != null) {
                                    String rewordPoint = model.getReward_point();
                                    if (rewordPoint != null && rewordPoint.length() != 0) {
                                        dataModel.setReward_point(rewordPoint);
                                        Preferences.SetLoginDetails(dataModel);
                                    }
                                }
                                ArrayList<VoucherDataModel> listVoucher = model.getData();
                                list.clear();
                                list.addAll(listVoucher);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                if (list.size() > 0) {
                                    showView();
                                } else {
                                    hideView(activity.getResources().getString(R.string
                                            .no_voucher_found), false);
                                }
                            } else {
                                Utills.showAlert(activity,activity.getResources()
                                        .getString(R.string.app_name),model.getResponseMsg());
                            }
                        }
                    }
                } else {
                    Utills.showAlert(activity,activity.getResources().getString(R.string.app_name),response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PurchaseVoucherResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for purchase voucher.");
                Utills.showAlert(activity,activity.getResources().getString(R.string.app_name),t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
