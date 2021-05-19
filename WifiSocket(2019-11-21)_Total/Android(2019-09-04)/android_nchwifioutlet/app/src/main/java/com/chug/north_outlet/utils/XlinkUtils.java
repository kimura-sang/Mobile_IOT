package com.chug.north_outlet.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import com.chug.north_outlet.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class XlinkUtils {
	/**
	 * Map 转换为json
	 * 
	 * @param map
	 * @return
	 */
	public static JSONObject getJsonObject(Map<String, Object> map) {
		JSONObject jo = new JSONObject();
		Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			try {
				jo.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jo;

	}

	/**
	 * 截取 byte
	 * 
	 * @param src
	 *            源数据
	 * @param off
	 *            偏移量
	 * @param len
	 *            长度
	 * @return
	 */
	public static byte[] subBytes(byte[] bytes, int offset, int len) {
		byte[] b = new byte[len];
		System.arraycopy(bytes, offset, b, 0, len);
		return b;
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String base64EncryptUTF(byte[] key) {
		String str = null;
		try {
			str = new String(Base64.encode(key, Base64.DEFAULT), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String base64Encrypt(byte[] key) {
		return new String(Base64.encode(key, Base64.DEFAULT));
	}

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static byte[] base64Decrypt(String key) {
		byte[] bs = null;
		if (key != null) {
			bs = Base64.decode(key, Base64.DEFAULT);
			if (bs == null || bs.length == 0) {
				bs = key.getBytes();
			}
		}
		return bs;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isConnected() {

		ConnectivityManager connectivity = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isAvailable() && connectivity.getBackgroundDataSetting()) {
				return true;
			}
		}
		return false;
	}


	public static String getHexBinString(byte[] bs) {
		StringBuffer log = new StringBuffer();
		for (int i = 0; i < bs.length; i++) {
			log.append(String.format("%02X", (byte) bs[i]) + " ");
		}
		return log.toString();
	}

	/**
	 * 把byte转化成 二进制.
	 * 
	 * @param aByte
	 * @return
	 */
	public static String getBinString(byte aByte) {
		String out = "";
		int i = 0;
		for (i = 0; i < 8; i++) {
			int v = (aByte << i) & 0x80;
			v = (v >> 7) & 1;
			out += v;
		}
		return out;
	}

	static private final int bitValue0 = 0x01; // 0000 0001
	static private final int bitValue1 = 0x02; // 0000 0010
	static private final int bitValue2 = 0x04; // 0000 0100
	static private final int bitValue3 = 0x08; // 0000 1000
	static private final int bitValue4 = 0x10; // 0001 0000
	static private final int bitValue5 = 0x20; // 0010 0000
	static private final int bitValue6 = 0x40; // 0100 0000
	static private final int bitValue7 = 0x80; // 1000 0000

	/**
	 * 设置flags
	 * 
	 * @param index
	 *            第几个bit，从零开始排
	 * @param value
	 *            byte值
	 * @return
	 */
	public static byte setByteBit(int index, byte value) {
		if (index > 7) {
			throw new IllegalAccessError("setByteBit error index>7!!! ");
		}
		byte ret = value;
		if (index == 0) {
			ret |= bitValue0;
		} else if (index == 1) {
			ret |= bitValue1;
		} else if (index == 2) {
			ret |= bitValue2;
		} else if (index == 3) {
			ret |= bitValue3;
		} else if (index == 4) {
			ret |= bitValue4;
		} else if (index == 5) {
			ret |= bitValue5;
		} else if (index == 6) {
			ret |= bitValue6;
		} else if (index == 7) {
			ret |= bitValue7;
		}
		return ret;
	}



	/**
	 * 判断是否是wifi连接
	 */
	public static boolean isWifi() {
		ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null || cm.getActiveNetworkInfo() == null) {
			return false;
		}

		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity) {
		Intent intent = null;
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
		} else {
			intent = new Intent("/");
			ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
			intent.setComponent(cm);
			intent.setAction("android.intent.action.VIEW");
		}
		activity.startActivityForResult(intent, 0);
	}

	public static void shortTips(String tip) {
		if (tip == null) {
			return;
		}
		Tips.showShortToast(tip);
	}
	
	public static void shortTips(int resid) {
		if (resid == 0) {
			return;
		}
		Tips.showShortToast(App.getAppResources().getString(resid));
	}

	public static void longTips(String tip) {
		if (tip == null) {
			return;
		}
		Tips.showLongToast(tip);
	}
	
	public static void longTips(int resid) {
		if (resid == 0) {
			return;
		}
		Tips.showLongToast(App.getAppResources().getString(resid));
	}

}
