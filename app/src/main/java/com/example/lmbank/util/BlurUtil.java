package com.example.lmbank.util;
// 导入需要的类

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.lmbank.R;

public class BlurUtil {
    private static int scaleRatio = 5;
    private static int blurRadius = 50;
    // 实现动态毛玻璃效果的方法
    //图片缩放比例

    // 实现动态毛玻璃效果的方法
    public static Drawable applyBlur(Context context) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            // 将 Drawable 转换为 Bitmap
            Bitmap bitmap = drawableToBitmap(wallpaperDrawable);
            // 缩放 Bitmap
            int scaledWidth = bitmap.getWidth() / scaleRatio;
            int scaledHeight = bitmap.getHeight() / scaleRatio;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
            // 对缩放后的 Bitmap 进行模糊处理
            Bitmap blurredBitmap = FastBlur.doBlur(scaledBitmap, blurRadius, true);
            return new BitmapDrawable(context.getResources(), blurredBitmap);
        } catch (Exception e) {
            Drawable fallbackDrawable = context.getDrawable(R.mipmap.wallpaper);
            // 将 Drawable 转换为 Bitmap
            Bitmap bitmap = drawableToBitmap(fallbackDrawable);
            // 缩放 Bitmap
            int scaledWidth = bitmap.getWidth() / scaleRatio;
            int scaledHeight = bitmap.getHeight() / scaleRatio;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
            // 对缩放后的 Bitmap 进行模糊处理
            Bitmap blurredBitmap = FastBlur.doBlur(scaledBitmap, blurRadius, true);
            return new BitmapDrawable(context.getResources(), blurredBitmap);
        }
    }

    // 将 Drawable 转换为 Bitmap 的方法
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
