package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.benshanyang.widgetlibrary.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * 类描述: 标题栏 </br>
 * 时间: 2019/3/20 10:50
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class TitleBarView extends FrameLayout {

    private View borderView;//底部边框
    private View titleLayout;//标题栏
    private ImageButton ibBack;//返回按钮
    private ImageButton ibActionBtn;//图片功能按钮
    private TextView tvTitle;//标题栏
    private TextView btnActionBtn;//文字功能按钮
    private FrameLayout flButton;//文字按钮父布局
    private FrameLayout flActionButton;//标题栏右侧功能按钮父布局

    private OnFinishListener onFinishListener;//点击返回按钮的回调接口
    private OnActionButtonClickListener onActionButtonClickListener;//右侧功能按钮点击事件的回调接口

    private float borderWidth = 0;//底边宽度
    private int borderColor = 0xFFD5D5D5;//底边颜色
    private boolean immersionStatusBar = false;//是否是沉浸式状态栏

    /**
     * 图片按钮
     */
    public static final int IMAGE_BUTTON = 0;
    /**
     * 文字按钮
     */
    public static final int TEXT_BUTTON = 1;

    public TitleBarView(Context context) {
        super(context);
        init(context, null);
    }

    public TitleBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        titleLayout = LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this, false);
        initView(titleLayout);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        if (typedArray != null) {
            String titleText = typedArray.getString(R.styleable.TitleBarView_titleText);//标题文字
            String btnStr = typedArray.getString(R.styleable.TitleBarView_buttonText);//文字按钮的文字
            ColorStateList titleTextColor = typedArray.getColorStateList(R.styleable.TitleBarView_titleTextColor);//标题文字颜色
            ColorStateList btnTextColor = typedArray.getColorStateList(R.styleable.TitleBarView_buttonTextColor);//文字按钮文字颜色
            borderColor = typedArray.getColor(R.styleable.TitleBarView_borderColor, 0xFFD5D5D5);//底边颜色
            int titleBarBgColor = typedArray.getColor(R.styleable.TitleBarView_titleBarBackgroundColor, 0);//标题栏背景色
            int titleTextSize = typedArray.getDimensionPixelSize(R.styleable.TitleBarView_titleTextSize, 0);//标题文字字号
            int btnTextSize = typedArray.getDimensionPixelSize(R.styleable.TitleBarView_buttonTextSize, 0);//文字按钮字号
            float btnDrawablePadding = typedArray.getDimension(R.styleable.TitleBarView_buttonDrawablePadding, 0);//文字按钮DrawablePadding
            borderWidth = typedArray.getDimension(R.styleable.TitleBarView_borderWidth, 0);//底边宽度
            float titleBarHeight = typedArray.getDimension(R.styleable.TitleBarView_titleBarHeight, -1);//标题栏高度
            ColorStateList backImageColor = typedArray.getColorStateList(R.styleable.TitleBarView_backImageColor);//返回按钮图片的颜色
            ColorStateList buttonDrawableColor = typedArray.getColorStateList(R.styleable.TitleBarView_buttonDrawableColor);//右侧功能按钮的颜色
            Drawable ibBackIcon = typedArray.getDrawable(R.styleable.TitleBarView_backImageSrc);//返回按钮图片
            Drawable ibIcon = typedArray.getDrawable(R.styleable.TitleBarView_buttonDrawable);//图片按钮或文字按钮的图片
            int btnType = typedArray.getInt(R.styleable.TitleBarView_buttonType, -1);//要显示哪种类型的功能按钮
            immersionStatusBar = typedArray.getBoolean(R.styleable.TitleBarView_immersionStatusBar, false);//沉浸式状态栏
            int finishActivityVisibility = typedArray.getInt(R.styleable.TitleBarView_finishActivityVisibility, -1);//是否显示标题栏返回按钮
            int actionButtonVisibility = typedArray.getInt(R.styleable.TitleBarView_actionButtonVisibility, -1);//是否显示标题栏右侧功能按钮

            if (tvTitle != null) {
                //设置标题
                tvTitle.setText(TextUtils.isEmpty(titleText) ? "" : titleText);
                //设置标题文字颜色
                if (titleTextColor != null) {
                    tvTitle.setTextColor(titleTextColor);
                }
                //设置标题字号
                if (titleTextSize != 0) {
                    tvTitle.setTextSize(COMPLEX_UNIT_PX, titleTextSize);
                }
            }

            if (ibBack != null) {
                if (ibBackIcon != null) {
                    if (backImageColor != null) {
                        //设置返回按钮的颜色
                        Drawable wrappedDrawable = DrawableCompat.wrap(ibBackIcon);
                        DrawableCompat.setTintList(wrappedDrawable, backImageColor);
                        ibBack.setImageDrawable(wrappedDrawable);
                    } else {
                        //设置返回按钮图片
                        ibBack.setImageDrawable(ibBackIcon);
                    }
                } else {
                    //设置返回按钮的颜色
                    if (backImageColor != null) {
                        ibBack.setColorFilter(backImageColor.getDefaultColor());
                    }
                }

            }

            if (ibActionBtn != null) {
                if (ibIcon != null) {
                    if (buttonDrawableColor != null) {
                        //右侧功能按钮的图片颜色
                        Drawable wrappedDrawable = DrawableCompat.wrap(ibIcon);
                        DrawableCompat.setTintList(wrappedDrawable, buttonDrawableColor);
                        ibActionBtn.setImageDrawable(wrappedDrawable);
                    } else {
                        //图片按钮
                        ibActionBtn.setImageDrawable(ibIcon);
                    }
                } else {
                    //右侧功能按钮的图片颜色
                    if (buttonDrawableColor != null) {
                        ibActionBtn.setColorFilter(buttonDrawableColor.getDefaultColor());
                    }
                }

            }

            //文字按钮
            if (btnActionBtn != null) {
                //设置文字按钮的icon
                if (ibIcon != null) {
                    if (buttonDrawableColor != null) {
                        //右侧功能按钮的图片颜色
                        Drawable wrappedDrawable = DrawableCompat.wrap(ibIcon);
                        DrawableCompat.setTintList(wrappedDrawable, buttonDrawableColor);
                        btnActionBtn.setCompoundDrawablesWithIntrinsicBounds(null, wrappedDrawable, null, null);
                    } else {
                        btnActionBtn.setCompoundDrawablesWithIntrinsicBounds(null, ibIcon, null, null);
                    }
                    btnActionBtn.setCompoundDrawablePadding((int) btnDrawablePadding);
                }
                btnActionBtn.setText(TextUtils.isEmpty(btnStr) ? "" : btnStr);
                if (btnTextColor != null) {
                    btnActionBtn.setTextColor(btnTextColor);//设置文字按钮的字体颜色
                }
                if (btnTextSize != 0)
                    btnActionBtn.setTextSize(COMPLEX_UNIT_PX, btnTextSize);//设置文字按钮的字体大小
            }

            //设置标题栏高度
            if (titleBarHeight != -1) {
                titleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) titleBarHeight));
            }

            //设置标题栏背景色
            if (titleBarBgColor != 0) {
                titleLayout.setBackgroundColor(titleBarBgColor);
            }

            if (ibActionBtn != null && btnActionBtn != null) {
                switch (btnType) {
                    case 0:
                        //图片按钮
                        ibActionBtn.setVisibility(VISIBLE);
                        btnActionBtn.setVisibility(INVISIBLE);
                        break;
                    case 1:
                        //文字按钮
                        ibActionBtn.setVisibility(INVISIBLE);
                        btnActionBtn.setVisibility(VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            //是否显示标题栏返回按钮
            if (ibBack != null) {
                switch (finishActivityVisibility) {
                    case 0:
                        ibBack.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        ibBack.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }

            //是否显示右侧功能按钮
            if (flActionButton != null) {
                switch (actionButtonVisibility) {
                    case 0:
                        flActionButton.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        flActionButton.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }

            typedArray.recycle();
        }

        LinearLayout.LayoutParams borderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) borderWidth);
        borderView.setLayoutParams(borderParams);//设置底边框的高度
        borderView.setBackgroundColor(borderColor);//设置底边框的颜色

        if (immersionStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPadding(getPaddingLeft(), getPaddingTop() + getStatusBarHeight(getContext()), getPaddingRight(), getPaddingBottom());
        } else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }

        initListener();
        addView(titleLayout);
    }

    /**
     * 初始化标题栏控件
     *
     * @param view
     */
    private void initView(View view) {
        //返回按钮
        ibBack = (ImageButton) view.findViewById(R.id.ib_finish_activity);
        //标题栏
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        //图片功能按钮
        ibActionBtn = (ImageButton) view.findViewById(R.id.ib_image_button);
        //文字功能按钮
        btnActionBtn = (TextView) view.findViewById(R.id.btn_button);
        //文字按钮父布局
        flButton = (FrameLayout) view.findViewById(R.id.fl_button);
        //标题栏右侧功能按钮父布局
        flActionButton = (FrameLayout) view.findViewById(R.id.fl_action_button);
        //底部边框
        borderView = view.findViewById(R.id.border_view);
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        //点击返回按钮
        if (ibBack != null) {
            ibBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onFinishListener != null) onFinishListener.onFinish(v);
                }
            });
        }

        //图片按钮
        if (ibActionBtn != null) {
            ibActionBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionButtonClickListener != null) onActionButtonClickListener.onClick(v);
                }
            });
        }

        //文字按钮
        if (flButton != null) {
            flButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionButtonClickListener != null) onActionButtonClickListener.onClick(v);
                }
            });
        }
    }

    /**
     * 设置返回按钮的点击回调
     *
     * @param onFinishListener 返回按钮点击的回调接口
     */
    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    /**
     * 设置右侧功能按钮的点击回调
     *
     * @param onActionButtonClickListener 右侧功能按钮的点击回调接口
     */
    public void setOnActionButtonClickListener(OnActionButtonClickListener onActionButtonClickListener) {
        this.onActionButtonClickListener = onActionButtonClickListener;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(CharSequence title) {
        if (tvTitle != null && title != null) {
            tvTitle.setText(title);
        }
    }

    /**
     * 设置标题文字大小
     *
     * @param size 标题文字大小
     */
    public void setTitleTextSize(float size) {
        if (tvTitle != null) {
            tvTitle.setTextSize(COMPLEX_UNIT_PX, size);
        }
    }

    /**
     * 设置标题文字颜色
     *
     * @param color 标题文字颜色
     */
    public void setTitleTextColor(@ColorInt int color) {
        if (tvTitle != null) {
            tvTitle.setTextColor(color);
        }
    }

    /**
     * 设置文字按钮的文字提示
     *
     * @param text 右侧文字按钮的文字
     */
    public void setButtonText(CharSequence text) {
        if (btnActionBtn != null && text != null) {
            btnActionBtn.setText(text);
        }
    }

    /**
     * 设置文字按钮的文字大小
     *
     * @param size 右侧文字按钮的字体字号
     */
    public void setButtonTextSize(float size) {
        if (btnActionBtn != null) {
            btnActionBtn.setTextSize(COMPLEX_UNIT_PX, size);
        }
    }

    /**
     * 设置文字按钮的文字颜色
     *
     * @param color 右侧文字按钮的文字颜色
     */
    public void setButtonTextColor(@ColorInt int color) {
        if (btnActionBtn != null) {
            btnActionBtn.setTextColor(color);
        }
    }

    /**
     * 设置文字按钮的icon
     *
     * @param drawable        drawable图片
     * @param drawablePadding 图片和文字的间距
     */
    public void setButtonDrawable(Drawable drawable, int drawablePadding) {
        if (btnActionBtn != null && drawable != null) {
            btnActionBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            btnActionBtn.setCompoundDrawablePadding(drawablePadding);
        }
    }

    /**
     * 设置文字按钮的icon
     *
     * @param resId           图片的资源id
     * @param drawablePadding 图片和文字的间距
     */
    public void setButtonDrawable(int resId, int drawablePadding) {
        if (btnActionBtn != null) {
            Drawable imgDrawable = getContext().getResources().getDrawable(resId);//获取资源图片
            btnActionBtn.setCompoundDrawablesWithIntrinsicBounds(null, imgDrawable, null, null);
            btnActionBtn.setCompoundDrawablePadding(drawablePadding);
        }
    }

    /**
     * 设置图片按钮的图片
     *
     * @param resId 图片按钮的图片资源id
     */
    public void setImageButtonSrc(@DrawableRes int resId) {
        if (ibActionBtn != null) {
            ibActionBtn.setImageResource(resId);
        }
    }

    /**
     * 设置图片按钮的图片
     *
     * @param drawable 图片按钮的Drawable图片
     */
    public void setImageButtonSrc(@Nullable Drawable drawable) {
        if (ibActionBtn != null) {
            ibActionBtn.setImageDrawable(drawable);
        }
    }

    /**
     * 设置底边
     *
     * @param width 底边粗细
     * @param color 底边颜色
     */
    public void setBottomBorder(int width, int color) {
        borderWidth = width;
        borderColor = color;
        invalidate();
    }

    /**
     * 设置标题栏高度
     *
     * @param height 标题栏的高度(不包含状态栏的高度)
     */
    public void setTitleBarHeight(int height) {
        if (titleLayout != null) {
            titleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
        }
    }

    /**
     * 设置标题栏的背景色
     *
     * @param color 标题栏的背景色
     */
    public void setTitleBarBackgroundColor(int color) {
        if (titleLayout != null) {
            titleLayout.setBackgroundColor(color);
        }
    }

    /**
     * 设置标题栏背景样式
     *
     * @param drawable 设置标题栏的背景样式
     */
    public void setTitleBarBackground(Drawable drawable) {
        if (titleLayout != null) {
            titleLayout.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 设置返回按钮的图片
     *
     * @param drawable 返回按钮的图片Drawable
     */
    public void setBackButtonSrc(@Nullable Drawable drawable) {
        if (ibBack != null) {
            ibBack.setImageDrawable(drawable);
        }
    }

    /**
     * 设置返回按钮的图片
     *
     * @param resId 返回按钮的图片资源id
     */
    public void setBackButtonSrc(@DrawableRes int resId) {
        if (ibBack != null) {
            ibBack.setImageResource(resId);
        }
    }

    /**
     * 设置要显示那种类型的右侧功能按钮
     *
     * @param type Type.IMAGE_BUTTON - 图片按钮,Type.TEXT_BUTTON - 文字按钮
     */
    public void setButtonType(@Type int type) {
        switch (type) {
            case IMAGE_BUTTON:
                //图片按钮
                ibActionBtn.setVisibility(VISIBLE);
                btnActionBtn.setVisibility(INVISIBLE);
                break;
            case TEXT_BUTTON:
                //文字按钮
                ibActionBtn.setVisibility(INVISIBLE);
                btnActionBtn.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 设置标题栏左侧返回按钮是否显示
     *
     * @param visibility 入参为: View.VISIBLE、View.INVISIBLE、View.GONE
     */
    public void setFinishActivityVisibility(int visibility) {
        if (ibBack != null && (visibility == View.VISIBLE || visibility == View.INVISIBLE)) {
            ibBack.setVisibility(visibility);
        }
    }

    /**
     * 设置标题栏右侧功能按钮是否显示
     *
     * @param visibility 入参为: View.VISIBLE、View.INVISIBLE、View.GONE
     */
    public void setActionButtonVisibility(int visibility) {
        if (flActionButton != null && (visibility == View.VISIBLE || visibility == View.INVISIBLE)) {
            flActionButton.setVisibility(visibility);
        }
    }

    /**
     * 沉浸式状态栏
     *
     * @param flag true设置为沉浸式状态栏样式(Android 5.0以后才有效),false非沉浸式状态栏样式
     */
    public void setImmersionStateBar(boolean flag) {
        immersionStatusBar = flag;
        if (immersionStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPadding(getPaddingLeft(), getPaddingTop() + getStatusBarHeight(getContext()), getPaddingRight(), getPaddingBottom());
        } else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    /**
     * 返回按钮的回调接口
     */
    public interface OnFinishListener {
        void onFinish(View view);
    }

    /**
     * 右侧功能按钮点击事件
     */
    public interface OnActionButtonClickListener {
        void onClick(View view);
    }

    /**
     * 设置右侧按钮类型
     */
    @IntDef({IMAGE_BUTTON, TEXT_BUTTON})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Type {
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        if (TextUtils.equals(Build.MANUFACTURER.toLowerCase(), "xiaomi")) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
            return 0;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            if (x > 0) {
                return context.getResources().getDimensionPixelSize(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
