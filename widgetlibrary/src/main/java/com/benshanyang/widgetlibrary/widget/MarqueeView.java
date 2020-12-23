package com.benshanyang.widgetlibrary.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benshanyang.widgetlibrary.R;

import java.lang.ref.WeakReference;

/**
 * @ClassName: MarqueeView
 * @Description: 横向滚动文字跑马灯
 * @Author: YangKuan
 * @Date: 2019/11/21 11:18
 */
public class MarqueeView extends RelativeLayout {

    private float textSize = -1f;//文字大小
    private int textColor = 0xFF444444;//文字颜色
    private float drawablePadding = 0f;//文字和图片间的距离
    private int shadowColor = Color.WHITE;//文字两侧阴影的颜色
    private boolean isShowClose = false;//是否显示关闭按钮
    private boolean isShowNotice = true;//是否显示滚动文字前边的图标
    private Drawable noticeSrc = null;//文字左侧的图标
    private Drawable closeSrc = null;//关闭按钮的图标
    private CharSequence text = "";//内容

    private ImageView ivTrumpet;//左侧小喇叭图标
    private TextView tvNotice;//提示内容
    private ImageButton ivBtnClose;//右侧关闭图标
    private MarqueeHandler marqueeHandler;

    private static final int FRAMELAYOUT_TRUMPET_ID = 0x000001;//小喇叭父布局id
    private static final int IMAGEVIEW_TRUMPET_ID = 0x000002;//小喇叭图标id
    private static final int TEXTVIEW_NOTICE_ID = 0x000003;//内容id
    private static final int IMAGEBUTTON_CLOSE_ID = 0x000004;//右侧关闭按钮

