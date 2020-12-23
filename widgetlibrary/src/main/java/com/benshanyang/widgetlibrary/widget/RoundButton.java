package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.benshanyang.widgetlibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述: 自定义圆角按钮 </br>
 * 时间: 2019/3/21  17:25
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class RoundButton extends AppCompatTextView {

    private float cornerRadius = 0.0f;//圆角半径
    private float borderWidth = 0.0f;//边框宽度
    private float topLeftRadius = 0.0f;//左上角圆角半径
    private float topRightRadius = 0.0f;//右上角圆角半径
    private float bottomRightRadius = 0.0f;//左下角圆角半径
    private float bottomLeftRadius = 0.0f;//右下角圆角半径
    private float dashWidth = 0.0f;//点划线的长度
    private float dashGap = 0.0f;//点划线间的缝隙宽度

    @ColorInt
    private int pressedBorderColor = Color.TRANSPARENT;//按下时边框颜色
    @ColorInt
    private int normalBorderColor = Color.TRANSPARENT;//未按下时边框颜色
    @ColorInt
    private int pressedBackgroundColor = Color.TRANSPARENT;//按下时背景色
    @ColorInt
    private int normalBackgroundColor = Color.TRANSPARENT;//未按下时背景色
    @ColorInt
    private int pressedTextColor = 0xFF333333;//按下时文字颜色
    @ColorInt
    private int normalTextColor = 0xFF333333;//未按下时文字颜色
    @ColorInt
    private int gradientStartColor = -1;//渐变开始颜色
    @ColorInt
    private int gradientCenterColor = -1;//渐变中间颜色
    @ColorInt
    private int gradientEndColor = -1;//渐变结尾颜色

    private int gradientOrientation = -1;//背景渐变的方向
    private int gradientType = -1;//背景渐变的类型
    private boolean isShowBorder = false;//是否显示边框
    private GradientDrawable pressedDrawable = null;//点击时背景样式
    private GradientDrawable normalDrawable = null;//默认背景样式

    public RoundButton(Context context) {
        super(context);
        init(context, null);
    }

    public RoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);//设置文字居中
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundButton);
            if (typedArray != null) {
                cornerRadius = typedArray.getDimension(R.styleable.RoundButton_cornerRadius, 0);//圆角半径
                topLeftRadius = typedArray.getDimension(R.styleable.RoundButton_topLeftRadius, 0);//左上角圆角半径
                topRightRadius = typedArray.getDimension(R.styleable.RoundButton_topRightRadius, 0);//右上角圆角半径
                bottomLeftRadius = typedArray.getDimension(R.styleable.RoundButton_bottomLeftRadius, 0);//左下角圆角半径
                bottomRightRadius = typedArray.getDimension(R.styleable.RoundButton_bottomRightRadius, 0);//右下角圆角半径
                borderWidth = typedArray.getDimension(R.styleable.RoundButton_borderWidth, 0);//边框宽度
                dashWidth = typedArray.getDimension(R.styleable.RoundButton_dashWidth, 0);//点划线的长度
                dashGap = typedArray.getDimension(R.styleable.RoundButton_dashGap, 0);//点划线间的缝隙宽度
                gradientOrientation = typedArray.getInt(R.styleable.RoundButton_gradientOrientation, -1);//背景渐变的方向
                gradientType = typedArray.getInt(R.styleable.RoundButton_gradientType, -1);//背景渐变的类型

                gradientStartColor = typedArray.getColor(R.styleable.RoundButton_android_startColor, -1);//渐变开始颜色
                gradientCenterColor = typedArray.getColor(R.styleable.RoundButton_android_centerColor, -1);//渐变中间颜色
                gradientEndColor = typedArray.getColor(R.styleable.RoundButton_android_endColor, -1);//渐变结尾颜色

                pressedBorderColor = typedArray.getColor(R.styleable.RoundButton_pressedBorderColor, Color.TRANSPARENT);//按下时边框颜色
                normalBorderColor = typedArray.getColor(R.styleable.RoundButton_normalBorderColor, Color.TRANSPARENT);//未按下时边框颜色
                pressedBackgroundColor = typedArray.getColor(R.styleable.RoundButton_pressedBackgroundColor, Color.TRANSPARENT);//按下时背景色
                normalBackgroundColor = typedArray.getColor(R.styleable.RoundButton_normalBackgroundColor, Color.TRANSPARENT);//未按下时背景色
                pressedTextColor = typedArray.getColor(R.styleable.RoundButton_pressedTextColor, 0xFF333333);//按下时文字颜色
                normalTextColor = typedArray.getColor(R.styleable.RoundButton_normalTextColor, 0xFF333333);//未按下时文字颜色
                isShowBorder = typedArray.getBoolean(R.styleable.RoundButton_isShowBorder, false);//是否显示边框
                typedArray.recycle();
            }
        }

        pressedDrawable = new GradientDrawable();
        normalDrawable = new GradientDrawable();
        pressedDrawable.setShape(GradientDrawable.RECTANGLE);
        normalDrawable.setShape(GradientDrawable.RECTANGLE);

        // 如果设定的有Orientation 就默认为是渐变色的Button，否则就是纯色的Button
        if (gradientOrientation != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                GradientDrawable.Orientation orientation = getOrientation(gradientOrientation);
                if (orientation != null) {
                    pressedDrawable.setOrientation(orientation);
                    normalDrawable.setOrientation(orientation);
                }
                int[] colors = getGradientColors();
                if (colors != null) {
                    pressedDrawable.setColors(colors);
                    normalDrawable.setColors(colors);
                } else {
                    pressedDrawable.setColor(pressedBackgroundColor);//默认背景色
                    normalDrawable.setColor(normalBackgroundColor);//按下时背景色
                }
            }
        } else {
            pressedDrawable.setColor(pressedBackgroundColor);//默认背景色
            normalDrawable.setColor(normalBackgroundColor);//按下时背景色
        }

        int type;
        switch (gradientType) {
            case 0:
                type = GradientDrawable.LINEAR_GRADIENT;
                break;
            case 1:
                type = GradientDrawable.RADIAL_GRADIENT;
                break;
            case 2:
                type = GradientDrawable.SWEEP_GRADIENT;
                break;
            default:
                type = -1;
                break;
        }
        if (type != -1) {
            pressedDrawable.setGradientType(type);
            normalDrawable.setGradientType(type);
        }

        pressedDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? pressedBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//按下时边框线颜色和线宽
        normalDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? normalBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//默认边框线颜色和线宽

        //圆角半径
        if (cornerRadius != 0) {
            normalDrawable.setCornerRadius(cornerRadius);
            pressedDrawable.setCornerRadius(cornerRadius);
        } else {
            //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            normalDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius});
            pressedDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius});
        }

        StateListDrawable stateListDrawable = new StateListDrawable();
        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, normalDrawable);
        //没有任何状态时显示的图片，就设置空集合，默认状态
        stateListDrawable.addState(new int[]{}, normalDrawable);
        setBackgroundDrawable(stateListDrawable);

        int[] colors = new int[]{pressedTextColor, normalTextColor, normalTextColor};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{-android.R.attr.state_enabled};
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        setTextColor(colorList);
    }

    /**
     * 是否显示边框
     *
     * @param isShow
     */
    public void setShowBorder(boolean isShow) {
        this.isShowBorder = isShow;
        resetStroke();
    }

    /**
     * 设置边框的宽度
     *
     * @param borderWidth 边框的宽度
     */
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        resetStroke();
    }

    /**
     * 设置点划线
     *
     * @param dashWidth 点划线的长度
     * @param dashGap   点划线间的缝隙宽度
     */
    public void setDash(float dashWidth, float dashGap) {
        this.dashWidth = dashWidth;
        this.dashGap = dashGap;
        resetStroke();
    }

    /**
     * 设置按钮的圆角半径
     *
     * @param cornerRadius 圆角半径
     */
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        normalDrawable.setCornerRadius(cornerRadius);
        pressedDrawable.setCornerRadius(cornerRadius);
    }

    /**
     * 设置按钮的是个圆角
     *
     * @param radii 圆角数组必须有4个值 第一个值为左上角 第二个值为右上角 第三个值为右下角 第四个值为左下角
     */
    public void setCornerRadii(@Nullable float[] radii) {
        if (radii != null) {
            if (radii.length != 4) {
                throw new ArrayIndexOutOfBoundsException("radii must have == 4 values");
            }

            topLeftRadius = radii[0];
            topRightRadius = radii[1];
            bottomRightRadius = radii[2];
            bottomLeftRadius = radii[3];
            //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            normalDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius});
            pressedDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius});
        }

    }

    /**
     * 设置点击时的边框颜色
     *
     * @param pressedBorderColor 边框的色值
     */
    public void setPressedBorderColor(@ColorInt int pressedBorderColor) {
        this.pressedBorderColor = pressedBorderColor;
        pressedDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? pressedBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//按下时边框线颜色和线宽
    }

    /**
     * 设置未点击时的边框颜色
     *
     * @param normalBorderColor 边框色值
     */
    public void setNormalBorderColor(@ColorInt int normalBorderColor) {
        this.normalBorderColor = normalBorderColor;
        normalDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? normalBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//默认边框线颜色和线宽
    }

    /**
     * 设置点击时的背景色
     *
     * @param pressedBackgroundColor 背景色值
     */
    public void setPressedBackgroundColor(@ColorInt int pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
        pressedDrawable.setColor(pressedBackgroundColor);//默认背景色
    }

    /**
     * 设置未点击时的背景颜色
     *
     * @param normalBackgroundColor 背景色值
     */
    public void setNormalBackgroundColor(@ColorInt int normalBackgroundColor) {
        this.normalBackgroundColor = normalBackgroundColor;
        normalDrawable.setColor(normalBackgroundColor);//按下时背景色
    }

    /**
     * 设置文字颜色
     *
     * @param normalTextColor  默认文字颜色的色值
     * @param pressedTextColor 按下时文字颜色的色值
     */
    public void setTextColor(@ColorInt int normalTextColor, @ColorInt int pressedTextColor) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;

        int[] colors = new int[]{pressedTextColor, normalTextColor, normalTextColor};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{-android.R.attr.state_enabled};
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        setTextColor(colorList);
    }

    private void resetStroke() {
        pressedDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? pressedBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//按下时边框线颜色和线宽
        normalDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? normalBorderColor : Color.TRANSPARENT, isShowBorder ? dashWidth : 0, isShowBorder ? dashGap : 0);//默认边框线颜色和线宽
    }

    private int[] getGradientColors() {
        int[] colors = null;
        List<Integer> gradientList = new ArrayList<>();
        if (gradientStartColor != -1) {
            gradientList.add(gradientStartColor);
        }
        if (gradientCenterColor != -1) {
            gradientList.add(gradientCenterColor);
        }
        if (gradientEndColor != -1) {
            gradientList.add(gradientEndColor);
        }
        if (!gradientList.isEmpty()) {
            colors = new int[gradientList.size()];
            for (int i = 0; i < gradientList.size(); i++) {
                colors[i] = gradientList.get(i);
            }
        }
        return colors;
    }

    private GradientDrawable.Orientation getOrientation(int gradientOrientation) {
        GradientDrawable.Orientation orientation = null;
        switch (gradientOrientation) {
            case 0:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 1:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 4:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 5:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case 6:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 7:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
        }
        return orientation;
    }
}
