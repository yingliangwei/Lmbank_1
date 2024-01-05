package com.example.lmbank.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lmbank.R;
import com.example.lmbank.databinding.ActivityPhoneCallBinding;
import com.example.lmbank.databinding.LayoutTelephoneBinding;
import com.example.lmbank.service.WindowActivity;
import com.example.lmbank.util.BlurUtil;
import com.example.lmbank.util.Handler;
import com.example.lmbank.util.PhoneUtil;
import com.example.lmbank.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CallPhoneView implements Runnable, View.OnClickListener, Callback, Handler.OnHandle {
    private final Call call;
    private final WindowActivity windowActivity;
    private final InCallService context;
    private final LayoutTelephoneBinding binding;
    private final RelativeLayout layout;
    private final String phone;
    private Handler handler = new Handler(Looper.myLooper(), this);
    private long startTime;
    private final TextView recording_text;
    private long startTimeRecording;

    public CallPhoneView(InCallService context, String phone, Call call, WindowActivity windowActivity) {
        this.call = call;
        this.phone = phone;
        this.windowActivity = windowActivity;
        this.context = context;
        windowActivity.init(context.getApplicationContext());
        ActivityPhoneCallBinding binding = ActivityPhoneCallBinding.inflate(LayoutInflater.from(context));
        windowActivity.addView(binding.getRoot());
        this.layout = binding.background;
        this.binding = binding.answerLayout;
        recording_text = this.binding.keyboardText;
        initView();
        initAnswerF(binding);
        initRun();
    }

    private void initRun() {
        PhoneUtil.getPhoneData(PhoneUtil.getPhoneAll(phone), this);
        new Thread(this).start();
    }

    private void initAnswerF(ActivityPhoneCallBinding binding) {
        binding.pull.setVisibility(View.GONE);
        binding.answerF.setVisibility(View.VISIBLE);
    }

    private void initView() {
        initText();
        initTab();
        initClick();
    }

    private void initClick() {
        binding.hangUp.setOnClickListener(this);
        binding.speakerphone.setOnClickListener(this);
        binding.keyboardText.setOnClickListener(this);
        binding.window.recording.setOnClickListener(this);
    }

    public void onDestroy() {
        handler = null;
    }

    private void initTab() {
        binding.speakerphone.setTag("no");
        binding.window.recording.setTag("no");
    }

    private void initText() {
        String text = PhoneUtil.getOperatorName(phone);
        if (text != null) {
            binding.operators.setText(PhoneUtil.getOperatorName(phone));
            binding.operators.setVisibility(View.VISIBLE);
        }
        binding.phone.setText(phone);
    }

    @Override
    public void run() {
        Drawable drawable = BlurUtil.applyBlur(context);
        layout.post(() -> layout.setBackground(drawable));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hangUp) {
            call.disconnect();
            windowActivity.onDestroy();
        } else if (v.getId() == R.id.speakerphone) {
            if (v.getTag().equals("no")) {
                context.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
                binding.speakerphone.setTextColor(Color.BLUE);
                binding.speakerphone.setTag("yes");
            } else {
                context.setAudioRoute(CallAudioState.ROUTE_EARPIECE);
                binding.speakerphone.setTextColor(Color.WHITE);
                binding.speakerphone.setTag("no");
            }
        } else if (v.getId() == R.id.keyboard_text) {
            if (binding.keyboardText.getText().equals("键盘")) {
                binding.keyboardText.setText("功能");
                binding.keyboardLinear.setVisibility(View.VISIBLE);
                binding.windowLinear.setVisibility(View.GONE);
            } else {
                binding.keyboardText.setText("键盘");
                binding.windowLinear.setVisibility(View.VISIBLE);
                binding.keyboardLinear.setVisibility(View.GONE);
            }
        } else if (v.getId() == R.id.recording) {
            if (binding.window.recording.getTag().equals("no")) {
                startTimeRecording = System.currentTimeMillis();
                binding.window.recordingImage.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                binding.window.recordingText.setTextColor(Color.BLUE);
                if (handler != null) {
                    handler.postDelayed(new Recording(), 1000); // 每隔1秒更新一次计时器
                }
                binding.window.recording.setTag("yes");
            } else {
                binding.window.recordingImage.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                binding.window.recordingText.setTextColor(Color.WHITE);
                binding.window.recordingText.setText("录音");
                binding.window.recording.setTag("no");
            }
        }
    }

    public void initTime() {
        binding.keyboardText.setText("键盘");
        binding.yes.setVisibility(View.GONE);
        binding.time.setVisibility(View.VISIBLE);
        binding.keyboardLinear.setVisibility(View.GONE);
        binding.windowLinear.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
        if (handler != null) {
            handler.postDelayed(new UpdateTimerRunnable(), 1000);
        }
    }

    @Override
    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
    }

    @Override
    public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            String json = body.string();
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String province = data.getString("province");
                    String city = data.getString("city");
                    String sp = data.getString("sp");
                    String text = String.format("%s%s %s", province, city, sp);
                    if (handler != null) {
                        handler.sendMessage(0, text);
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public void handleMessage(int what, String message) {
        if (what == 0) {
            if (message.length() == 1) {
                return;
            }
            binding.operators.setText(message);
            binding.operators.setVisibility(View.VISIBLE);
        }
    }

    private class Recording implements Runnable {

        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTimeRecording;
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            String timeElapsedFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            recording_text.setText(timeElapsedFormatted);
            if (handler != null) {
                handler.postDelayed(this, 1000);
            }
        }
    }

    private class UpdateTimerRunnable implements Runnable {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            String timeElapsedFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            binding.time.setText(timeElapsedFormatted);
            if (handler != null) {
                handler.postDelayed(this, 1000);
            }
        }
    }
}
