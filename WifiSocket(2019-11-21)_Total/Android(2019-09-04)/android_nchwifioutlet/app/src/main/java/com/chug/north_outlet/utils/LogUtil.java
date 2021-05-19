package com.chug.north_outlet.utils;

import android.util.Log;

import com.chug.north_outlet.Config;

public class LogUtil {

    public static void v(String tag, String msg) {
        if (debugOpen()) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg, Exception e) {
        if (debugOpen()) {
            Log.i(tag, msg, e);
        }
    }

    public static void i(String tag, String msg) {
        if (debugOpen()) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Exception e) {
        if (debugOpen()) {
            Log.i(tag, msg, e);
        }
    }

    public static void d(String tag, String msg) {
        if (debugOpen()) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Exception e) {
        if (debugOpen()) {
            Log.d(tag, msg, e);
        }
    }

    public static void w(String tag, String msg) {
        if (debugOpen()) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, Exception e) {
        if (debugOpen()) {
            Log.w(tag, e);
        }
    }

    public static void w(String tag, String msg, Exception e) {
        if (debugOpen()) {
            Log.w(tag, msg, e);
        }
    }

    public static void e(String tag, String msg) {
        if (debugOpen()) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (debugOpen()) {
            Log.e(tag, msg, e);
        }
    }

    private static boolean debugOpen() {
        return Config.DEBUG;
    }
}