    public MarqueeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MarqueeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
        init(context);
    }

    public MarqueeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        init(context);
    }

    //初始化xml属性
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0);
        if (typedArray != null) {
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.MarqueeView_android_textSize) {
                    //文字大小
                    textSize = typedArray.getDimension(attr, -1);
                } else if (attr == R.styleable.MarqueeView_android_textColor) {
                    //文字颜色
                    textColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.MarqueeView_android_background) {
                    //文字左右两侧的阴影
                    shadowColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.MarqueeView_isShowClose) {
                    //是否显示右侧按钮
                    isShowClose = typedArray.getBoolean(attr, false);
                } else if (attr == R.styleable.MarqueeView_isShowNotice) {
                    //是否显示滚动文字前边的图标
                    isShowNotice = typedArray.getBoolean(attr, true);
                } else if (attr == R.styleable.MarqueeView_noticeSrc) {
                    //文字左侧的图标
                    noticeSrc = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.MarqueeView_closeSrc) {
                    //关闭按钮图标
                    closeSrc = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.MarqueeView_android_text) {
                    //设置内容
                    text = typedArray.getString(attr);
                } else if (attr == R.styleable.MarqueeView_android_drawablePadding) {
                    //文字和图片间的距离
                    drawablePadding = typedArray.getDimension(attr, 0f);
                }
            }
            typedArray.recycle();
        }

    }

    private void init(Context context) {
        setBackgroundColor(shadowColor);
        //小喇叭父布局
        LayoutParams flTrumpetParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        FrameLayout flTrumpet = new FrameLayout(context);//小喇叭父布局
        flTrumpet.setPadding(0, 0, (int) drawablePadding, 0);
        flTrumpet.setId(FRAMELAYOUT_TRUMPET_ID);

        //小喇叭图标
        FrameLayout.LayoutParams ivTrumpetParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        ivTrumpet = new ImageView(context);//小喇叭图标
        //ivTrumpet.setImageResource(R.drawable.ic_trumpet);
        if (noticeSrc != null) {
            ivTrumpet.setImageDrawable(noticeSrc);
        }
        ivTrumpet.setId(IMAGEVIEW_TRUMPET_ID);
        flTrumpet.addView(ivTrumpet, ivTrumpetParams);
        flTrumpet.setVisibility(isShowNotice ? VISIBLE : GONE);

        //右侧关闭按钮
        LayoutParams ivBtnCloseParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        ivBtnCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ivBtnClose = new ImageButton(context);
        ivBtnClose.setPadding((int) drawablePadding, 0, 0, 0);
        ivBtnClose.setBackgroundColor(Color.TRANSPARENT);
        ivBtnClose.setId(IMAGEBUTTON_CLOSE_ID);
        //ivBtnClose.setImageResource(R.drawable.ic_close);
        if (closeSrc != null) {
            ivBtnClose.setImageDrawable(closeSrc);
        }
        ivBtnClose.setVisibility(isShowClose ? VISIBLE : GONE);
        ivBtnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });

        //文字内容
        LayoutParams ivNoticeParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ivNoticeParams.addRule(RelativeLayout.RIGHT_OF, FRAMELAYOUT_TRUMPET_ID);
        ivNoticeParams.addRule(RelativeLayout.LEFT_OF, IMAGEBUTTON_CLOSE_ID);
        tvNotice = new TextView(context);
        tvNotice.setSingleLine();
        tvNotice.setText(text);
        tvNotice.setTextColor(textColor);
        if (textSize != -1) {
            tvNotice.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        tvNotice.setGravity(Gravity.CENTER_VERTICAL);
        tvNotice.setId(TEXTVIEW_NOTICE_ID);

        //左阴影
        LayoutParams leftShadow = new LayoutParams(dp2px(10), LayoutParams.MATCH_PARENT);
        leftShadow.addRule(RelativeLayout.ALIGN_LEFT, TEXTVIEW_NOTICE_ID);
        View leftShadowView = new View(context);
        GradientDrawable leftShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{shadowColor, Color.TRANSPARENT});
        leftShadowDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        leftShadowView.setBackgroundDrawable(leftShadowDrawable);

        //右阴影
        LayoutParams rightShadow = new LayoutParams(dp2px(10), LayoutParams.MATCH_PARENT);
        rightShadow.addRule(RelativeLayout.ALIGN_RIGHT, TEXTVIEW_NOTICE_ID);
        View rightShadowView = new View(context);
        GradientDrawable rightShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, shadowColor});
        rightShadowDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        rightShadowView.setBackgroundDrawable(rightShadowDrawable);

        addView(flTrumpet, flTrumpetParams);//加入小喇叭的父布局
        addView(ivBtnClose, ivBtnCloseParams);//右侧关闭按钮
        addView(tvNotice, ivNoticeParams);//加入内容
        addView(leftShadowView, leftShadow);//左阴影
        addView(rightShadowView, rightShadow);//右阴影

        marqueeHandler = new MarqueeHandler(tvNotice, leftShadowView, rightShadowView);
        post(new Runnable() {
            @Override
            public void run() {
                if (marqueeHandler != null && !TextUtils.isEmpty(text)) {
                    marqueeHandler.sendEmptyMessageDelayed(0, 200);
                }
            }
        });
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility != getVisibility()) {
            if (visibility == VISIBLE) {
                if (marqueeHandler != null && !TextUtils.isEmpty(text)) {
                    marqueeHandler.sendEmptyMessageDelayed(0, 200);
                }
            } else {
                if (marqueeHandler != null) {
                    marqueeHandler.stopAnim();
                }
            }
        }
        super.setVisibility(visibility);
    }

    /**
     * 设置文字
     *
     * @param text 跑马灯文字
     */
    public void setText(CharSequence text) {
        this.text = text;
        if (TextUtils.isEmpty(text)) {
            marqueeHandler.stopAnim();
            tvNotice.setText(text);
        } else {
            if (tvNotice != null && marqueeHandler != null) {
                tvNotice.setText(text);
                marqueeHandler.stopAnim();
                marqueeHandler.sendEmptyMessageDelayed(0, 200);
            }
        }
    }

    /**
     * 设置字号
     *
     * @param spValue 跑马灯滚动文字的字号
     */
    public void setTextSize(float spValue) {
        if (tvNotice != null) {
            tvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
        }
    }

    /**
     * 设置字号
     *
     * @param unit 设置文字字号的单位 例如：TypedValue.COMPLEX_UNIT_SP
     * @param size 跑马灯滚动文字的字号
     */
    public void setTextSize(int unit, float size) {
        if (tvNotice != null) {
            tvNotice.setTextSize(unit, size);
        }
    }

    private static class MarqueeHandler extends Handler {

        private ValueAnimator animator;
        private WeakReference<TextView> textViewRef;
        private WeakReference<View> leftShadowRef;
        private WeakReference<View> rightShadowRef;
        private int speed = 50;//越小越快

        private MarqueeHandler(TextView view, View leftShadow, View rightShadow) {
            textViewRef = new WeakReference<>(view);
            leftShadowRef = new WeakReference<>(leftShadow);
            rightShadowRef = new WeakReference<>(rightShadow);
        }

        public void stopAnim() {
            if (animator != null && animator.isRunning()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    animator.pause();
                }
                animator.cancel();
                TextView view = textViewRef.get();
                if (view != null) {
                    view.scrollTo(view.getPaddingLeft(), 0);
                }
                View leftShadowView = leftShadowRef.get();
                View rightShadowView = rightShadowRef.get();
                if (leftShadowView != null) {
                    leftShadowView.setVisibility(View.GONE);
                }
                if (rightShadowView != null) {
                    rightShadowView.setVisibility(View.GONE);
                }

            }
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            final TextView view = textViewRef.get();
            View leftShadowView = leftShadowRef.get();
            View rightShadowView = rightShadowRef.get();

            if (view != null) {
                Paint paint = view.getPaint();
                final int textWidth = (int) (paint != null ? paint.measureText(view.getText().toString()) : 0);

                if (textWidth > view.getWidth()) {
                    if (leftShadowView != null) {
                        leftShadowView.setVisibility(View.VISIBLE);
                    }
                    if (rightShadowView != null) {
                        rightShadowView.setVisibility(View.VISIBLE);
                    }

                    if (msg.what == 0) {
                        animator = ValueAnimator.ofFloat(view.getPaddingLeft(), textWidth);
                        animator.setDuration((textWidth / 5) * speed);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                view.scrollTo((int) value, 0);
                                if (value >= (textWidth)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        animation.pause();
                                    }
                                    animation.cancel();
                                    sendEmptyMessageDelayed(1, 80);
                                }
                            }
                        });
                        animator.setInterpolator(new LinearInterpolator());
                        animator.start();

                    } else if (msg.what == 1) {
                        animator = ValueAnimator.ofFloat(-(view.getWidth() - view.getPaddingRight()), textWidth);
                        animator.setDuration((((view.getWidth() - view.getPaddingRight()) + textWidth) / 5) * speed);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                view.scrollTo((int) value, 0);
                            }
                        });
                        animator.setRepeatMode(ValueAnimator.RESTART);//播放完毕后从头播放
                        animator.setRepeatCount(ValueAnimator.INFINITE);//无线循环播放
                        animator.setInterpolator(new LinearInterpolator());
                        animator.start();
                    }
                } else {
                    if (leftShadowView != null) {
                        leftShadowView.setVisibility(View.GONE);
                    }
                    if (rightShadowView != null) {
                        rightShadowView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
