package com.bouldersmart.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bouldersmart.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class Utills {
    private static ProgressDialog progressDialog;

    @SuppressLint("StaticFieldLeak")

    public static Typeface SetCustomFontTypeface(String fontName, Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontName);
    }

    public static Typeface SetCustomFont(String fontName, Context context) {
        return Typeface.createFromAsset(context.getAssets(), fontName);
    }


    public static void ShowToastNormal(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence) || charSequence.toString().equalsIgnoreCase("null");
    }

    public static boolean isEmptyABoolean(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence) || charSequence.toString().equalsIgnoreCase("null");
    }

    public static boolean isValidLink(String urlString) {
        return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
    }

    public final static boolean isValidEmailBOOLEAN(CharSequence targetCharSequence) {
        if (TextUtils.isEmpty(targetCharSequence))
            return false;
        else
            return Patterns.EMAIL_ADDRESS.matcher(targetCharSequence).matches();
    }

    public static void hideKeyBoard(View view, Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(View v, Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    public static boolean isNetworkAvailable(final Context context, boolean canShowErrorDialogOnFail, final boolean isFinish) {
        boolean isNetAvailable = false;

        if (context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (mConnectivityManager != null) {
                boolean mobileNetwork = false;
                boolean wifiNetwork = false;
                boolean mobileNetworkConnecetd = false;
                boolean wifiNetworkConnecetd = false;

                final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mobileInfo != null) {
                    mobileNetwork = mobileInfo.isAvailable();
                }

                if (wifiInfo != null) {
                    wifiNetwork = wifiInfo.isAvailable();
                }

                if (wifiNetwork || mobileNetwork) {
                    if (mobileInfo != null)
                        mobileNetworkConnecetd = mobileInfo
                                .isConnectedOrConnecting();
                    wifiNetworkConnecetd = wifiInfo.isConnectedOrConnecting();
                }

                isNetAvailable = (mobileNetworkConnecetd || wifiNetworkConnecetd);
            }
            context.setTheme(R.style.AppTheme);
            if (!isNetAvailable && canShowErrorDialogOnFail) {
                Log.v("TAG", "context : " + context.toString());
                if (context instanceof Activity) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            showAlertWithFinis((Activity) context, context.getString(R.string.app_name),
                                    context.getString(R.string.lbl_network_alert), isFinish);
                        }
                    });
                }
            }
        }
        return isNetAvailable;
    }

    public static Boolean isValidationEmpty(String value) {

        if (value == null || value.isEmpty() || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("") || value.length() == 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Boolean isValidationEmptyWithZero(String value) {

        Logger.e("29/03 value ----> ", value + "");

        if (value == null || value.isEmpty() || value.equals("null") || value.length() <= 0 || value.equalsIgnoreCase("0")) {

            Logger.e("29/03 return true ----> ", "true" + "");
            return true;
        } else {

            Logger.e("29/03 return false ----> ", "false" + "");

            return false;
        }

    }


    public static void showAlertWithFinis(final Activity activity, String title, String message, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish) {
                    dialog.dismiss();
                    activity.finish();
                } else {
                    dialog.dismiss();
                }
            }
        }).show();
    }

    public static void showAlert(final Activity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(activity.getString(R.string.ok), null);
        if (message != null && message.length() != 0)
            builder.show();
    }

    public static void showAlertWithFinish(final Activity activity, String title, String message, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish) {

                    if (!activity.isFinishing()) {
                        dialog.dismiss();
                        activity.finish();
                    }

                } else {
                    dialog.dismiss();
                }
            }
        }).show();
    }

    public static void ShowProgressDialog(Context context, String message) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static boolean CheckNullOrBlank(String value) {

        if (value == null || value.equalsIgnoreCase("") || value.length() == 0) {
            return true;
        } else {
            return false;

        }

    }

    public static Boolean IsVideoFile(Context context, String path) {
        String type = null;
        String[] extensionSplit = path.split("\\.");
        String extension = extensionSplit[extensionSplit.length - 1];

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());

            if (type == null) {
                return false;
            } else {
                if (type.startsWith("video/"))
                    return true;
            }
            return false;
        }
        return false;
    }

    public static String displayCurrencyInfoForLocaleSymbol(Locale locale) {
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);

        Logger.e("27/11 Currency Code: ----> ", currency.getCurrencyCode());
        Logger.e("27/11 Symbol: ----> ", currency.getSymbol());
        Logger.e("27/11 Default Fraction Digits: ----> ", currency.getDefaultFractionDigits());

        System.out.println("Currency Code: " + currency.getCurrencyCode());
        System.out.println("Symbol: " + currency.getSymbol());
        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
        System.out.println();

        return currency.getSymbol();
    }

    public static String displayCurrencyInfoForLocaleCurrencyCode(Locale locale) {
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);

        Logger.e("27/11 Currency Code: ----> ", currency.getCurrencyCode());
        Logger.e("27/11 Symbol: ----> ", currency.getSymbol());
        Logger.e("27/11 Default Fraction Digits: ----> ", currency.getDefaultFractionDigits());

        System.out.println("Currency Code: " + currency.getCurrencyCode());
        System.out.println("Symbol: " + currency.getSymbol());
        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
        System.out.println();

        return currency.getCurrencyCode();
    }

    public static String DecimalFormat(double value) {
        String str_value = new DecimalFormat("##.##").format(value);
        return str_value;
    }

    public static String CurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }

    public static String CurrentDateTime_dd_MM_yyyy_HH_mm_ss() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String formattedDate = df.format(c.getTime());

        return formattedDate;

    }

    public static String twoDecimalPointValues(String value) {
        NumberFormat numberFormat = new DecimalFormat(".00");
        value = numberFormat.format(Double.parseDouble(value));             // -.46
        System.out.print(value + "\n");
        return value;

    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFormatChange(String date, String inputFormat, String outputFormat) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat spf = new SimpleDateFormat(inputFormat);
        try {
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat(outputFormat);
            date = spf.format(newDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String differenceTwoDate(String startDate, String endDate, String inputFormat) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputFormat);

        try {
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);
            if (date1 == null) {
                return "0";
            }
            if (date2 == null) {
                return "0";
            }
            return printDifference(date1, date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return "0";
        }
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    private static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours,
                elapsedMinutes, elapsedSeconds);
        if ((elapsedDays + "").contains("-")) {
            return "0d 0h";
        }
        return elapsedDays + "d " + elapsedHours + "h";
    }


    public static void setRecyclerView(Context context, RecyclerView recyclerView) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public static void setHorizontalRecyclerView(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    public static void setGridRecyclerView(Activity context, RecyclerView recyclerView, int column) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, column));
    }

    // ========================= Date picker Code Start =========================

    public static void CommonDatePickerDialog(Activity activity, String identify,
                                              OnClickCommonDate clickCommonDate, boolean isMinCurrentDate) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String myTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                    if (clickCommonDate != null)
                        clickCommonDate.onClickSelectedDate(identify, myTime);
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setTitle("");
        if (isMinCurrentDate)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public static String GetStringFromArrayList(ArrayList<String> arr) {
        String saveTag = "";
        for (int j = 0; j < arr.size(); j++) {
            if (saveTag.length() == 0) {
                saveTag = arr.get(j);
            } else {
                saveTag = saveTag + "," + arr.get(j);
            }
        }
        return saveTag;
    }

    public interface OnClickCommonDate {
        void onClickSelectedDate(String identify, String date);
    }

    // ========================= Date picker Code End =========================

    // ========================= Time picker Code Start =========================
    public static void CommonTimePicker(Activity activity, String identify, OnClickCommonTime clickCommonTime
            , String timeFormat) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (view, hourOfDay, minute1) -> {
            String Time = hourOfDay + ":" + minute1;

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                Date myDate = dateFormat.parse(Time);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat.length() != 0 ? timeFormat
                        : "hh:mm:ss a", Locale.US);
                String display = simpleDateFormat.format(myDate);
                if (clickCommonTime != null)
                    clickCommonTime.onClickSelectedTime(identify, display);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public interface OnClickCommonTime {
        void onClickSelectedTime(String identify, String time);
    }

    // ========================= Time picker Code End =========================

    public static void showCustomDialog(Context context, String title, String message, String
            positiveButton, DialogInterface.OnClickListener listenerPositive, String
                                                negativeButton, DialogInterface
                                                .OnClickListener listenerNegative,
                                        boolean isCancel, boolean isNegative) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(isCancel);

        builder.setMessage(message).setPositiveButton(positiveButton, listenerPositive);
        if (isNegative) {
            builder.setNegativeButton(negativeButton, listenerNegative);
        }

        android.app.AlertDialog mAlertDialog = builder.create();
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null,
                    null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
     * File storage path from external storage
     * */
    public static String fileStoragePath(Activity activity) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), activity.getResources()
                .getString(R.string.app_name));
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        Logger.e("path", "storage->" + myDirectory.getAbsolutePath());
        return myDirectory.getAbsolutePath();
    }

    public static String deviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static HashMap<String, String> compareTwoHashMapGetModifyContent(HashMap<String, String> oldMap,
                                                                            HashMap<String, String> modifiedMap) {
        HashMap<String, String> map = new HashMap<>();
        for (final String key : modifiedMap.keySet()) {
            if (oldMap.containsKey(key)) {
                if (!modifiedMap.get(key).equals(oldMap.get(key))) {
                    map.put(key, modifiedMap.get(key));
                }
            } else map.put(key, modifiedMap.get(key));
        }
        return map;
    }

    public static String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static ArrayList<File> searchFiles(String filePath) {
        ArrayList<File> list = new ArrayList<>();
        File dir = new File(filePath);
        if (dir.isDirectory()) {
            String pattern = ".mp4";
            //Get the listfile of that folder
            final File listFile[] = dir.listFiles();

            if (listFile != null) {
                for (int i = 0; i < listFile.length; i++) {
                    list.add(listFile[i]);
                }
            }
        }
        return list;
    }

    public static String fileDirectoryPath(Activity activity) {
        return activity.getExternalFilesDir("Images") + "/";
    }

    public static ArrayList<File> removeDuplicates(ArrayList<File> list) {
        // Set set1 = new LinkedHashSet(list);
        Set set = new TreeSet(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (((File) o1).getName().equalsIgnoreCase(((File) o2).getName())) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);
        return (ArrayList<File>) new ArrayList(set);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void shareFileOrTextText(String message, Context mContext, File file) {
        try {
            Uri uri = null;
            if (file.exists()) {
                uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext()
                        .getPackageName()
                        + ".provider", file);
            }
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mContext);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("*/*");
            sharingIntent.setType("vnd.android-dir/mms-sms");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            sharingIntent.putExtra("address", "8141323065;9924461072");
            if (defaultSmsPackageName != null)
            // Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sharingIntent.setPackage(defaultSmsPackageName);
            }
            if (uri != null) {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sharingIntent.setDataAndType(uri, "*/*");
            }

            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share"/*mContext.getResources().getString(R.string.share_using)*/));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getMarkerBitmapFromView(Activity activity, @DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_map_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.ivPinLocation);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public static void RequestPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public static boolean checkStoragePermission(Activity activity) {
        String[] mPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        try {
            if (ActivityCompat.checkSelfPermission(activity, mPermission[0])
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, mPermission,
                        2211);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //    showCurvedPolyline(coordinate, coordinate2, 0.4, gMap);
    private void showCurvedPolyline(LatLng p1, LatLng p2, double k, GoogleMap gMap) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 - h1) / numpoints;

        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }

        //Draw polyline
        gMap.addPolyline(options.width(10).color(Color.MAGENTA).geodesic(false).pattern(pattern));
    }

    //statusIndex=1 or 0
    public static boolean locationIsAtStatus(Location start, Location end) {
        if (start == null) {
            return false;
        }
        if (end == null) {
            return false;
        }
        float distance = end.distanceTo(start);
        Logger.e("--->", ("Distance :" + distance));
        return distance < 20;
    }

    public static String convertMillsToHMmSs(long seconds) {
        long ml = seconds % 1000;
        long s = ml % 60;
        long m = (ml / 60) % 60;
        long h = (ml / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static File getScreenshotFile(Activity activity) {
        File screenshotDirectory = getScreenshotsDirectory(activity);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS",
                Locale.getDefault());
        String screenshotName = dateFormat.format(new Date()) + ".jpeg";
        return new File(screenshotDirectory, screenshotName);
    }

    private static File getScreenshotsDirectory(Activity context) {
        String dirName = "screenshots";

        File rootDir = context.getExternalFilesDir("");
        File directory = new File(rootDir, dirName);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                try {
                    throw new IllegalAccessException("Unable to create screenshot directory " + directory.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return directory;
    }
}
