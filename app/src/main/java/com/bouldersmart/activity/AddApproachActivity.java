package com.bouldersmart.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.AddApproachResponseModel;
import com.bouldersmart.model.LocationDataModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class AddApproachActivity extends BaseActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    Activity activity;
    MapView mvApproach;
    TextView tvTimeCounter, tvSave;
    LinearLayout llSave;
    ImageView ivPlay, ivStop;
    private Bundle mapViewBundle = null;
    GoogleMap gMap;
    ArrayList<LatLng> list = new ArrayList<>();

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location currentLocation, startLocation, stopLocation;
    boolean isStart = false, isStopLocation = false, isZoom = false;
    int timeMills = 1000;
    private final Handler handler = new Handler();
    LocationDataModel model;
    String strLocationId = "";
    LatLngBounds.Builder builder;
    LatLngBounds bounds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_add_approach);
        activity = AddApproachActivity.this;
        if (savedInstanceState != null) {
            String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        init();
    }

    private void init() {
        initToolbar();
        mvApproach = findViewById(R.id.mvApproach);
        tvTimeCounter = findViewById(R.id.tvTimeCounter);
        tvSave = findViewById(R.id.tvSave);
        llSave = findViewById(R.id.llSave);

        ivPlay = findViewById(R.id.ivPlay);
        ivStop = findViewById(R.id.ivStop);

        GetIntentData();

        buildGoogleApiClient();
        mvApproach.onCreate(mapViewBundle);
        mvApproach.getMapAsync(this);

        tvSave.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivStop.setOnClickListener(this);
    }

    private void initToolbar() {
        LinearLayout llChipView = findViewById(R.id.llChipView);
        llChipView.setVisibility(View.GONE);

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
        ImageView ivAddLocation = findViewById(R.id.ivAddLocation);
        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.upload_approach_directions));
        ivAddLocation.setVisibility(View.INVISIBLE);
        ivAddLocation.setImageResource(R.drawable.ic_add_32dp);
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            Logger.e("exist", "-->");
            model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                strLocationId = model.getLocation_id();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //Initialize Google Play Services
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
    }

    private void stopLocation() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mvApproach != null) {
            mvApproach.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mvApproach != null) {
            mvApproach.onStop();
        }
    }

    @Override
    protected void onPause() {
        if (mvApproach != null) {
            mvApproach.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mvApproach != null) {
            mvApproach.onDestroy();
        }
        super.onDestroy();
        stopLocation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mvApproach != null) {
            mvApproach.onLowMemory();
        }
    }

    @Override
    protected void onResume() {
        if (mvApproach != null) {
            mvApproach.onResume();
        }
        super.onResume();
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
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        if (isStart) {
            startLocation = location;
        } else if (isStopLocation) {
            stopLocation = location;
        }
        currentLocation = location;

        list.add(new LatLng(location.getLatitude(), location.getLongitude()));

        if (startLocation != null && stopLocation == null) {
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            Polyline line = gMap.addPolyline(options);
            line.setGeodesic(true);
        }

        if (startLocation != null && stopLocation != null && isStopLocation) {
            isStopLocation = false;
            builder = new LatLngBounds.Builder();
            gMap.clear();
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

            for (int i = 0; i < list.size(); i++) {
                LatLng point = list.get(i);
                if (i == 0 || i == (list.size() - 1)) {
                    builder.include(point);
                    addStartOrStopMarker(point);
                }
                options.add(point);
            }

            Polyline line = gMap.addPolyline(options);
            line.setGeodesic(true);
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            gMap.moveCamera(cu);
        }

        if (!isZoom) {
            isZoom = true;
            CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            gMap.animateCamera(location1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSave:
                try {
                    captureMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ivPlay:
                ivStop.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.GONE);

                startRecordings();
                break;
            case R.id.ivStop:
                ivStop.setVisibility(View.GONE);
                llSave.setVisibility(View.VISIBLE);

                finishRecordingIfNeeded();
                break;
        }
    }

    private void successAdded() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        stopLocation();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void captureMap() {
        GoogleMap.SnapshotReadyCallback callback = snapshot -> {
            // TODO Auto-generated method stub
            try {
                String filePath = Utills.getScreenshotFile(activity).getAbsolutePath();
                FileOutputStream out = new FileOutputStream(filePath);
                // Write the string to the file
                snapshot.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Logger.e("image", "map_screen_shot->" + filePath);
                try {
                    File file = new File(filePath);
                    addNewRoute(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Logger.e("ImageCapture", e.getMessage());
                e.printStackTrace();
            }
        };
        if (gMap != null)
            gMap.snapshot(callback);
    }

    private void startRecordings() {
        isStart = true;
        isStopLocation = false;
        addStartOrStopMarker();
        doTheAutoRefresh();
    }

    private void finishRecordingIfNeeded() {
        if (isStart) {
            try {
                isStart = false;
                isStopLocation = true;
                addStartOrStopMarker();
                handler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addStartOrStopMarker() {
        gMap.addMarker(new MarkerOptions()
                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
//                .title("Current Position")
                .icon(BitmapDescriptorFactory.fromBitmap(Utills.getMarkerBitmapFromView(activity,
                        R.drawable.ic_approach_pin_32dp))));
    }

    private void addStartOrStopMarker(LatLng latLng) {
        gMap.addMarker(new MarkerOptions()
                .position(latLng)
//                .title("Current Position")
                .icon(BitmapDescriptorFactory.fromBitmap(Utills.getMarkerBitmapFromView(activity,
                        R.drawable.ic_approach_pin_32dp))));
    }

    private void doTheAutoRefresh() {
        handler.postDelayed(() -> {
            timeMills++;
            String time = Utills.convertMillsToHMmSs(timeMills);
            if (time.startsWith("00:")) {
                time = time.replaceFirst("00:", "");
            }
            tvTimeCounter.setText(time);
            // Write code for your refresh logic
            doTheAutoRefresh();
        }, 1000);
    }

    private void addNewRoute(File file) {
        ShowProgressDialog(activity, getString(R.string.loading));
        String strCurrentDate = Utills.CurrentDate(Constants.INPUT_DATE);
        RequestBody routeImage;
        MultipartBody.Part routeBetaImagePart;
        if (file != null) {
            routeImage = RequestBody.create(MediaType.parse("image/*"), file);
            routeBetaImagePart = MultipartBody.Part.createFormData("approach_image", file.getName(),
                    routeImage);
        } else {
            routeImage = RequestBody.create(MediaType.parse("image/*"), "");
            routeBetaImagePart = MultipartBody.Part.createFormData("approach_image", "",
                    routeImage);
        }
        GetDataService service = RetrofitClientInstance.UserApiClient(Preferences.
                getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);

        Call<AddApproachResponseModel> call = service.addApproach(routeBetaImagePart,
                RequestBody.create(MediaType.parse("text/plain"),
                        Preferences.getStringName(Preferences.USER_ID)),
                RequestBody.create(MediaType.parse("text/plain"), strLocationId),
                RequestBody.create(MediaType.parse("text/plain"), strCurrentDate),
                RequestBody.create(MediaType.parse("text/plain"), tvTimeCounter.getText().toString()));

        call.enqueue(new Callback<AddApproachResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AddApproachResponseModel> call,
                                   @NonNull Response<AddApproachResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    AddApproachResponseModel model = response.body();
                    if (model != null) {
                        if (model.getResponseCode().equals("1")) {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());
                            }
                            successAdded();
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
            public void onFailure(@NonNull Call<AddApproachResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for add approach.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (!isStart) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            stopLocation();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }
}
