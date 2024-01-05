package com.example.lmbank.util;

import android.os.Looper;
import android.os.Message;

public class Handler extends android.os.Handler {
    private OnHandle handle;

    public Handler(Looper myLooper) {
        super(myLooper);
    }

    public Handler(Looper myLooper, OnHandle handle) {
        super(myLooper);
        this.handle = handle;
    }


    @Override
    public void handleMessage(Message msg) {
        handleMessage(msg.what, (String) msg.obj);
        if (handle != null) {
            handle.handleMessage(msg.what, (String) msg.obj);
        }
    }

    public void handleMessage(int what, String message) {

    }

    public void sendMessage(int what, String message) {
        Message message1 = new Message();
        message1.what = what;
        message1.obj = message;
        sendMessage(message1);
    }

    public interface OnHandle {
        default void handleMessage(int what, String message) {

        }
    }
}
