package com.example.lmbank.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.lmbank.R;
import com.example.lmbank.databinding.ActivityPhoneCallBinding;
import com.example.lmbank.databinding.LayoutFloatingBinding;
import com.example.lmbank.databinding.LayoutTelephoneBinding;
import com.example.lmbank.service.WindowActivity;
import com.example.lmbank.service.WindowFloating;
import com.example.lmbank.util.BlurUtil;
import com.example.lmbank.util.Handler;
import com.example.lmbank.util.PhoneUtil;
import com.example.lmbank.util.SharedPreferencesUtils;
import com.example.lmbank.widget.PullUpView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnswerView implements View.OnClickListener, Callback, com.example.lmbank.util.Handler.OnHandle {
    private final Call call;
    private final Context context;
    private Handler handler = new Handler(Looper.myLooper(), this);
    private long startTime;//接听电话时间戳
    private final WindowActivity windowActivity;//接听电话界面
    private final WindowFloating floating;//顶部悬浮窗接听电话
    private final LayoutFloatingBinding binding;//顶部悬浮窗接听电话
    private final String phone;//手机号码

    public AnswerView(Context context, String phone, Call call, WindowFloating floating, WindowActivity windowActivity) {
        this.call = call;
        this.phone = phone;
        this.context = context;
        this.windowActivity = windowActivity;
        this.floating = floating;
        floating.init(context);//初始化悬浮窗
        binding = LayoutFloatingBinding.inflate(LayoutInflater.from(context));
        floating.addView(binding.getRoot());
        initView();
        PhoneUtil.getPhoneData(PhoneUtil.getPhoneAll(phone), this);
    }

    private void initView() {
        initText(phone);
        initClick();
    }

    private void initClick() {
        binding.hangUp.setOnClickListener(this);//挂断
        binding.answer.setOnClickListener(this);//接听
        binding.root.setOnClickListener(this);
    }

    public void onDestroy() {
        handler = null;
    }

    private void initText(String phone) {
        String text = PhoneUtil.getOperatorName(phone);
        if (text != null) {
            binding.operators.setText(PhoneUtil.getOperatorName(phone));
            binding.operators.setVisibility(View.VISIBLE);
        }
        binding.phone.setText(phone);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.hangUp) {//挂断操作
            call.disconnect();
            floating.onDestroy();
        } else if (v.getId() == R.id.answer) {//接听电话
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
            floating.onDestroy();
            new Answer(true);//接听电话界面加载
        } else if (v.getId() == R.id.root) {//打开未接听
            floating.onDestroy();
            new Answer(false);//未接听电话界面加载
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
            binding.operators.setText(message);
            binding.operators.setVisibility(View.VISIBLE);
        }
    }

    private class Answer implements PullUpView.OnClickListener, View.OnClickListener, Runnable {
        private final ActivityPhoneCallBinding binding;
        private final boolean is;

        public Answer(boolean is) {
            this.is = is;
            windowActivity.init(context);//加载悬浮窗
            binding = ActivityPhoneCallBinding.inflate(LayoutInflater.from(context));
            windowActivity.addView(binding.getRoot());
            initView();
            IsAnswer();//判断是否已经接听
            new Thread(this).start();//加载高斯模糊背景图
        }

        private void IsAnswer() {
            if (is) {
                binding.pull.setVisibility(View.GONE);
                binding.answerF.setVisibility(View.VISIBLE);
                answer(binding.answerLayout);
            }
        }

        private void initView() {
            initText();
            initClick();
        }

        private void initText() {
            binding.phone.setText(phone);
        }

        private void initClick() {
            binding.hangUp.setOnClickListener(call, this);//接听
            binding.answer.setOnClickListener(call, this);//挂断
        }

        private void answer(LayoutTelephoneBinding binding) {
            initTime();//接听的时间
            initVisibility(binding);//隐藏布局操作
            initText(binding);
            initClick(binding);
        }

        private void initClick(LayoutTelephoneBinding binding) {
            binding.keyboardText.setOnClickListener(this);//键盘隐藏显示
            binding.hangUp.setOnClickListener(this);//挂断
        }

        private void initText(LayoutTelephoneBinding binding) {
            String operators = SharedPreferencesUtils.getString(context, "text", null);
            if (operators != null) {
                binding.operators.setText(operators);//运营商名
                binding.operators.setVisibility(View.VISIBLE);
            }
            binding.phone.setText(phone);
        }

        private void initVisibility(LayoutTelephoneBinding binding) {
            binding.keyboardLinear.setVisibility(View.GONE);
            binding.windowLinear.setVisibility(View.VISIBLE);
            binding.yes.setVisibility(View.GONE);
            binding.time.setVisibility(View.VISIBLE);
        }

        private void initTime() {
            startTime = System.currentTimeMillis();
            if (handler != null) {
                handler.postDelayed(new UpdateTimerRunnable(), 1000);
            }
        }

        @Override
        public void onClick(Call call, View view) {
            if (view.getId() == R.id.hangUp) {//挂断
                call.disconnect();
                windowActivity.onDestroy();
            } else if (view.getId() == R.id.answer) {//接听
                call.answer(VideoProfile.STATE_AUDIO_ONLY);
                binding.pull.setVisibility(View.GONE);
                binding.answerF.setVisibility(View.VISIBLE);
                answer(binding.answerLayout);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.keyboard_text) {//键盘显示隐藏
                LayoutTelephoneBinding binding = this.binding.answerLayout;
                if (binding.keyboardText.getText().equals("键盘")) {
                    binding.keyboardText.setText("功能");
                    binding.keyboardLinear.setVisibility(View.VISIBLE);
                    binding.windowLinear.setVisibility(View.GONE);
                } else {
                    binding.keyboardText.setText("键盘");
                    binding.windowLinear.setVisibility(View.VISIBLE);
                    binding.keyboardLinear.setVisibility(View.GONE);
                }
            } else if (v.getId() == R.id.hangUp) {//挂断
                call.disconnect();
                windowActivity.onDestroy();
            }
        }

        @Override
        public void run() {
            Drawable drawable = BlurUtil.applyBlur(context);
            binding.background.post(() -> binding.background.setBackground(drawable));
        }

        private class UpdateTimerRunnable implements Runnable {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long minutes = (elapsedTime / 1000) / 60;
                long seconds = (elapsedTime / 1000) % 60;
                String timeElapsedFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                binding.answerLayout.time.setText(timeElapsedFormatted);
                if (handler != null) {
                    handler.postDelayed(this, 1000);
                }
            }
        }
    }
}
