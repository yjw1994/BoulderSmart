package com.bouldersmart.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bouldersmart.R;
import com.bouldersmart.common.FileUtil;
import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Utills;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import id.zelory.compressor.Compressor;

public class UploadBetaActivity extends BaseActivity implements View.OnClickListener {

    Activity activity;
    StickerView svBeta;
    ImageView ivBetaUpdate;
    LinearLayout llHandClick, llFootClick;
    ArrayList<String> listSticker = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_beta);
        activity = UploadBetaActivity.this;

        init();
    }

    private void init() {
        initToolbar();
        svBeta = findViewById(R.id.svBeta);
        ivBetaUpdate = findViewById(R.id.ivBetaUpdate);
        llHandClick = findViewById(R.id.llHandClick);
        llFootClick = findViewById(R.id.llFootClick);

        GetIntentData();

        svBeta.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Logger.e("--", "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();

                Logger.e("--", "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {

                if (listSticker != null && listSticker.size() > 0) {
                    listSticker.remove(listSticker.size() - 1);
                }
                Logger.e("-RemainCount-", "onStickerDeleted->" + listSticker.size());
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Logger.e("--", "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Logger.e("--", "onStickerTouchedDown");
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Logger.e("--", "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Logger.e("--", "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Logger.e("--", "onDoubleTapped: double tap will be with two click");
            }
        });

        //Here, UI click interaction
        llHandClick.setOnClickListener(this);
        llFootClick.setOnClickListener(this);
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
        tvTitleToolbar.setText(activity.getResources().getString(R.string.upload_photo_beta));
        ivAddLocation.setVisibility(View.VISIBLE);
        ivAddLocation.setImageResource(R.drawable.ic_save_24dp);
        ivAddLocation.setOnClickListener(v -> {
            if (listSticker.size() > 0) {
                File file = FileUtil.getNewFile(activity, activity.getResources()
                        .getString(R.string.app_name));
                if (file != null) {
                    svBeta.save(file);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("image_beta", file.getAbsolutePath());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            } else {
                Utills.showAlert(activity, activity.getResources().getString(R.string.app_name),
                        activity.getResources().getString(R.string.error_save_image));
            }
        });
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void GetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("image_path")) {
            String image_path = intent.getStringExtra("image_path");
            try {
                if (image_path != null) {
                    File imgFile = new File(image_path);
                    Bitmap compressedBitmap = new Compressor(activity).compressToBitmap(imgFile);
                    ivBetaUpdate.setImageBitmap(compressedBitmap);
                } else onBackPressed();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        @Override
        public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llHandClick:
                addHandOrFootCircle(R.drawable.circle_red);
                break;
            case R.id.llFootClick:
                addHandOrFootCircle(R.drawable.circle_blue);
                break;
        }
    }

    private void addHandOrFootCircle(int image) {
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());


        svBeta.setIcons(Arrays.asList(deleteIcon/*, zoomIcon*/));

        Drawable drawable = ContextCompat.getDrawable(this, image);
        svBeta.addSticker(new DrawableSticker(drawable));
        listSticker.add((listSticker.size() + 1) + "");
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
