package com.example.lmbank.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.example.lmbank.activity.MainActivity;
import com.example.lmbank.activity.PeopleActivity;

public class IconUtil {
    public static void chaneIcon(Context context) {
        PackageManager packageManager = context.getPackageManager();
        //显示别名的设置
        packageManager.setComponentEnabledSetting(new ComponentName(context, PeopleActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        //disable 掉原来的设置
        packageManager.setComponentEnabledSetting(new ComponentName(context, MainActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
