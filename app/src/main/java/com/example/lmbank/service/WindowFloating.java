package com.example.lmbank.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class WindowFloating {
    private WindowManager wm;
    private View view;
    private final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH // 监听触摸事件
            , // 不受限制的布局
            PixelFormat.TRANSLUCENT);

    public void init(Context context) {
        if (wm != null) {
            return;
        }
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.type = Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        lp.gravity = Gravity.TOP;
    }

    public void addView(View view) {
        try {
            if (wm != null) {
                this.view = view;
                wm.addView(view, lp);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void onDestroy() {
        try {
            if (wm != null && view != null) {
                wm.removeView(view);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

}
