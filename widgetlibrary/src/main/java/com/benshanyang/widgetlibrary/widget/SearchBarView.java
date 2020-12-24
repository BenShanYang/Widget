package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.benshanyang.widgetlibrary.R;

/**
 * 类描述: 搜索框 </br>
 * 时间: 2019/4/8 17:24
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class SearchBarView extends LinearLayout {

    private EditText editText;
    private ImageButton imageButton;

    @ColorInt
    private int backgroundColor = Color.TRANSPARENT;//背景色 等级最高
    @ColorInt
    private int focusedBackgroundColor = Color.TRANSPARENT;//获取焦点时的背景色
    @ColorInt
    private int borderColor = Color.TRANSPARENT;//边框颜色
    @ColorInt
    private int focusedBorderColor = Color.TRANSPARENT;//获取焦点时的颜色
    private int maxLength = Integer.MAX_VALUE;//最大输入字数
    private boolean isShowBorder = false;//是否显示边框
    private boolean isShowActionButton = false;//是否显示最右侧的按钮
    private float textSize;//输入框文字字号
    private float editIconPadding;//左侧图片和文字的间距
    private float cornerRadius = 0f;//圆角半径
    private float borderWidth = 0f;//边框的宽度

    private int hintId = -1;
    private int textId = -1;
    private int inputType = -1;//输入类型
    private String digits = "";//过滤条件
    private CharSequence hint = "";//提示文字
    private CharSequence text = "";//内容文字
    private ColorStateList textColor;//文字颜色
    private ColorStateList textColorHint;//提示文字颜色
    private Drawable actionIcon;//右侧功能按钮图片
    private Drawable editIcon;//左侧图片Icon

    private InputFilter inputFilter;//过滤器
    private GradientDrawable bgDrawable;//默认的背景样式
    private TextWatcher textWatchListener;//输入监听
    private OnFocusChangeListener onFocusChangeListener;//输入框的焦点改变监听
    private OnTextChangedListener onTextChangedListener;//内容的改变监听

    public SearchBarView(Context context) {
        super(context);
        initWidget(context);
        initSetting(context);
    }

    public SearchBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initWidget(context);
        initAttr(context, attrs);
        initSetting(context);
    }

    public SearchBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget(context);
        initAttr(context, attrs);
        initSetting(context);
    }

    private void initWidget(Context context) {
        textSize = sp2px(14f);
        setOrientation(HORIZONTAL);

        editText = new EditText(context);
        imageButton = new ImageButton(context);

        editText.setPadding(dp2px(1), 0, dp2px(1), 0);
        editText.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(R.drawable.ic_clear);

        FrameLayout actionLayout = new FrameLayout(context);
        actionLayout.addView(new View(context), new FrameLayout.LayoutParams(dp2px(16), FrameLayout.LayoutParams.MATCH_PARENT, android.view.Gravity.CENTER));
        actionLayout.addView(imageButton, new FrameLayout.LayoutParams(dp2px(40), FrameLayout.LayoutParams.MATCH_PARENT, android.view.Gravity.CENTER));

        LayoutParams editParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        editParams.weight = 1;
        addView(editText, editParams);
        addView(actionLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        bgDrawable = new GradientDrawable();
        bgDrawable.setShape(GradientDrawable.RECTANGLE);
        setBackgroundDrawable(bgDrawable);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBarView, 0, 0);
        if (typedArray != null) {
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.SearchBarView_android_hint) {
                    //提示文字
                    hint = typedArray.getText(attr);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        hintId = typedArray.getResourceId(attr, Resources.ID_NULL);
                    }
                } else if (attr == R.styleable.SearchBarView_android_text) {
                    //设置内容文字
                    text = typedArray.getText(attr);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        textId = typedArray.getResourceId(attr, Resources.ID_NULL);
                    }
                } else if (attr == R.styleable.SearchBarView_android_textColor) {
                    //设置文字颜色
                    textColor = typedArray.getColorStateList(attr);
                } else if (attr == R.styleable.SearchBarView_android_textColorHint) {
                    //设置提示文字颜色
                    textColorHint = typedArray.getColorStateList(attr);
                } else if (attr == R.styleable.SearchBarView_android_textSize) {
                    //设置文字字号
                    textSize = typedArray.getDimensionPixelSize(attr, sp2px(14f));
                } else if (attr == R.styleable.SearchBarView_actionIcon) {
                    //右侧功能按钮图片
                    actionIcon = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.SearchBarView_editIcon) {
                    //左侧图片Icon
                    editIcon = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.SearchBarView_editIconPadding) {
                    //左侧图片和文字的间距
                    editIconPadding = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.SearchBarView_isShowBorder) {
                    //是否显示边框
                    isShowBorder = typedArray.getBoolean(attr, false);
                } else if (attr == R.styleable.SearchBarView_android_maxLength) {
                    //最大输入字数
                    maxLength = typedArray.getInt(attr, Integer.MAX_VALUE);
                } else if (attr == R.styleable.SearchBarView_android_digits) {
                    //输入的过滤条件
                    digits = typedArray.getString(attr);
                } else if (attr == R.styleable.SearchBarView_cornerRadius) {
                    //圆角
                    cornerRadius = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.SearchBarView_backgroundColor) {
                    //背景色
                    backgroundColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.SearchBarView_focusedBackgroundColor) {
                    //获取焦点时的背景色
                    focusedBackgroundColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.SearchBarView_borderColor) {
                    //边框颜色
                    borderColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.SearchBarView_focusedBorderColor) {
                    //获取焦点时的边框颜色
                    focusedBorderColor = typedArray.getColor(attr, Color.TRANSPARENT);
                } else if (attr == R.styleable.SearchBarView_borderWidth) {
                    //边框宽度
                    borderWidth = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.SearchBarView_isShowActionButton) {
                    //是否显示最右侧Button
                    isShowActionButton = typedArray.getBoolean(attr, false);
                } else if (attr == R.styleable.SearchBarView_android_inputType) {
                    //搜索框输入类型
                    inputType = typedArray.getInt(attr, -1);
                }
            }
            typedArray.recycle();
        }
    }

    private void initSetting(Context context) {
        inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                if (!TextUtils.isEmpty(charSequence)) {
                    String inputStr = charSequence.toString();
                    if (TextUtils.isEmpty(digits)) {
                        //如果没有匹配条件就不去匹配
                        return null;
                    } else if (inputStr.matches(digits)) {
                        //设置了匹配条件 且符合匹配条件
                        return null;
                    } else {
                        //设置了匹配条件但不符合匹配条件
                        return "";
                    }
                } else {
                    //输入的为空则过滤掉
                    return "";
                }
            }
        };

        if (editText != null) {
            if (inputType != -1) {
                editText.setInputType(inputType);
            }
            editText.setSingleLine();
            editText.setGravity(android.view.Gravity.CENTER_VERTICAL);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            if (textColor != null) {
                editText.setTextColor(textColor);
            }
            if (textColorHint != null) {
                editText.setHintTextColor(textColorHint);
            }
            setDrawable(editText, null, (int) editIconPadding, editIcon, null, null, null);

            //设置过滤器
            editText.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});

            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (bgDrawable != null) {
                        bgDrawable.setColor(hasFocus ? focusedBackgroundColor : backgroundColor);
                        bgDrawable.setStroke((int) borderWidth, hasFocus ? focusedBorderColor : borderColor);
                    }
                    //焦点改变的监听回调接口
                    if (onFocusChangeListener != null) {
                        onFocusChangeListener.onFocusChange(v, hasFocus);
                    }
                }
            });

            //输入内容改变时的监听
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (textWatchListener != null) {
                        textWatchListener.beforeTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (textWatchListener != null) {
                        textWatchListener.onTextChanged(s, start, before, count);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (textWatchListener != null) {
                        textWatchListener.afterTextChanged(s);
                    }
                    if (onTextChangedListener != null) {
                        onTextChangedListener.onChanged(s, editText, imageButton);
                    }
                }
            });

            if (hintId != -1 && hintId != Resources.ID_NULL) {
                editText.setHint(hintId);
            }
            if (!TextUtils.isEmpty(hint)) {
                editText.setHint(hint);
            }
            if (textId != -1 && textId != Resources.ID_NULL) {
                editText.setText(textId);
            }
            if (!TextUtils.isEmpty(text)) {
                editText.setText(text);
            }

            CharSequence contentStr = editText.getText();
            if (!TextUtils.isEmpty(contentStr)) {
                editText.setSelection(contentStr.length());
            }
        }

        if (imageButton != null) {
            if (actionIcon != null) {
                imageButton.setImageDrawable(actionIcon);
            }
            imageButton.setVisibility(isShowActionButton ? VISIBLE : GONE);
        }

        if (bgDrawable != null) {
            bgDrawable.setCornerRadius(cornerRadius);//设置背景圆角半径
            bgDrawable.setColor(backgroundColor);//设置背景
            bgDrawable.setStroke(isShowBorder ? (int) borderWidth : 0, isShowBorder ? borderColor : Color.TRANSPARENT);//设置边框的宽度和颜色和是否显示边框
        }

    }

    /**
     * 输入类型
     *
     * @param type 输入的类型 例如：InputType.TYPE_CLASS_TEXT
     * @see android.text.InputType 输入类型入参
     */
    public void setInputType(int type) {
        this.inputType = type;
        if (editText != null) {
            editText.setInputType(inputType);
        }
    }

    /**
     * 是否显示最右侧的按钮
     *
     * @param showActionButton true显示，false不显示
     */
    public void setShowActionButton(boolean showActionButton) {
        this.isShowActionButton = showActionButton;
        if (imageButton != null) {
            imageButton.setVisibility(isShowActionButton ? VISIBLE : GONE);
        }
    }

    /**
     * 设置搜索框背景色
     *
     * @param backgroundColor 背景色优先级最高 设置BackgroundColor后NormalBackgroundColor和FocusedBackgroundColor不起作用
     */
    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        if (bgDrawable != null) {
            bgDrawable.setColor(backgroundColor);
        }
    }

    /**
     * 设置获取焦点时的背景色
     *
     * @param focusedBackgroundColor 获取焦点时的背景色
     */
    public void setFocusedBackgroundColor(@ColorInt int focusedBackgroundColor) {
        this.focusedBackgroundColor = focusedBackgroundColor;
    }

    /**
     * 设置边框的颜色,边框的宽度(粗细)
     *
     * @param borderColor 边框的颜色
     * @param borderWidth 边框的宽度(粗细)
     */
    public void setBorder(@ColorInt int borderColor, int borderWidth) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        if (bgDrawable != null) {
            bgDrawable.setStroke(borderWidth, borderColor);
        }
    }

    /**
     * 设置获取焦点时的颜色
     *
     * @param focusedBorderColor 获取焦点时的颜色
     */
    public void setFocusedBorderColor(int focusedBorderColor) {
        this.focusedBorderColor = focusedBorderColor;
    }

    /**
     * 设置最大输入字数
     *
     * @param maxLength 最大输入字数
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (editText != null) {
            editText.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});
        }
    }

    /**
     * 输入的过滤条件
     *
     * @param digits 过滤条件
     */
    public void setDigits(String digits) {
        this.digits = digits;
    }

    /**
     * 是否显示边框
     *
     * @param showBorder
     */
    public void setShowBorder(boolean showBorder) {
        this.isShowBorder = showBorder;
        if (bgDrawable != null) {
            bgDrawable.setStroke(isShowBorder ? borderColor : 0, isShowBorder ? borderColor : Color.TRANSPARENT);
        }
    }

    /**
     * 设置搜索框的圆角半径
     *
     * @param radius 圆角半径
     */
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        if (bgDrawable != null) {
            bgDrawable.setCornerRadius(radius);
        }
    }

    /**
     * 设置最左侧的Icon
     *
     * @param drawable        最左侧的Icon
     * @param drawablePadding 文字和Icon的距离
     */
    public void setEditIcon(Drawable drawable, int drawablePadding) {
        if (drawable != null) {
            this.editIcon = drawable;
        }
        this.editIconPadding = drawablePadding;
        if (editText != null) {
            setDrawable(editText, null, drawablePadding, drawable, null, null, null);
        }
    }

    /**
     * 设置最左侧的Icon
     *
     * @param resId           最左侧的Icon的资源id
     * @param drawablePadding 文字和Icon的距离
     */
    public void setEditIcon(@DrawableRes int resId, int drawablePadding) {
        this.editIcon = AppCompatResources.getDrawable(getContext(), resId);
        this.editIconPadding = drawablePadding;
        if (editText != null) {
            setDrawable(editText, null, drawablePadding, editIcon, null, null, null);
        }
    }

    /**
     * 获取右侧功能按钮控件
     *
     * @return
     */
    public ImageButton getActionIconButton() {
        return imageButton;
    }

    /**
     * 设置右侧功能按钮显示和隐藏
     *
     * @param visibility
     */
    public void setActionIconVisibility(int visibility) {
        if (imageButton != null) {
            imageButton.setVisibility(visibility);
        }
    }

    /**
     * 设置右侧的Icon
     *
     * @param actionIcon
     */
    public void setActionIcon(Drawable actionIcon) {
        this.actionIcon = actionIcon;
        if (imageButton != null) {
            imageButton.setImageDrawable(actionIcon);
        }
    }

    /**
     * 设置右侧的Icon
     *
     * @param resId
     */
    public void setActionIcon(@DrawableRes int resId) {
        this.actionIcon = AppCompatResources.getDrawable(getContext(), resId);
        if (imageButton != null) {
            imageButton.setImageResource(resId);
        }
    }

    /**
     * 设置文字字号
     *
     * @param textSize 字号
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        if (editText != null) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    /**
     * 设置提示文字
     *
     * @param hint 提示文字
     */
    public void setHint(CharSequence hint) {
        this.hint = hint;
        if (editText != null) {
            editText.setHint(hint);
        }
    }

    /**
     * 设置提示文字
     *
     * @param resid 提示文字资源id
     */
    public void setHint(@StringRes int resid) {
        this.hintId = resid;
        if (editText != null) {
            editText.setHint(resid);
        }
    }

    /**
     * 设置内容文字
     *
     * @param text 内容
     */
    public void setText(CharSequence text) {
        this.text = text;
        if (editText != null) {
            editText.setText(text);
        }
    }

    /**
     * 设置内容文字
     *
     * @param resid 内容资源id
     */
    public void setText(@StringRes int resid) {
        this.textId = resid;
        if (editText != null) {
            editText.setText(resid);
        }
    }

    /**
     * 获取输入的内容
     *
     * @return
     */
    public CharSequence getText() {
        return editText != null ? editText.getText() : "";
    }

    /**
     * 设置文字颜色
     *
     * @param textColor
     */
    public void setTextColor(ColorStateList textColor) {
        this.textColor = textColor;
        if (editText != null) {
            editText.setTextColor(textColor);
        }
    }

    /**
     * 设置文字颜色
     *
     * @param textColor
     */
    public void setTextColor(@ColorInt int textColor) {
        this.textColor = ColorStateList.valueOf(textColor);
        if (editText != null) {
            editText.setTextColor(textColor);
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param textColorHint
     */
    public void setTextColorHint(ColorStateList textColorHint) {
        this.textColorHint = textColorHint;
        if (editText != null) {
            editText.setHintTextColor(textColorHint);
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param textColorHint
     */
    public void setTextColorHint(@ColorInt int textColorHint) {
        this.textColorHint = ColorStateList.valueOf(textColorHint);
        if (editText != null) {
            editText.setHintTextColor(textColorHint);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @return 是否隐藏成功
     */
    public boolean hideKeyboard() {
        boolean isHide = false;
        if (null != getEditText()) {
            InputMethodManager inputManager = (InputMethodManager) getEditText().getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            // 即使当前焦点不在editText，也是可以隐藏的。
            isHide = inputManager.hideSoftInputFromWindow(getEditText().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return isHide;
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        showKeyboard(0);
    }

    /**
     * 显示软键盘
     *
     * @param delay 延时显示软键盘延时的毫秒数
     */
    public void showKeyboard(int delay) {
        if (null != getEditText()) {
            if (getEditText().requestFocus()) {
                if (delay > 0) {
                    getEditText().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getEditText().getContext().getApplicationContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(getEditText(), InputMethodManager.SHOW_IMPLICIT);
                        }
                    }, delay);
                } else {
                    InputMethodManager imm = (InputMethodManager) getEditText().getContext().getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getEditText(), InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
                Log.w("SearchBarView", "showSoftInput() can not get focus");
            }
        }
    }

    /**
     * 获取输入框控件 用于显示或隐藏键盘
     *
     * @return 返回的输入框控件
     */
    public EditText getEditText() {
        return editText;
    }

    /**
     * 设置输入监听
     *
     * @param textWatchListener 输入监听回调接口
     */
    public void addTextWatchListener(TextWatcher textWatchListener) {
        this.textWatchListener = textWatchListener;
    }

    /**
     * 焦点改变监听
     *
     * @param onFocusChangeListener 焦点改变的回调接口
     */
    public void setClearEditTextFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    /**
     * 设置内容的改变监听回调接口
     *
     * @param onTextChangedListener 内容的改变监听回调接口
     */
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    /**
     * 设置内容的改变监听回调接口
     *
     * @param onTextChangedListener 内容的改变监听回调接口
     * @param onClickListener       最右侧按钮的点击事件
     */
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener, OnClickListener onClickListener) {
        this.onTextChangedListener = onTextChangedListener;
        if (imageButton != null) {
            imageButton.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置右侧按钮的点击事件
     *
     * @param onClickListener 最右侧按钮的点击事件
     */
    public void setOnActionButtonClickListener(OnClickListener onClickListener) {
        if (imageButton != null) {
            imageButton.setOnClickListener(onClickListener);
        }
    }

    /**
     * 内容的改变监听
     */
    public interface OnTextChangedListener {
        void onChanged(Editable s, EditText editText, ImageButton imageButton);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue
     * @return
     */
    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 为TextView设置Drawable图片
     *
     * @param textView
     * @param charSequence    文字内容
     * @param drawablePadding 图片和文字的间距
     * @param leftDrawable    左图片
     * @param topDrawable     上图片
     * @param rightDrawable   右图片
     * @param bottomDrawable  下图片
     */
    private void setDrawable(TextView textView, CharSequence charSequence, int drawablePadding, Drawable leftDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottomDrawable) {
        if (textView != null) {
            if (!TextUtils.isEmpty(charSequence)) {
                textView.setText(charSequence);
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
            textView.setCompoundDrawablePadding(drawablePadding);
        }
    }

}
