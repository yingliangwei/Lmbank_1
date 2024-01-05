package com.example.lmbank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PeopleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        startActivity(intent);
        finish();
    }
}
