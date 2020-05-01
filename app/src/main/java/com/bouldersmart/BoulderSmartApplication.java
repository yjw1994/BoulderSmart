package com.bouldersmart;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import androidx.multidex.MultiDex;

import com.bouldersmart.common.Logger;
import com.bouldersmart.common.Preferences;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BoulderSmartApplication extends Application {
    public static BoulderSmartApplication application;
    private static final String TAG = BoulderSmartApplication.class.getSimpleName();
    public ImageLoader mImageLoader;

    public BoulderSmartApplication() {
        application = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        FirebaseApp.initializeApp(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        /*
         * Here, get fire-base token
         **/
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                Logger.e("--boulder-token--", "app-class-->" + deviceToken);
                Preferences.SetStringName(Preferences.DEVICE_TOKEN, deviceToken);
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            }
        });

        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Logger.e(TAG, "Facebook Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Logger.e(TAG, "Facebook Hash Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "printHashKey()" + e.getMessage());
        }

        // Here, universal image-loader set-up
        initImageLoader(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static BoulderSmartApplication getApp() {
        if (application == null) {
            application = new BoulderSmartApplication();
        }
        return application;
    }

    public static Context getAppContext() {
        if (application == null) {
            application = new BoulderSmartApplication();
        }
        return application;
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.threadPriority(3);
        builder.denyCacheImageMultipleSizesInMemory();
        builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        builder.diskCacheSize(52428800);
        builder.tasksProcessingOrder(QueueProcessingType.LIFO);
        builder.threadPoolSize(10);

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(builder.build());
    }

    public ImageLoader getmImageLoader() {
        return this.mImageLoader;
    }

    public DisplayImageOptions getDisplayImageOptionForBackground() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ic_gallery_32dp)
                .showImageOnFail(R.drawable.ic_gallery_32dp)
                .showImageOnLoading(R.drawable.ic_gallery_32dp)
                .build();
    }

    public DisplayImageOptions getDisplayImageOptionForBackground(Drawable drawable) {
        return new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .showImageOnLoading(drawable)
                .showImageOnFail(drawable)
                .showImageForEmptyUri(drawable)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public DisplayImageOptions getDisplayImageOptionChat() {
        return new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(1))
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_gallery_32dp)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public DisplayImageOptions getDisplayImageOptionForBackgroundRoundRest() {
        return new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(90))
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_gallery_32dp)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public DisplayImageOptions getDisplayImageOptionForBackgroundRoundRestProfile() {
        return new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(360))
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_gallery_32dp)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public DisplayImageOptions getDisplayImageOptionGallery() {
        return new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(15))
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }
}
