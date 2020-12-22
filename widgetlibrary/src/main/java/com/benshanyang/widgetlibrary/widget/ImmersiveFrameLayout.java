package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * @ClassName: ImmersiveStatusBarLayout
 * @Description: 沉浸式状态栏父布局 全屏模式下使用该控件作为跟布局
 * @Author: YangKuan
 * @Date: 2020/11/13 16:34
 */
public class ImmersiveFrameLayout extends FrameLayout {
    private int[] mInsets = new int[4];

    public ImmersiveFrameLayout(Context context) {
        super(context);
    }

    public ImmersiveFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImmersiveFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }

        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            mInsets[1] = insets.getSystemWindowInsetTop();
            mInsets[2] = insets.getSystemWindowInsetRight();
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
