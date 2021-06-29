package com.benshanyang.widgetlibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * @ClassName: TitleBar
 * @Description: 标题栏父布局
 * @Author: 杨宽
 * @Date: 2019/2/16 0016 15:04
 */
public class TitleBarLayout extends LinearLayout {

    private static final int spaceId = 0x1234;
    private View view = null;

    public TitleBarLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TitleBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view = findViewById(spaceId);
            if (view == null) {
                view = new TextView(context);
                view.setId(spaceId);
            }
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(view);

            int height = getStatusBarHeight(getContext());
            if (height < 0) {
                try {
                    ((Activity) context).findViewById(android.R.id.content).setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                        @Override
                        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                            int statusBarH = insets.getSystemWindowInsetTop();
                            if (view != null) {
                                LayoutParams titleParams = (LayoutParams) view.getLayoutParams();
                                titleParams.height = statusBarH;
                                view.setLayoutParams(titleParams);
                            }
                            return insets;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (view != null) {
                    LayoutParams titleParams = (LayoutParams) view.getLayoutParams();
                    titleParams.height = height;
                    view.setLayoutParams(titleParams);
                }
            }
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        int statusBarH = 0;
        if (TextUtils.equals(Build.MANUFACTURER.toLowerCase(), "xiaomi")) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarH = context.getResources().getDimensionPixelSize(resourceId);
            }
        } else {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                if (x > 0) {
                    statusBarH = context.getResources().getDimensionPixelSize(x);
                }
            } catch (Exception e) {
                statusBarH = -1;
            }
        }
        return statusBarH;
    }

}
