package com.chug.north_outlet.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chug.north_outlet.R;
import com.chug.north_outlet.bean.DbGroupInfo;
import com.chug.north_outlet.bean.DbScheduleInfo;
import com.chug.north_outlet.bean.EFDeviceOutlet;
import com.chug.north_outlet.bean.EFScene;
import com.chug.north_outlet.dao.DaoUtil;
import com.google.gson.Gson;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import org.xutils.db.sqlite.WhereBuilder;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by techno11 on 13/7/16.
 */
public class Util {

    static ArcConfiguration configuration;


    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static ArcConfiguration getArcConfiguration(Context mContext) {
        if (configuration == null)
            configuration = new ArcConfiguration(mContext);
        configuration.setColors(new int[]{mContext.getResources().getColor(R.color.primary_dark)});
        configuration.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
        return configuration;
    }

    public static List<EFDeviceOutlet> initAddedDeviceList() {
        List<EFDeviceOutlet> addedDeviceList = new ArrayList<>();
        List<EFDeviceOutlet> mOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("sceneId", "=", EFScene.DEFAULT_SCENEID));
        if (mOutletList != null && !mOutletList.isEmpty()) {
            for (int i = 0; i < mOutletList.size(); i++) {
                if (mOutletList.get(i).isDeviceAdded())
                    addedDeviceList.add(mOutletList.get(i));
            }
        }
        return addedDeviceList;
    }

    //<<<< mars_add_20190826
    public static List<EFDeviceOutlet> getDeviceListFromId(Long deviceId) {
        List<EFDeviceOutlet> addedDeviceList = new ArrayList<>();
        List<EFDeviceOutlet> mOutletList = DaoUtil.getAllList(EFDeviceOutlet.class, WhereBuilder.b("id", "=", deviceId));
        if (mOutletList != null && !mOutletList.isEmpty()) {
            for (int i = 0; i < mOutletList.size(); i++) {
                if (mOutletList.get(i).isDeviceAdded())
                    addedDeviceList.add(mOutletList.get(i));
            }
        }
        return addedDeviceList;
    }
    //>>>>

    //<<<< mars_add_20190826
    public static int getMaxUniqueNo() {
        int max = 0;
        List<DbScheduleInfo> scheduleInfos = DaoUtil.getAllList(DbScheduleInfo.class);
        if (scheduleInfos != null){
            for (int i = 0; i < scheduleInfos.size(); i++){
                if (max < scheduleInfos.get(i).getUniqueNo()){
                    max = scheduleInfos.get(i).getUniqueNo();
                }
            }
        }
        return max;
    }
    //>>>>

    public static SimpleArcDialog showArcDialog(Context context) {


        SimpleArcDialog mDialog = new SimpleArcDialog(context);
        mDialog.setConfiguration(Util.getArcConfiguration(context));
        mDialog.setCancelable(false);
        mDialog.show();
        return mDialog;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public static List<EFDeviceOutlet> getDeviceListFromGroup(DbGroupInfo dbGroupInfo){
        List<EFDeviceOutlet> efDeviceOutletList = new ArrayList<>();
        final ArrayList<String> aList = new ArrayList(Arrays.asList(dbGroupInfo.getDeviceList().split("~")));
        Gson gson = new Gson();
        for (int i=0;i<aList.size();i++){
           EFDeviceOutlet mOutlet = gson.fromJson(aList.get(i), EFDeviceOutlet.class);
            efDeviceOutletList.add(mOutlet);
        }
        return efDeviceOutletList;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean isTablet(Activity mActivity) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(width * width + height * height);
        return (screenDiagonal >= 9.0);
    }

    public static int getWidth(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return width;
    }

    public static int getHeight(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return height;
    }

    public static int getDpAsPixels(int sizeInDp) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        float scale = displaymetrics.density;
        return (int) (sizeInDp * scale + 0.5f);
    }

    public static float getPercentage(double range, double total) {
        float proportion = ((float) range) / ((float) total);
        return proportion * 100;
    }

    public static float getPercentage(int start, int end, int total) {
        float differance = 0;
        if (start > end) {
            differance = start - end;
        } else {
            differance = end - start;
        }
        float proportion = ((float) differance) / ((float) total);
        return proportion * 100;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "00:00:00:00:00:00";
    }

    public static void showMessage(Context context, String message) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context);
        sweetAlertDialog.setTitleText(context.getString(R.string.str_wifi_outlet));
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
    }

    public static SweetAlertDialog showAlertMessage(Context context, String message) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context);
        sweetAlertDialog.setTitleText(context.getString(R.string.str_wifi_outlet));
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
        return sweetAlertDialog;
    }

    public static int getSoftButtonsBarHeight(Context mContext) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }


   /* //According to local time to obtain Beijing time,
    localTimeStr format: HH: mm+ (NSString )convertLocalTimeToBeijingTime:(NSString )localTimeStr {
        NSDateFormatter *formatter_local = [[NSDateFormatter alloc]init];
        [formatter_local setTimeZone:[NSTimeZone systemTimeZone]];
        [formatter_local setDateFormat"HH:mm"];
        NSDate *date_tobeChange = [formatter_local dateFromString:localTimeStr];
        NSTimeZone *beijingTimeZone = [NSTimeZone timeZoneWithName"Asia/Shanghai"];
        NSDateFormatter *formatter_China = [[NSDateFormatter alloc]init];
        [formatter_China setTimeZone:beijingTimeZone];
        [formatter_China setDateFormat"HH:mm"];
        NSString *time_China = [formatter_China stringFromDate:date_tobeChange];
        return time_China;
    }
*/
    public static SweetAlertDialog createSweetDeleteDialog(Context context, String contentText) {

        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText("Are you sure?");
        dialog.setConfirmText("Yes,delete it!");
        dialog.setContentText(contentText);
        dialog.show();
        return dialog;
    }

}
