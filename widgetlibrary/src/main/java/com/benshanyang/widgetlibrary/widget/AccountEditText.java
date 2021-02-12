package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.benshanyang.widgetlibrary.R;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * 类描述: 带清除功能的输入框 </br>
 * 时间: 2019/3/20 11:07
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class AccountEditText extends FrameLayout {

    private ImageView ivIcon;//输入框图标
    private EditText etInput;//输入框
    private ImageButton ibClear;//清除按钮
    private View borderView;//底边

    private CharSequence digits = "";//过滤条件
    private float borderWidth = 0;//底部分割线宽度
    private int maxLength = Integer.MAX_VALUE;//最大输入长度
    private int focusedBorderColor = 0xFF0087f3;//输入框获取焦点时候的底边颜色
    private int normalBorderColor = 0xFFD5D5D5;//输入框未获取焦点时候的底边颜色
    private boolean isShowBorder = false;//是否显示底部分割线 默认不现实
    private boolean isShowIcon = true;//是否显示左侧图标

    private InputFilter inputFilter;//过滤器
    private TextWatcher textWatchListener;//输入监听
    private OnFocusChangeListener onFocusChangeListener;//输入框的焦点改变监听

    public AccountEditText(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public AccountEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AccountEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_account_edittext, this, false);
        initView(view);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccountEditText);
        if (typedArray != null) {

            boolean singleLine = typedArray.getBoolean(R.styleable.AccountEditText_android_singleLine, false);//是否是一行
            isShowBorder = typedArray.getBoolean(R.styleable.AccountEditText_isShowBorder, false);//是否显示底部分割线
            isShowIcon = typedArray.getBoolean(R.styleable.AccountEditText_isShowIcon, true);//是否显示左侧图标
            int gravity = typedArray.getInt(R.styleable.AccountEditText_android_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);//文字显示的位置

            float iconPaddingLeft = typedArray.getDimension(R.styleable.AccountEditText_iconPaddingLeft, 0);//icon和左边的距离
            float iconTextPaddingLeft = typedArray.getDimension(R.styleable.AccountEditText_drawablePaddingLeft, etInput.getPaddingLeft());//icon和文字间的距离
            float clearIconPaddingLeft = typedArray.getDimension(R.styleable.AccountEditText_clearIconPaddingLeft, ibClear.getPaddingLeft());//清除按钮的左边距
            float clearIconPaddingRight = typedArray.getDimension(R.styleable.AccountEditText_clearIconPaddingRight, ibClear.getPaddingLeft());//清除按钮的右边距
            int minLines = typedArray.getInt(R.styleable.AccountEditText_android_minLines, 1);//最小输入行数
            int maxLines = typedArray.getInt(R.styleable.AccountEditText_android_maxLines, Integer.MAX_VALUE);//最大输入行数
            float textSize = typedArray.getDimensionPixelSize(R.styleable.AccountEditText_android_textSize, 0);//文字的大小

            ColorStateList textColor = typedArray.getColorStateList(R.styleable.AccountEditText_android_textColor);//设置密码字体颜色
            ColorStateList textColorHint = typedArray.getColorStateList(R.styleable.AccountEditText_android_textColorHint);//提示文字的颜色

            CharSequence text = typedArray.getText(R.styleable.AccountEditText_android_text);//设置输入框文字
            CharSequence hint = typedArray.getText(R.styleable.AccountEditText_android_hint);//提示文字
            int mTextId = -1;
            int mHintId = -1;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mTextId = typedArray.getResourceId(R.styleable.AccountEditText_android_text, Resources.ID_NULL);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mHintId = typedArray.getResourceId(R.styleable.AccountEditText_android_hint, Resources.ID_NULL);
            }

            Drawable icon = typedArray.getDrawable(R.styleable.AccountEditText_android_icon);//输入框最左侧小图标
            Drawable iconClear = typedArray.getDrawable(R.styleable.AccountEditText_iconClear);//输入框最左侧小图标

            int iconColor = typedArray.getColor(R.styleable.AccountEditText_iconColor, -1);//文字左侧图变颜色
            int iconClearColor = typedArray.getColor(R.styleable.AccountEditText_iconClearColor, -1);//清除按钮图标颜色
            normalBorderColor = typedArray.getColor(R.styleable.AccountEditText_normalBorderColor, 0xFFD5D5D5);//失去焦点时底边颜色
            focusedBorderColor = typedArray.getColor(R.styleable.AccountEditText_focusedBorderColor, 0xFF0087f3);//获取焦点时底边颜色
            borderWidth = typedArray.getDimension(R.styleable.AccountEditText_borderWidth, 0);//底部分割线宽度
            digits = typedArray.getText(R.styleable.AccountEditText_android_digits);//过滤字符串
            maxLength = typedArray.getInteger(R.styleable.AccountEditText_android_maxLength, Integer.MAX_VALUE);//最大输入的长度
            int inputType = typedArray.getInt(R.styleable.AccountEditText_android_inputType, InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);//输入框输入类型
            float borderLeftSpace = typedArray.getDimension(R.styleable.AccountEditText_bottomBorderLeftSpace, 0);//第部边框线的左侧margin
            float borderRightSpace = typedArray.getDimension(R.styleable.AccountEditText_bottomBorderRightSpace, 0);//第部边框线的右侧margin
            int minHeight = typedArray.getDimensionPixelSize(R.styleable.AccountEditText_android_minHeight, -1);//输入框最小高度
            float textPaddingTop = typedArray.getDimension(R.styleable.AccountEditText_textPaddingTop, 0);//文字的上边距
            float textPaddingBottom = typedArray.getDimension(R.styleable.AccountEditText_textPaddingBottom, 0);//文字的下边距

            //设置左侧图标和控件左边的距离
            if (ivIcon != null) {
                ivIcon.setPadding((int) iconPaddingLeft, 0, 0, 0);
                //设置左侧图标
                if (icon != null) {
                    ivIcon.setImageDrawable(icon);
                }
                //设置左侧图标颜色
                if (iconColor != -1) {
                    ivIcon.setColorFilter(iconColor);
                }
                //是否显示左侧图标
                ivIcon.setVisibility(isShowIcon ? View.VISIBLE : View.GONE);
            }

            //设置清除按钮的左右内边距
            if (ibClear != null) {
                ibClear.setPadding((int) clearIconPaddingLeft, 0, (int) clearIconPaddingRight, 0);
                //设置清除按钮的图标
                if (iconClear != null) {
                    ibClear.setImageDrawable(iconClear);
                }
                //设置清除按钮的图标颜色
                if (iconClearColor != -1) {
                    ibClear.setColorFilter(iconClearColor);
                }
            }

            if (etInput != null) {
                if (minHeight != -1) {
                    etInput.setMinHeight(minHeight);
                    etInput.setMinimumHeight(minHeight);
                }
                //设置图标和文字的间距
                etInput.setPadding((int) iconTextPaddingLeft, (int) textPaddingTop, 0, (int) textPaddingBottom);
                //设置最小行数
                etInput.setMinLines(minLines);
                //设置最大行数
                etInput.setMaxLines(maxLines);
                //是否只显示一行
                etInput.setSingleLine(singleLine);
                //设置字体大小
                etInput.setTextSize(COMPLEX_UNIT_PX, textSize);
                //设置字体颜色
                if (textColor != null) {
                    etInput.setTextColor(textColor);
                }
                //设置文字
                if (!TextUtils.isEmpty(text)) {
                    etInput.setText(text);
                }
                if (mTextId != -1 && mTextId != Resources.ID_NULL) {
                    etInput.setText(mTextId);
                }
                //设置提示文字
                if (!TextUtils.isEmpty(hint)) {
                    etInput.setHint(hint);
                }
                if (mHintId != -1 && mHintId != Resources.ID_NULL) {
                    etInput.setHint(mHintId);
                }
                //设置提示文字颜色
                if (textColorHint != null) {
                    etInput.setHintTextColor(textColorHint);
                }
                //设置文字显示的方向
                etInput.setGravity(gravity);
                //设置输入类型
                etInput.setInputType(inputType);
            }


            //设置底边
            if (borderView != null) {
                if (isShowBorder) {
                    //显示底边
                    ViewGroup.LayoutParams params = borderView.getLayoutParams();
                    params.height = (int) borderWidth;
                    borderView.setLayoutParams(params);
                    borderView.setBackgroundColor(normalBorderColor);
                    borderView.setVisibility(VISIBLE);
                } else {
                    //不显示底边
                    borderView.setVisibility(GONE);
                }

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) borderView.getLayoutParams();
                params.leftMargin = (int) borderLeftSpace;
                params.rightMargin = (int) borderRightSpace;
                borderView.setLayoutParams(params);
            }

            typedArray.recycle();
        }

        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));
        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                if (!TextUtils.isEmpty(charSequence)) {
                    String inputStr = charSequence.toString();
                    if (TextUtils.isEmpty(digits)) {
                        //如果没有匹配条件就不去匹配
                        return null;
                    } else if (inputStr.matches(digits.toString())) {
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

        //清空输入框
        if (ibClear != null) {
            ibClear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    etInput.setText("");
                }
            });
        }

        if (etInput != null) {
            //设置过滤器
            etInput.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});

            etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String s = etInput.getText().toString();
                    if (hasFocus) {
                        // 此处为得到焦点时的处理内容
                        if (!TextUtils.isEmpty(s)) {
                            ibClear.setVisibility(VISIBLE);
                        } else {
                            ibClear.setVisibility(GONE);
                        }
                        if (isShowBorder) {
                            borderView.setBackgroundColor(focusedBorderColor);//输入框获取焦点的底边颜色
                        }
                    } else {
                        // 此处为失去焦点时的处理内容
                        ibClear.setVisibility(GONE);
                        if (isShowBorder) {
                            borderView.setBackgroundColor(normalBorderColor);//输入框失去焦点的底边颜色
                        }
                    }
                    //焦点改变的监听回调接口
                    if (onFocusChangeListener != null) {
                        onFocusChangeListener.onFocusChange(v, hasFocus);
                    }
                }
            });

            //输入内容改变时的监听
            etInput.addTextChangedListener(new TextWatcher() {
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
                    if (!TextUtils.isEmpty(s.toString())) {
                        ibClear.setVisibility(VISIBLE);
                    } else {
                        ibClear.setVisibility(GONE);
                    }
                    if (textWatchListener != null) {
                        textWatchListener.afterTextChanged(s);
                    }
                }
            });
        }
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);//输入框图标
        etInput = (EditText) view.findViewById(R.id.et_inputbar);//输入框
        ibClear = (ImageButton) view.findViewById(R.id.ib_clearbtn);//清除按钮
        borderView = view.findViewById(R.id.borderview);//底边
    }

    /**
     * 设置底部边框距父布局左侧的间距
     *
     * @param space 距离值
     */
    public void setBorderLeftSpace(int space) {
        if (borderView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) borderView.getLayoutParams();
            params.leftMargin = space;//第部边框线的左侧margin
            borderView.setLayoutParams(params);
        }
    }

    /**
     * 设置底部边框距父布局右侧的间距
     *
     * @param space 距离值
     */
    public void setBorderRightSpace(int space) {
        if (borderView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) borderView.getLayoutParams();
            params.rightMargin = space;//第部边框线的右侧margin
            borderView.setLayoutParams(params);
        }
    }

    /**
     * 设置输入框的输入类型
     *
     * @param type 输入类型 输入类型请参照{@link InputType}
     * @see android.text.InputType 输入类型入参
     */
    public void setInputType(int type) {
        if (etInput != null) {
            etInput.setInputType(type);
        }
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
     * 设置是否只显示一行
     *
     * @param isSinglieLine 设置是否只显示一行
     */
    public void setSingleLine(boolean isSinglieLine) {
        if (etInput != null) {
            etInput.setSingleLine(isSinglieLine);
        }
    }

    /**
     * 是否显示底边
     *
     * @param isShow 是否显示底部分割线
     */
    public void isShowBorder(boolean isShow) {
        this.isShowBorder = isShow;
        if (isShow) {
            //显示底边
            borderView.setVisibility(VISIBLE);
        } else {
            //不显示底边
            borderView.setVisibility(GONE);
        }
    }

    /**
     * 设置文字显示的方向
     *
     * @param gravity 文字方向 使用android.view.Gravity的相关常量
     */
    public void setGravity(int gravity) {
        if (etInput != null) {
            etInput.setGravity(gravity);
        }
    }

    /**
     * 设置左侧图标和控件左边的距离
     *
     * @param padding 距离值
     */
    public void setIconPaddingLeft(int padding) {
        if (ivIcon != null) {
            ivIcon.setPadding(padding, 0, 0, 0);
        }
    }

    /**
     * 设置左侧图标和文字的距离
     *
     * @param padding 距离值
     */
    public void setIconTextPaddingLeft(int padding) {
        if (etInput != null) {
            etInput.setPadding(padding, 0, 0, 0);
        }
    }

    /**
     * 设置清除按钮的左右内边距
     *
     * @param padding 距离值
     */
    public void setClearIconPaddingLeft(int padding) {
        if (ibClear != null) {
            ibClear.setPadding(padding, 0, ibClear.getPaddingRight(), 0);
        }
    }

    /**
     * 设置清除按钮的左右内边距
     *
     * @param padding 距离值
     */
    public void setClearIconPaddingRight(int padding) {
        if (ibClear != null) {
            ibClear.setPadding(ibClear.getPaddingLeft(), 0, padding, 0);
        }
    }

    /**
     * 设置最小行数
     *
     * @param lines 行数
     */
    public void setMinLines(int lines) {
        if (etInput != null) {
            etInput.setMinLines(lines);
        }
    }

    /**
     * 设置最大行数
     *
     * @param lines 行数
     */
    public void setMaxLines(int lines) {
        if (etInput != null) {
            etInput.setMaxLines(lines);
        }
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 字号
     */
    public void setTextSize(float fontSize) {
        if (etInput != null) {
            etInput.setTextSize(COMPLEX_UNIT_PX, fontSize);
        }
    }

    /**
     * 设置字体颜色
     *
     * @param textColor 字体颜色色值
     */
    public void setTextColor(@ColorInt int textColor) {
        if (etInput != null) {
            etInput.setTextColor(textColor);
        }
    }

    /**
     * 设置文字内容
     *
     * @param str 文字内容
     */
    public void setText(CharSequence str) {
        if (etInput != null) {
            etInput.setText(str);
        }
    }

    /**
     * 设置文字内容
     *
     * @param resid 资源id
     */
    public void setText(@StringRes int resid) {
        if (etInput != null) {
            etInput.setText(resid);
        }
    }

    /**
     * 返回输入框内容
     *
     * @return
     */
    public CharSequence getText() {
        Editable text = null;
        if (etInput != null) {
            text = etInput.getText();
        }

        return TextUtils.isEmpty(text) ? "" : text;
    }

    /**
     * 设置提示文字颜色
     *
     * @param colorHint 提示文字颜色
     */
    public void setHintTextColor(@ColorInt int colorHint) {
        if (etInput != null) {
            etInput.setHintTextColor(colorHint);
        }
    }

    /**
     * 设置提示文字
     *
     * @param hint 提示文字
     */
    public void setHint(CharSequence hint) {
        if (etInput != null) {
            etInput.setHint(hint);
        }
    }

    /**
     * 设置提示文字
     *
     * @param resId 提示文字资源id
     */
    public void setHint(@StringRes int resId) {
        if (etInput != null) {
            etInput.setHint(resId);
        }
    }

    /**
     * 获取提示文本内容
     *
     * @return 返回提示内容
     */
    public CharSequence getHint() {
        CharSequence hint = null;
        if (etInput != null) {
            hint = etInput.getHint();
        }
        return TextUtils.isEmpty(hint) ? "" : hint;
    }

    /**
     * 设置左侧icon
     *
     * @param drawable 左侧图标
     */
    public void setIcon(@Nullable Drawable drawable) {
        if (ivIcon != null) {
            ivIcon.setImageDrawable(drawable);
        }
    }

    /**
     * 设置左侧icon
     *
     * @param resId 左侧图标资源id
     */
    public void setIcon(@DrawableRes int resId) {
        if (ivIcon != null) {
            ivIcon.setImageResource(resId);
        }
    }

    /**
     * 设置左侧icon
     *
     * @param bitmap 左侧图标
     */
    public void setIcon(Bitmap bitmap) {
        if (ivIcon != null) {
            ivIcon.setImageBitmap(bitmap);
        }
    }

    /**
     * 获取左侧图标显示的状态
     *
     * @return true显示 false不显示
     */
    public boolean isShowIcon() {
        return isShowIcon;
    }

    /**
     * 设置是否显示左侧图标
     *
     * @param showIcon true显示 false不显示
     */
    public void setShowIcon(boolean showIcon) {
        isShowIcon = showIcon;
        if (ivIcon != null) {
            ivIcon.setVisibility(showIcon ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置清除按钮的图标
     *
     * @param drawable 图标的drawable
     */
    public void setClearIcon(@Nullable Drawable drawable) {
        if (ibClear != null) {
            ibClear.setImageDrawable(drawable);
        }
    }

    /**
     * 设置清除按钮的图标
     *
     * @param resId 图标的资源id
     */
    public void setClearIcon(@DrawableRes int resId) {
        if (ibClear != null) {
            ibClear.setImageResource(resId);
        }
    }

    /**
     * 设置清除按钮的图标
     *
     * @param bitmap 清除按钮图标
     */
    public void setClearIcon(Bitmap bitmap) {
        if (ibClear != null) {
            ibClear.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置底边的颜色和样式
     *
     * @param borderWidth  边线的宽度
     * @param focusedColor 获取焦点的时候线的颜色
     * @param normalColor  失去焦点的时候线的颜色
     */
    public void setBottomBorder(int borderWidth, @ColorInt int focusedColor, @ColorInt int normalColor) {
        //设置线的宽度
        if (borderView != null) {
            if (isShowBorder) {
                borderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, borderWidth));
                borderView.setBackgroundColor(normalColor);
                borderView.setVisibility(VISIBLE);
            } else {
                //不显示底边
                borderView.setVisibility(GONE);
            }
        }

        focusedBorderColor = focusedColor;//输入框获取焦点时候的底边颜色
        normalBorderColor = normalColor;//输入框未获取焦点时候的底边颜色
    }

    /**
     * 设置过滤字符串的正则表达式
     *
     * @param regex 必须是正则表达式,否则过滤不正确
     */
    public void setDigits(CharSequence regex) {
        this.digits = regex;
    }

    /**
     * 设置字符串最大输入长度
     *
     * @param maxLength 字符串最大输入长度
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (etInput != null) {
            etInput.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});
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
                Log.w("AccountEditText", "showSoftInput() can not get focus");
            }
        }
    }

    /**
     * 获取输入框控件 用于显示或隐藏键盘
     *
     * @return 返回的输入框控件
     */
    public EditText getEditText() {
        return etInput;
    }
}
