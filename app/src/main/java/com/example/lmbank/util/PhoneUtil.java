package com.example.lmbank.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telecom.Call;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;

import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PhoneUtil {

    public static void getPhoneData(String phone, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url("https://cx.shouji.360.cn/phonearea.php?number=" + phone);
        System.out.println("网络请求" + phone);
        client.newCall(builder.build()).enqueue(callback);
    }

    public static String getOperatorName(String phoneNumber) {
        String operatorName = null;
        switch (phoneNumber) {
            case "10000":
                operatorName = "中国电信";
                break;
            case "10010":
                operatorName = "中国联通";
                break;
            case "10086":
                operatorName = "中国移动";
                break;
            case "1060":
                operatorName = "中国广电";
                break;
        }
        return operatorName;
    }

    // 拨打电话
    @SuppressLint("MissingPermission")
    public static void placeCall(Context context, String phoneNumber) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        Uri number = Uri.fromParts("tel", phoneNumber, null);
        if (telecomManager != null) {
            Bundle extras = new Bundle();
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getPhoneAccountHandle(context));
            telecomManager.placeCall(number, extras);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, number);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        }
    }

    /**
     * 修改通话记录
     *
     * @param contentResolver
     * @param phone
     * @param phone_a
     */
    @SuppressLint("MissingPermission")
    public static void update(Context context, ContentResolver contentResolver, String phone, String phone_a) {
        System.out.println(phone + "|" + phone_a);
        if (phone.equals(phone_a)) {
            return;
        }
        if (contentResolver == null) {
            return;
        }
        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        String selection = CallLog.Calls.NUMBER + " = ?";
        String[] selectionArgs = {phone};
        String sortOrder = CallLog.Calls.DATE + " DESC";
        Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        if (cursor != null && cursor.moveToFirst()) {
            int _id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
            long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            contentResolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + "=?", new String[]{String.valueOf(_id)});
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, phone_a);
            values.put(CallLog.Calls.DATE, date);
            values.put(CallLog.Calls.DURATION, duration);
            values.put(CallLog.Calls.TYPE, type);
            String text = SharedPreferencesUtils.getString(context, "text", null);
            if (text != null) {
                values.put(CallLog.Calls.GEOCODED_LOCATION, text);
                values.put("geocoded_location", text);
            }
            contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static PhoneAccountHandle getPhoneAccountHandle(Context context) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            @SuppressLint("MissingPermission") List<PhoneAccountHandle> phoneAccounts = telecomManager.getCallCapablePhoneAccounts();
            if (!phoneAccounts.isEmpty()) {
                return phoneAccounts.get(0); // 获取第一个电话账户
            }
        }
        return null;
    }

    public static String formatPhoneNumber(String phoneNumber) {
        String formattedNumber = phoneNumber.replaceAll("\\s", ""); // 去除空格
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < formattedNumber.length(); i++) {
            if (i > 0 && i % 3 == 0) {
                result.append(" "); // 在每个网络识别号后添加空格
            }
            result.append(formattedNumber.charAt(i));
        }
        return result.toString();
    }

    public static String getPhoneAll(String phone) {
        return phone.replaceAll("-", "").replaceAll(" ", "");
    }

    public static String getCallPhone(Call call) {
        if (call == null || call.getDetails().getHandle() == null) {
            return "";
        }
        String schemeSpecificPart = call.getDetails().getHandle().getSchemeSpecificPart();
        if (TextUtils.isEmpty(schemeSpecificPart)) {
            return "";
        }
        String replaceAll = schemeSpecificPart.replaceAll("-", "").replaceAll(" ", "");
        String str = replaceAll;
        if (replaceAll.startsWith("+82")) {
            str = "0" + replaceAll.substring(3);
        }
        return str;
    }
}
