package com.bouldersmart.common;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Constants {
    public static String INPUT_DATE = "yyyy-MM-dd";
    public static String OUTPUT_DATE = "dd/MM/yyyy";
    public static String DEVICE_TYPE = "android";
    public static String IS_NOT_FB = "0";
    public static String IS_FB = "1";
    public static String IS_MANUAL = "0";
    public static String EMAIL_FOUND = "0"; // only for fb login
    public static String EMAIL_NOT_FOUND = "1"; // only for fb login

    public static int PICK_IMAGE_GALLERY = 200;
    public static int ADD_ROUTE_ACTIVITY = 991;
    public static int ADD_COMMENT_ACTIVITY = 992;
    public static int ADD_APPROACH_ACTIVITY = 993;
    public static int ADD_BETA_ACTIVITY = 994;
    public static int ADD_LOCATION_ACTIVITY = 995;
    public static int CLIMB_DETAILS_ACTIVITY = 996;

    public static String getMD5(String data) {
        if (data.length() == 0) {
            return "";
        }
        String result;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes(Charset.forName("UTF-8")));
            result = String.format(Locale.ROOT, "%032x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

}
