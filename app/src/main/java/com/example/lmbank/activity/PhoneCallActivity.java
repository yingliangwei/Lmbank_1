package com.example.lmbank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lmbank.databinding.ActivityPhoneCallBinding;


public class PhoneCallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.lmbank.databinding.ActivityPhoneCallBinding binding = ActivityPhoneCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
