package com.bouldersmart.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.BoulderSmartApplication;
import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.CommonResponseModel;
import com.bouldersmart.model.LocationDataModel;
import com.bouldersmart.model.LocationResponseModel;
import com.bouldersmart.model.LoginDataModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class HomeActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Activity activity;
    RecyclerView rvDrawer;
    DrawerLayout drawer;
    MapView mapView;
    LinearLayout llDetails;
    ImageView ivImageLocation, ivDeleteLocation;
    TextView tvLocationName, tvRouteCount, tvRewardPoint;

    private Bundle mapViewBundle = null;
    GoogleMap gMap;
    boolean isMove = false;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient fusedLocationClient;

    ArrayList<LocationDataModel> arrayList = new ArrayList<>();
    BoulderSmartApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_home);
        activity = HomeActivity.this;
        application = (BoulderSmartApplication) getApplicationContext();
        if (savedInstanceState != null) {
            String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        customToolbar();

        mapView = findViewById(R.id.mapView);
        rvDrawer = findViewById(R.id.rvDrawer);
        llDetails = findViewById(R.id.llDetails);
        drawer = findViewById(R.id.drawer_layout);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvRouteCount = findViewById(R.id.tvRouteCount);
        ivImageLocation = findViewById(R.id.ivImageLocation);
        ivDeleteLocation = findViewById(R.id.ivDeleteLocation);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        rvDrawer.setLayoutManager(mLayoutManager);
        rvDrawer.setItemAnimator(new DefaultItemAnimator());

        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView tvWelcome = navigationView.findViewById(R.id.tvWelcome);
        TextView tvVouchers = navigationView.findViewById(R.id.tvVouchers);
        Button btnLogout = navigationView.findViewById(R.id.btnLogout);

        LoginDataModel model = Preferences.GetLoginObject();

        if (model != null) {
            tvWelcome.setText(activity.getResources().getString(R.string.welcome) + " " +
                    model.getFname() + " " + model.getLname());
            tvRewardPoint.setText(model.getReward_point());
        }

        buildGoogleApiClient();

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Here, click event set for UI interaction
        tvWelcome.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        tvVouchers.setOnClickListener(this);
    }

    private void SendToServer() {
        if (Utills.isNetworkAvailable(activity, true,
                false)) {
            getLocations();
        }
    }

    private void customToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.VISIBLE);
        tvRewardPoint = findViewById(R.id.tvRewardPoint);
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivBack.setVisibility(View.GONE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);

        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.find_climbing_locations));

        ivMenu.setOnClickListener(v -> {
            if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        ivAddLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvWelcome:
                if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                }
                break;
            case R.id.btnLogout:
                if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                }
                Utills.showCustomDialog(activity, activity.getResources().getString(R
                                .string.logout), activity.getResources().getString(R.string
                                .logout_message), activity.getResources().getString(R.string.yes),
                        (dialog, which) -> {
                            if (Utills.isNetworkAvailable(activity, true,
                                    false)) {
                                logoutAPI();
                            }
                        }, activity.getResources().getString(R.string.no), null,
                        false, true);
                break;
            case R.id.tvVouchers:
                if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                }
                startActivity(new Intent(activity, VouchersActivity.class));
                break;
            case R.id.ivAddLocation:
                startActivityForResult(new Intent(activity, AddLocationActivity.class),
                        Constants.ADD_LOCATION_ACTIVITY);
                break;
        }
    }

    private void getLocations() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<LocationResponseModel> call = service.getLocations("");

        call.enqueue(new Callback<LocationResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LocationResponseModel> call,
                                   @NonNull Response<LocationResponseModel> response) {
                hideProgressDialog();
                arrayList.clear();
                if (response.isSuccessful()) {
                    LocationResponseModel model = response.body();
                    if (model != null) {
                        if (!model.getResponseMsg().isEmpty()) {
                            if (model.getResponseCode().equals("1")) {
                                arrayList = model.getData();
                                createMarkers();
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
            public void onFailure(@NonNull Call<LocationResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for get locations.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void createMarkers() {
        gMap.clear();
        if (arrayList != null)
            for (int i = 0; i < arrayList.size(); i++) {
                LocationDataModel model = arrayList.get(i);
                gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(model.getLat()), Double.parseDouble(model
                                .getLng())))
                        .snippet(model.getLocation_id())
                        .icon(BitmapDescriptorFactory.fromBitmap(Utills.getMarkerBitmapFromView(activity,
                                R.drawable.ic_pin_location_32dp))));
            }
    }

    private void logoutAPI() {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.logOut(Preferences.getStringName
                (Preferences.USER_ID));
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
                                Preferences.SetStringName(Preferences.USER_ID, "");
                                Preferences.SetStringName(Preferences.TOKEN_LOGIN, "");
                                Preferences.SetLoginDetails(null);
                                Utills.ShowToastNormal(activity, model.getResponseMsg());

                                stopLocation();

                                startActivity(new Intent(activity, LoginActivity.class));
                                finish();
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
                Logger.e("API Error", "Unable to submit for logout.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                gMap.setMyLocationEnabled(true);
            }
        } else {
            gMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(latLng -> llDetails.setVisibility(View.GONE));
        googleMap.setOnMarkerClickListener(marker -> {

            LocationDataModel model = getLocationDataFromLocationId(marker.getSnippet());
            if (model != null) {
                llDetails.setVisibility(View.VISIBLE);
                ivDeleteLocation.setVisibility(View.GONE);

                if (Preferences.getStringName(Preferences.USER_ID).equals(model.getUser_id())) {
//                    ivDeleteLocation.setVisibility(View.VISIBLE);

                    ivDeleteLocation.setOnClickListener(v -> Utills.showCustomDialog(activity,
                            activity.getResources().getString(R.string.delete),
                            activity.getResources().getString(R.string.delete_location),
                            activity.getResources().getString(R.string.yes),
                            (dialog, which) -> {
                                if (Utills.isNetworkAvailable(activity, true,
                                        false)) {
                                    llDetails.setVisibility(View.GONE);
                                    deleteLocation(model.getLocation_id());
                                }
                            }, activity.getResources().getString(R.string.no), null,
                            false, true));
                }

                tvLocationName.setText(model.getLocation_name());

                if (model.getListof_routes() != null)
                    tvRouteCount.setText(model.getListof_routes().size() + "");
                try {
                    application.getmImageLoader().displayImage(model.getCover_image(), ivImageLocation,
                            application.getDisplayImageOptionForBackground(activity.getResources()
                                    .getDrawable(R.drawable.ic_gallery_32dp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                llDetails.setOnClickListener(v -> {
                    llDetails.setVisibility(View.GONE);
                    startActivity(new Intent(activity, ClimbDetailsActivity.class)
                            .putExtra("model", model));
                });
            }
            return false;
        });
    }

    private void deleteLocation(String location_id) {
        ShowProgressDialog(activity, getString(R.string.loading));
        GetDataService service = RetrofitClientInstance.UserApiClient(
                Preferences.getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);
        Call<CommonResponseModel> call = service.deleteLocation(Preferences.getStringName
                (Preferences.USER_ID), location_id);
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
                                int position = getPositionFromLocationId(location_id);
                                if (position != -1) {
                                    arrayList.remove(position);
                                    createMarkers();
                                } else {
                                    SendToServer();
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
            public void onFailure(@NonNull Call<CommonResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for delete location.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });


    }

    private int getPositionFromLocationId(String location_id) {
        if (arrayList != null)
            for (int i = 0; i < arrayList.size(); i++) {
                LocationDataModel model = arrayList.get(i);
                if (model.getLocation_id().equals(location_id)) {
                    return i;
                }
            }
        return -1;
    }

    private LocationDataModel getLocationDataFromLocationId(String locationId) {
        if (arrayList != null)
            for (LocationDataModel model : arrayList) {
                if (model.getLocation_id().equals(locationId)) {
                    return model;
                }
            }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_LOCATION_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
//                SendToServer();
/*
                if (data != null) {
                    if (data.hasExtra("model")) {
                        LocationDataModel model = (LocationDataModel) data
                                .getSerializableExtra("model");
                        if (model != null) {
                            arrayList.add(model);
                            createMarkers();
                        }
                    }
                }
*/
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    protected void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
        SendToServer();

        try {
            LoginDataModel dataModel = Preferences.GetLoginObject();
            if (dataModel != null) {
                String strReword = dataModel.getReward_point();
                if (strReword != null && strReword.length() != 0) {
                    tvRewardPoint.setText(strReword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLocation() {
        try {
            if (fusedLocationClient != null)
                fusedLocationClient.removeLocationUpdates(mLocationCallback);
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Logger.e("Location", "Home-> " + location.getLatitude() + " " + location.getLongitude());
                if (!isMove) {
                    isMove = true;
                    CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15);
                    gMap.animateCamera(location1);
                }
            }
        }
    };

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
                fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.e(location.getLatitude() + "<-lat-home", "-lng->" + location.getLongitude());

        if (!isMove) {
            isMove = true;
            CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            gMap.animateCamera(location1);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            super.onBackPressed();
            stopLocation();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }
}
