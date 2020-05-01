package com.bouldersmart.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.bouldersmart.BoulderSmartApplication;
import com.bouldersmart.model.LoginDataModel;

import java.io.IOException;

/**
 * Created by abc on 2/8/2018.
 */

public class Preferences {
    public static final String LOGIN_OBJ = "login_object_data";
    public static final String USER_ID = "USER_ID";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String TOKEN_LOGIN = "token";
    public static final String FLASH_CHECK = "flash_check";

    private static SharedPreferences get() {
        return BoulderSmartApplication.getAppContext().getSharedPreferences("SaveBoulderInfo", Context.MODE_PRIVATE);
    }

    public static String getStringName(String Key) {
        return get().getString(Key, "");
    }

    public static void SetStringName(String Key, String Value) {
        get().edit().putString(Key, Value).commit();
    }

    public static boolean getBoolenValue(String Key) {
        return get().getBoolean(String.valueOf(Key), false);
    }

    public static void setBoolenValue(String Key, boolean TrueorFalse) {
        get().edit().putBoolean(String.valueOf(Key), TrueorFalse).commit();
    }

    public static int getInteger(String Key) {
        return get().getInt(String.valueOf(Key), 0);
    }

    public static void setInteger(String Key, int value) {
        get().edit().putInt(String.valueOf(Key), value).commit();
    }

    public static String getRadius(String Key) {
        return get().getString(Key, "25");
    }

    public static void setRadius(String key, String values) {
        get().edit().putString(key, values).commit();
    }

    public static LoginDataModel GetLoginObject() {
        try {
            return (LoginDataModel) ObjectSerializer.deserialize(get().getString(LOGIN_OBJ, ObjectSerializer.serialize(null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean SetLoginDetails(LoginDataModel res) {
        try {
            return get().edit()
                    .putString(LOGIN_OBJ, ObjectSerializer.serialize(res))
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public static void saveHashMapFromSP(String mapKey, HashMap<String, String> jsonMap) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences.Editor editor = get().edit();
        editor.putString(mapKey, jsonString);
        editor.apply();
    }

    public static HashMap<String, String> getHashMapFromSP(String mapKey) {
        String defValue = new Gson().toJson(new HashMap<String, String>());
        String json = get().getString(mapKey, defValue);
        TypeToken<HashMap<String, String>> token = new TypeToken<HashMap<String, String>>() {
        };
        return new Gson().fromJson(json, token.getType());
    }

    public static void clearHashMapFromSP(String mapKey, HashMap<String, String> jsonMap) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences.Editor editor = get().edit();
        editor.putString(mapKey, jsonString);
        editor.apply();
    }

    public static <T> void saveArrayListFromSP(String mapKey, ArrayList<T> callLog) {
        SharedPreferences.Editor prefsEditor = get().edit();
        Gson gson = new Gson();
        String json = gson.toJson(callLog);
        prefsEditor.putString(mapKey, json);
        prefsEditor.apply();
    }

    public static <T> ArrayList<T> getArrayListFromSP(String mapKey, Type cls) {
        Gson gson = new Gson();

        String jsonPreferences = get().getString(mapKey, "");
        return gson.fromJson(jsonPreferences, cls);
    }

    public static <T> void clearArrayListFromSP(String mapKey, ArrayList<T> callLog) {
        SharedPreferences.Editor prefsEditor = get().edit();
        Gson gson = new Gson();
        String json = gson.toJson(callLog);
        prefsEditor.putString(mapKey, json);
        prefsEditor.apply();
    }*/
}

