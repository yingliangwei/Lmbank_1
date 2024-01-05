package com.example.lmbank.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.lmbank.R;
import com.example.lmbank.databinding.LayoutPullUpBinding;

public class PullUpView extends LinearLayout implements View.OnTouchListener {
    private final boolean isArrow;
    private final LayoutPullUpBinding binding;
    private float lastY;
    private Call call;
    private boolean is;
    private OnClickListener clickListener;

    @SuppressLint("ClickableViewAccessibility")
    public PullUpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullUpView);
        isArrow = typedArray.getBoolean(R.styleable.PullUpView_is_arrow, false);
        int color = typedArray.getColor(R.styleable.PullUpView_up_background, Color.RED);
        float rotation = typedArray.getFloat(R.styleable.PullUpView_up_rotation, 0f);
        binding = LayoutPullUpBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (isArrow) {
            binding.image.animate()
                    .translationY(-300)
                    .translationY(0)
                    .setDuration(300)
                    .start();
            binding.arrow.setVisibility(View.VISIBLE);
        }
        binding.image.setRotation(rotation);
        setGradientDrawable(binding.image, color);
        binding.getRoot().setOnTouchListener(this);
        typedArray.recycle();
    }

    public void setGradientDrawable(View view, int color) {
        // 创建一个GradientDrawable对象，并设置形状为椭圆
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        // 设置圆形的颜色
        shape.setColor(color);
        // 设置圆形的大小
        shape.setSize(150, 150);
        // 将染色后的GradientDrawable设置为View的背景
        view.setBackground(shape);
    }


    public void setOnClickListener(Call call, OnClickListener l) {
        this.call = call;
        clickListener = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float distanceY = event.getRawY() - lastY;
                float translationY = binding.image.getTranslationY() + distanceY;
                translationY = Math.max(-300, Math.min(translationY, 300));
                if (-translationY <= 200 && clickListener != null) {//触发
                    is = true;
                    clickListener.onClick(call, v);
                }
                if (isArrow) {
                    binding.arrow.setVisibility(GONE);
                }
                binding.image.setTranslationY(translationY);
                lastY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                if (is) {
                    return true;
                }
                if (isArrow) {
                    binding.arrow.setVisibility(VISIBLE);
                }
                binding.image.animate()
                        .translationY(0)
                        .setDuration(300)
                        .start();
                return true;
        }
        return false;
    }

    public interface OnClickListener {
        void onClick(Call call, View view);
    }
}
