package com.example.lmbank.service;

import android.database.ContentObserver;
import android.os.Handler;

import com.example.lmbank.util.PhoneUtil;
import com.example.lmbank.util.SharedPreferencesUtils;

public class CallLogObserver extends ContentObserver {
    private final PhoneCallService phoneCallService;

    public CallLogObserver(PhoneCallService service, Handler handler) {
        super(handler);
        this.phoneCallService = service;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        try {
            String phone_a = SharedPreferencesUtils.getString(phoneCallService.getApplicationContext(), "phone", phoneCallService.phone);
            PhoneUtil.update(phoneCallService, phoneCallService.getContentResolver(), phoneCallService.phone, phone_a);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
