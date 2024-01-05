package com.example.lmbank.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class WindowActivity {
    private WindowManager wm;
    private View view;
    public final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS//允许window扩展值屏幕之外
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN//当这个window显示的时候,隐藏所有的装饰物(比如状态栏)这个flag允许window使用整个屏幕区域
                    | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER//标记在其它窗口的LayoutParams.flags中的存在情况而不断地被调整
                    | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT);

    public void init(Context context) {
        lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        lp.height = size.y + (int) (40 * displayMetrics.density);
    }

    public void addView(View view) {
        if (wm != null) {
            this.view = view;
            wm.addView(view, lp);
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
