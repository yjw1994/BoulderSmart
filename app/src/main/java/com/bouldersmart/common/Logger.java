package com.bouldersmart.common;

import android.util.Log;

import com.bouldersmart.BuildConfig;
import com.bouldersmart.R;


/**
 * Created by COMP on 20-07-2018.
 */

public class Logger {

    public static final String TAG = String.valueOf(R.string.app_name);

    public static final boolean DEBUGGING_BUILD = true;

    public static void d(final String tag, final Object message) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message.toString());
    }

    public static void i(final String tag, final String message) {
        if (DEBUGGING_BUILD)
            Log.i(tag, message);
    }

    public static void e(final String tag, final Object message) {
        if (DEBUGGING_BUILD) {
            Log.e(tag, "" + message.toString());

        }
    }

   /* public static void e(final String tag, final Object message, final Throwable t) {
        if (DEBUGGING_BUILD) {
            Log.e(tag, message.toString(), t);

        }
    }*/

    /*public static void error(final String tag, final String message,
                             final Throwable throwable) {
        if (DEBUGGING_BUILD)
            Log.e(tag, message, throwable);
    }*/
}