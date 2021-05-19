package com.chug.north_outlet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.chug.north_outlet.App;
import com.chug.north_outlet.Config;


public class PreferenceHelper {
	
	public static SharedPreferences getSharedPreferences(){
		SharedPreferences preference = App.getAppContext().getSharedPreferences(Config.EHOME_PREFRENCE, Context.MODE_PRIVATE);
		return preference;
	}
	
	public static SharedPreferences getLoginSharedPreferences(){
		SharedPreferences preference = App.getAppContext().getSharedPreferences(Config.LOGIN_PREFRENCE, Context.MODE_PRIVATE);
		return preference;
	}

	public static void write(String k, int v) {
		Editor editor = getSharedPreferences().edit();
		editor.putInt(k, v);
		editor.commit();
	}

	public static void write(String k, boolean v) {
		Editor editor = getSharedPreferences().edit();
		editor.putBoolean(k, v);
		editor.commit();
	}

	public static void write(String k, String v) {
		Editor editor = getSharedPreferences().edit();
		editor.putString(k, v);
		editor.commit();
	}

	public static void write(String fileName, String k, long v) {
		Editor editor = getSharedPreferences().edit();
		editor.putLong(k, v);
		editor.commit();
	}

	public static void write(String k, long v) {
		Editor editor = getSharedPreferences().edit();
		editor.putLong(k, v);
		editor.commit();
	}

	public static int readInt(String k) {
		return getSharedPreferences().getInt(k, 0);
	}

	public static int readInt(String k, int defv) {
		return getSharedPreferences().getInt(k, defv);
	}

	public static boolean readBoolean(String k) {
		return getSharedPreferences().getBoolean(k, false);
	}

	public static boolean readBoolean(String k, boolean defBool) {
		return getSharedPreferences().getBoolean(k, defBool);
	}

	public static String readString(String k) {
		return getSharedPreferences().getString(k, null);
	}

	public static String readString(String k, String defV) {
		return getSharedPreferences().getString(k, defV);
	}

	public static Long readLong(String k) {
		return getSharedPreferences().getLong(k, 0);
	}

	public static Long readLong(String k, long defV) {
		return getSharedPreferences().getLong(k, defV);
	}

	public static Long readLong(String fileName, String k) {
		return getSharedPreferences().getLong(k, 0);
	}

	public static void remove(String k) {
		Editor editor = getSharedPreferences().edit();
		editor.remove(k);
		editor.commit();
	}
	

	public static void clean() {
		Editor editor = getSharedPreferences().edit();
		editor.clear();
		editor.commit();
	}
	
	
	/******************** 远程appId与authKey存储 ***************************/
	public static void loginWrite(String k, String v) {
		Editor editor = getLoginSharedPreferences().edit();
		editor.putString(k, v);
		editor.commit();
	}
	
	public static void loginWrite(String k, int v) {
		Editor editor = getLoginSharedPreferences().edit();
		editor.putInt(k, v);
		editor.commit();
	}

	public static String readLoginString(String k) {
		return getLoginSharedPreferences().getString(k, null);
	}

	public static String readLoginString(String k, String defV) {
		return getLoginSharedPreferences().getString(k, defV);
	}
	
	public static int readLoginInt(String k) {
		return getLoginSharedPreferences().getInt(k, 0);
	}

	public static int readLoginInt(String k, int defv) {
		return getLoginSharedPreferences().getInt(k, defv);
	}
}
