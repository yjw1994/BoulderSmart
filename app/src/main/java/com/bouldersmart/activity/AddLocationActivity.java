package com.bouldersmart.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.AddLocationResponseModel;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mindorks.paracamera.Camera;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bouldersmart.common.Utills.ShowProgressDialog;
import static com.bouldersmart.common.Utills.hideProgressDialog;

public class AddLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Activity activity;
    MapView mpAddLocation;
    private Bundle mapViewBundle = null;
    GoogleMap gMap;
    ImageView ivCenterLocation;
    Button btnUploadCover, btnSaveLocation;
    private Camera camera = null;
    private File imgFile = null;
    private Uri outputUri = null;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean isMove = false;
    EditText etLocationName;
    LatLng latLng;
    String strAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_add_location);
        activity = AddLocationActivity.this;
        if (savedInstanceState != null) {
            String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        init();
    }

    private void init() {
        initToolbar();
        mpAddLocation = findViewById(R.id.mpAddLocation);
        ivCenterLocation = findViewById(R.id.ivCenterLocation);
        btnUploadCover = findViewById(R.id.btnUploadCover);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        etLocationName = findViewById(R.id.etLocationName);

        mpAddLocation.onCreate(mapViewBundle);
        mpAddLocation.getMapAsync(this);

        buildGoogleApiClient();

        // Here, click event set for UI interaction
        btnUploadCover.setOnClickListener(this);
        btnSaveLocation.setOnClickListener(this);
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
        TextView tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        tvTitleToolbar.setText(activity.getResources().getString(R.string.add_climbing_location));

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                googleMap.clear();
                ivCenterLocation.setVisibility(View.VISIBLE);
            }
        });
        addCenterMarker();
    }

    private void addCenterMarker() {
        if (gMap != null) {
            gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    LatLng position = gMap.getCameraPosition().target;
                    ivCenterLocation.setVisibility(View.GONE);
                    strAddress = addressFromLatLong(position);
                    latLng = position;
                    Logger.e("center", "lat->" + position.latitude);
                    Logger.e("center", "lng->" + position.longitude);
                    Logger.e("Address", "--->" + strAddress);

                    gMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromBitmap(Utills.getMarkerBitmapFromView(activity,
                                    R.drawable.ic_pin_location_32dp))));
                }
            });
        }

    }

    private String addressFromLatLong(LatLng position) {
        Geocoder geocoder;
        geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {
                return addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUploadCover:
                if (Utills.checkStoragePermission(activity)) {
                    OpenPhotoDialog();
                }
                break;
            case R.id.btnSaveLocation:
                if (isValid()) {
                    if (Utills.isNetworkAvailable(activity, true, false)) {
                        addNewLocation();
                    }
                }
                break;
        }
    }

    private void addNewLocation() {
        ShowProgressDialog(activity, getString(R.string.loading));
        RequestBody profileImage;
        MultipartBody.Part profileImagePart;
        if (imgFile != null) {
            profileImage = RequestBody.create(MediaType.parse("image/*"), imgFile);
            profileImagePart = MultipartBody.Part.createFormData("cover_image", imgFile.getName(), profileImage);
        } else {
            profileImage = RequestBody.create(MediaType.parse("image/*"), "");
            profileImagePart = MultipartBody.Part.createFormData("cover_image", "", profileImage);
        }
        GetDataService service = RetrofitClientInstance.UserApiClient(Preferences.
                getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);

        Call<AddLocationResponseModel> call = service.addLocation(profileImagePart,
                RequestBody.create(MediaType.parse("text/plain"),
                        Preferences.getStringName(Preferences.USER_ID)),
                RequestBody.create(MediaType.parse("text/plain"),
                        etLocationName.getText().toString().trim()),
                RequestBody.create(MediaType.parse("text/plain"), strAddress),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.latitude)),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.longitude)));

        call.enqueue(new Callback<AddLocationResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AddLocationResponseModel> call,
                                   @NonNull Response<AddLocationResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    AddLocationResponseModel model = response.body();
                    if (model != null) {
                        if (model.getResponseCode().equals("1")) {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());
                            }
                            stopLocation();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("model", model.getData());
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
            public void onFailure(@NonNull Call<AddLocationResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for add location.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private boolean isValid() {
        if (Utills.isEmpty(etLocationName.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_location_name));
            return false;
        } else if (latLng == null) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_select_location));
            return false;
        } else if (imgFile == null) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_image));
            return false;
        }
        return true;
    }

    private void OpenPhotoDialog() {
        final CharSequence[] options = {activity.getString(R.string.take_photo), activity
                .getString(R.string.choose_from_gallery), activity.getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.select_option_image));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(activity.getString(R.string.take_photo))) {
                    dialog.dismiss();
                    camera = new Camera.Builder()
                            .resetToCorrectOrientation(true) // it will rotate the camera bitmap to the correct orientation from meta data
                            .setTakePhotoRequestCode(1)
                            .setImageFormat(Camera.IMAGE_JPEG)
                            .build(activity);
                    try {
                        camera.takePicture();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals(activity.getString(R.string.choose_from_gallery))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, Constants.PICK_IMAGE_GALLERY);
                } else if (options[item].equals(activity.getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (camera != null) {
                    Bitmap bitmap = camera.getCameraBitmap();
                    if (bitmap != null) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/Camera";
                        File dir = new File(file_path);
                        if (!dir.exists())
                            dir.mkdirs();
                        String format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                        File file = new File(dir, format + ".png");
                        FileOutputStream fo;
                        try {
                            fo = new FileOutputStream(file);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imgFile = file;
                        File compressedImageFile = null;
                        try {
                            compressedImageFile = new Compressor(activity).compressToFile(imgFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imgFile = compressedImageFile;
                        Uri inputUri = Uri.fromFile(new File(String.valueOf(imgFile)));
                        outputUri = Uri.fromFile(new File(Utills.fileStoragePath(activity), Calendar
                                .getInstance().getTimeInMillis() + ".png"));

                        Crop.of(inputUri, outputUri).asSquare().start(activity);
                    } else {
                        Utills.ShowToastNormal(activity, "Picture not taken!");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imgFile = null;
            }
        } else if (requestCode == Constants.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    imgFile = FileUtils.getFile(activity, selectedImage);
                    imgFile = new Compressor(activity).compressToFile(imgFile);
                    Uri inputUri = Uri.fromFile(new File(String.valueOf(imgFile)));
                    outputUri = Uri.fromFile(new File(Utills.fileStoragePath(activity), Calendar
                            .getInstance().getTimeInMillis() + ".png"));

                    Crop.of(inputUri, outputUri).asSquare().start(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imgFile = null;
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String path = Objects.requireNonNull(FileUtils.getPath(activity, Crop.getOutput(data)));
                    imgFile = new File(path);
                    Bitmap compressedImageBitmap = new Compressor(activity).compressToBitmap(imgFile);
                    Logger.e("***", "path->" + path);
//                    String strLogo = ContentFileUtils.getRealPath(activity, Uri.fromFile(imgFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imgFile = null;
            }
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
        if (mpAddLocation != null) {
            mpAddLocation.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mpAddLocation != null) {
            mpAddLocation.onStop();
        }
    }

    @Override
    protected void onPause() {
        if (mpAddLocation != null) {
            mpAddLocation.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mpAddLocation != null) {
            mpAddLocation.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mpAddLocation != null) {
            mpAddLocation.onLowMemory();
        }
    }

    @Override
    protected void onResume() {
        if (mpAddLocation != null) {
            mpAddLocation.onResume();
        }
        super.onResume();
        Utills.RequestPermission(activity);
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
        if (!isMove) {
            isMove = true;
            CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            gMap.animateCamera(location1);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        stopLocation();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
