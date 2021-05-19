package com.chug.north_outlet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chug.north_outlet.App;

public class Tips {

    public static final String TAG = Tips.class.getSimpleName();

    public static final int LONG  = Toast.LENGTH_LONG;
    public static final int SHORT = Toast.LENGTH_SHORT;

    /**
     * Toast
     *
     * @param resid
     * @param duration
     */
    public static void showToast(Context context, int resid, int duration) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, resid, duration).show();
    }

    /**
     * Toast
     *
     * @param text
     * @param duration
     */
    public static void showToast(CharSequence text, int duration) {
        if (App.getAppContext() == null) {
            return;
        }
        Toast.makeText(App.getAppContext(), text, duration).show();
    }

    /**
     * Toast
     *
     * @param text
     */
    public static void showShortToast(CharSequence text) {
        if (App.getAppContext() == null) {
            return;
        }
        Toast toast = Toast.makeText(App.getAppContext(), text, Toast.LENGTH_SHORT);
        //		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showLongToast(CharSequence text) {
        if (App.getAppContext() == null) {
            return;
        }
        Toast toast = Toast.makeText(App.getAppContext(), text, Toast.LENGTH_LONG);
        //		toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showShortToast(int resid) {
        if (App.getAppContext() == null) {
            return;
        }
        Toast.makeText(App.getAppContext(), resid, Toast.LENGTH_SHORT).show();
    }

    /**
     * 带图片消息提示
     *
     * @param ImageResourceId
     * @param duration
     */
    public static void showImageToast(Activity act, int ImageResourceId, Object o, int duration) {
        if (act == null) {
            return;
        }
        Toast toast = null;
        //创建一个Toast提示消息
        if (o instanceof CharSequence) {
            toast = Toast.makeText(act, (CharSequence) o, duration);
        } else if (o instanceof Integer) {
            toast = Toast.makeText(act, (Integer) o, duration);
        }
        //设置Toast提示消息在屏幕上的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取Toast提示消息里原有的View
        View toastView = toast.getView();
        //创建一个ImageView
        ImageView img = new ImageView(act);
        img.setImageResource(ImageResourceId);
        //创建一个LineLayout容器 
        LinearLayout ll = new LinearLayout(act);
        //向LinearLayout中添加ImageView和Toast原有的View
        ll.addView(img);
        ll.addView(toastView);
        //将LineLayout容器设置为toast的View
        toast.setView(ll);
        //显示消息
        toast.show();
    }
}
