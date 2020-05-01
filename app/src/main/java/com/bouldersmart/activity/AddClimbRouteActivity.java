package com.bouldersmart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bouldersmart.R;
import com.bouldersmart.RetrifitInterface.GetDataService;
import com.bouldersmart.RetrofitApi.RetrofitClientInstance;
import com.bouldersmart.common.Constants;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.bouldersmart.common.Utills;
import com.bouldersmart.model.AddRouteResponseModel;
import com.bouldersmart.model.LocationDataModel;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mindorks.paracamera.Camera;
import com.soundcloud.android.crop.Crop;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

public class AddClimbRouteActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    Button btnUploadBeta, btnSaveRoute;
    SeekBar sbGrades;
    TextView tvGrades;
    EditText etRouteName;
    ScaleRatingBar srbRouteAdd;
    private Camera camera = null;
    private File imgFile = null;
    private Uri outputUri = null;
    String strGrade = "VB", strBetaImage = "", strLocationId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_add_climb_route);
        activity = AddClimbRouteActivity.this;

        init();
    }

    private void init() {
        initToolbar();
        GetIntentData();

        tvGrades = findViewById(R.id.tvGrades);
        srbRouteAdd = findViewById(R.id.srbRouteAdd);
        etRouteName = findViewById(R.id.etRouteName);
        sbGrades = findViewById(R.id.sbGrades);
        btnUploadBeta = findViewById(R.id.btnUploadBeta);
        btnSaveRoute = findViewById(R.id.btnSaveRoute);
        tvGrades.setText(strGrade);
        String[] arrayGrade = activity.getResources().getStringArray(R.array.grades);
        ArrayList<String> listGrades = new ArrayList<>(Arrays.asList(arrayGrade));

        sbGrades.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                strGrade = listGrades.get(progress - 1);
                tvGrades.setText(strGrade);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Here, click event set for UI interaction
        btnUploadBeta.setOnClickListener(this);
        btnSaveRoute.setOnClickListener(this);
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
        tvTitleToolbar.setText(activity.getResources().getString(R.string.add_new_climbing_route));
        ivAddLocation.setVisibility(View.INVISIBLE);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            LocationDataModel model = (LocationDataModel) intent.getSerializableExtra("model");
            if (model != null) {
                strLocationId = model.getLocation_id();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUploadBeta:
                if (Utills.checkStoragePermission(activity)) {
                    OpenPhotoDialog();
                }

                break;
            case R.id.btnSaveRoute:
                if (isValid()) {
                    if (Utills.isNetworkAvailable(activity, true, false)) {
                        addNewRoute();
                    }
                }
                break;
        }
    }

    private void addNewRoute() {
        ShowProgressDialog(activity, getString(R.string.loading));
        RequestBody routeImage;
        MultipartBody.Part routeBetaImagePart;
        File file = null;
        if (strBetaImage.length() != 0) {
            file = new File(strBetaImage);
        }
        if (file != null) {
            routeImage = RequestBody.create(MediaType.parse("image/*"), file);
            routeBetaImagePart = MultipartBody.Part.createFormData("route_image", file.getName(),
                    routeImage);
        } else {
            routeImage = RequestBody.create(MediaType.parse("image/*"), "");
            routeBetaImagePart = MultipartBody.Part.createFormData("route_image", "",
                    routeImage);
        }
        GetDataService service = RetrofitClientInstance.UserApiClient(Preferences.
                getStringName(Preferences.TOKEN_LOGIN)).create(GetDataService.class);

        Call<AddRouteResponseModel> call = service.addRoute(routeBetaImagePart,
                RequestBody.create(MediaType.parse("text/plain"),
                        Preferences.getStringName(Preferences.USER_ID)),
                RequestBody.create(MediaType.parse("text/plain"), strLocationId),
                RequestBody.create(MediaType.parse("text/plain"), etRouteName.getText().toString()
                        .trim()),
                RequestBody.create(MediaType.parse("text/plain"), strGrade),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(srbRouteAdd
                        .getRating())));

        call.enqueue(new Callback<AddRouteResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<AddRouteResponseModel> call,
                                   @NonNull Response<AddRouteResponseModel> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    AddRouteResponseModel model = response.body();
                    if (model != null) {
                        if (model.getResponseCode().equals("1")) {
                            if (!model.getResponseMsg().isEmpty()) {
                                Utills.ShowToastNormal(activity, model.getResponseMsg());
                            }

                            Intent returnIntent = new Intent();
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
            public void onFailure(@NonNull Call<AddRouteResponseModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                Logger.e("API Error", "Unable to submit for add route.");
                Utills.showAlert(activity, getString(R.string.app_name), t.getMessage());
                t.printStackTrace();
            }
        });
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
//                Camera camera = Utils.getCamera();
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
                    Logger.e("**beta**", "path->" + path);
                    startActivityForResult(new Intent(activity, UploadBetaActivity.class)
                            .putExtra("image_path", path), Constants.ADD_BETA_ACTIVITY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                imgFile = null;
            }
        } else if (requestCode == Constants.ADD_BETA_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra("image_beta")) {
                    strBetaImage = data.getStringExtra("image_beta");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isValid() {
        if (Utills.isEmpty(etRouteName.getText().toString().trim())) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_route_name));
            return false;
        } else if (Utills.isEmpty(strGrade)) {
            Utills.showAlert(activity, getString(R.string.app_name), getString(R.string.error_select_grade));
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
