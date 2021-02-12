package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.benshanyang.widgetlibrary.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * 类描述: 密码输入框 </br>
 * 时间: 2019/3/20 10:51
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class PasswordEditText extends FrameLayout {

    /**
     * 清除按钮 - 密码显示隐藏按钮
     */
    public static final int CLEAR_PASSWORD = 0;
    /**
     * 密码显示隐藏按钮 - 清除按钮
     */
    public static final int PASSWORD_CLEAR = 1;

    /**
     * 显示 底边
     */
    public static final int VISIBLE = View.VISIBLE;
    /**
     * 占位隐藏 底边
     */
    public static final int INVISIBLE = View.INVISIBLE;
    /**
     * 不占位隐藏 底边
     */
    public static final int GONE = View.GONE;

    private ImageButton ibClear;//清除内容按钮
    private ImageButton ibShowHidePwd;//是否显示密码按钮
    private ImageView ibIcon;//密码输入框icon
    private EditText etPassword;//密码输入框
    private FrameLayout flRightBtn;//右侧功能按钮的输入框
    private View bottomBorder;//底边
    private TextWatcher textWatchListener;//输入监听
    private OnFocusChangeListener onFocusChangeListener;//输入框的焦点改变监听

    private int focusedBorderColor = 0xFF0087f3;//输入框获取焦点时候的底边颜色
    private int normalBorderColor = 0xFFD5D5D5;//输入框未获取焦点时候的底边颜色
    private String pwdDigits = "";//密码输入框的过滤字符串
    private Integer maxLength = Integer.MAX_VALUE;//最大输入的密码位数
    private InputFilter inputFilter;//过滤器

    public PasswordEditText(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View pwdView = LayoutInflater.from(context).inflate(R.layout.layout_password_edittext, this, false);
        initView(pwdView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        if (typedArray != null) {
            int actionButtonType = typedArray.getInt(R.styleable.PasswordEditText_actionButtonType, -1);//要显示那种类型的功能按钮
            int borderVisibility = typedArray.getInt(R.styleable.PasswordEditText_borderVisibility, 2);//是否显示底边默认不现实

            normalBorderColor = typedArray.getColor(R.styleable.PasswordEditText_normalBorderColor, 0xFFD5D5D5);//失去焦点时底边颜色
            focusedBorderColor = typedArray.getColor(R.styleable.PasswordEditText_focusedBorderColor, 0xFF0087f3);//获取焦点时底边颜色
            ColorStateList textColor = typedArray.getColorStateList(R.styleable.PasswordEditText_android_textColor);//设置密码字体颜色
            ColorStateList textColorHint = typedArray.getColorStateList(R.styleable.PasswordEditText_android_textColorHint);//提示文字的颜色
            float textSize = typedArray.getDimension(R.styleable.PasswordEditText_android_textSize, 0);//设置密码字体文字的大小
            float borderLeftSpace = typedArray.getDimension(R.styleable.PasswordEditText_bottomBorderLeftSpace, 0);//第部边框线的左侧margin
            float borderRightSpace = typedArray.getDimension(R.styleable.PasswordEditText_bottomBorderRightSpace, 0);//第部边框线的右侧margin
            float borderWidth = typedArray.getDimension(R.styleable.PasswordEditText_bottomBorderWidth, 0);//底边宽度
            float iconPaddingLeft = typedArray.getDimension(R.styleable.PasswordEditText_iconPaddingLeft, 0);//icon和左边的距离
            float iconTextPaddingLeft = typedArray.getDimension(R.styleable.PasswordEditText_drawablePaddingLeft, etPassword.getPaddingLeft());//icon和左侧文字间的距离
            float iconTextPaddingRight = typedArray.getDimension(R.styleable.PasswordEditText_drawablePaddingRight, etPassword.getPaddingRight());//icon和右侧文字间的距离
            String hint = typedArray.getString(R.styleable.PasswordEditText_android_hint);//提示文字
            String text = typedArray.getString(R.styleable.PasswordEditText_android_text);//设置密码输入框文字
            pwdDigits = typedArray.getString(R.styleable.PasswordEditText_android_digits);//密码输入框的过滤字符串
            maxLength = typedArray.getInteger(R.styleable.PasswordEditText_android_maxLength, Integer.MAX_VALUE);//最大输入的密码位数默认是30位

            int iconColor = typedArray.getColor(R.styleable.PasswordEditText_iconColor, -1);//密码前面小图标的颜色
            int clearImageColor = typedArray.getColor(R.styleable.PasswordEditText_passwordClearImageColor, -1);//清空按钮的颜色
            int switchImageColor = typedArray.getColor(R.styleable.PasswordEditText_passwordSwitchImageColor, -1);//密码可见按钮的颜色

            Drawable icon = typedArray.getDrawable(R.styleable.PasswordEditText_android_icon);//密码输入框最左侧小图标
            Drawable passwordSwitchSrc = typedArray.getDrawable(R.styleable.PasswordEditText_passwordSwitchSrc);//密码显示隐藏小图标
            Drawable passwordClearSrc = typedArray.getDrawable(R.styleable.PasswordEditText_passwordClearSrc);//密码清除按钮

            //选择右侧功能按钮的显示样式
            switch (actionButtonType) {
                case 0:
                    //清除按钮-密码显示隐藏按钮
                    setActionButtonType(CLEAR_PASSWORD);
                    break;
                case 1:
                    //密码显示隐藏按钮-清除按钮
                    setActionButtonType(PASSWORD_CLEAR);
                    break;
                default:
                    break;
            }

            //文字和图片的间距
            if (etPassword != null) {
                etPassword.setPadding((int) iconTextPaddingLeft, 0, (int) iconTextPaddingRight, 0);
            }

            //设置图片和左边的距离
            setIconPaddingLeft((int) iconPaddingLeft);

            //设置底边
            if (bottomBorder != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) borderWidth);
                params.leftMargin = (int) borderLeftSpace;//第部边框线的左侧margin
                params.rightMargin = (int) borderRightSpace;//第部边框线的右侧margin
                bottomBorder.setLayoutParams(params);
                bottomBorder.setBackgroundColor(normalBorderColor);
                //是否显示底边线
                switch (borderVisibility) {
                    case 0:
                        //显示 visible
                        bottomBorder.setVisibility(VISIBLE);
                        break;
                    case 1:
                        //不显示但是占位 invisible
                        bottomBorder.setVisibility(INVISIBLE);
                        break;
                    case 2:
                        //不显示也不占位 gone
                        bottomBorder.setVisibility(GONE);
                        break;
                    default:
                        break;
                }
            }

            if (ibIcon != null) {
                //左侧密码输入框的小图标
                if (icon != null) {
                    ibIcon.setImageDrawable(icon);
                }
                //左侧密码输入框的小图标颜色
                if (iconColor != -1) {
                    ibIcon.setColorFilter(iconColor);
                }
            }

            if (ibClear != null) {
                //密码清除按钮
                if (passwordClearSrc != null) {
                    ibClear.setImageDrawable(passwordClearSrc);
                }
                //密码清除按钮颜色
                if (clearImageColor != -1) {
                    ibClear.setColorFilter(clearImageColor);
                }
            }

            if (ibShowHidePwd != null) {
                //密码显示隐藏按钮
                if (passwordSwitchSrc != null) {
                    ibShowHidePwd.setImageDrawable(passwordSwitchSrc);
                }
                //密码显示隐藏按钮颜色
                if (switchImageColor != -1) {
                    ibShowHidePwd.setColorFilter(switchImageColor);
                }
            }

            if (etPassword != null) {
                //设置密码输入框的文字
                if (!TextUtils.isEmpty(text)) {
                    etPassword.setText(text);
                }
                //设置字体大小
                if (textSize != 0) {
                    etPassword.setTextSize(px2sp(context, textSize));
                }
                //设置字体颜色
                if (textColor != null) {
                    etPassword.setTextColor(textColor);
                }
                //设置提示文字
                if (!TextUtils.isEmpty(hint)) {
                    etPassword.setHint(hint);
                }
                //设置提示文字的颜色
                if (textColorHint != null) {
                    etPassword.setHintTextColor(textColorHint);
                }
            }

            typedArray.recycle();
        }

        addView(pwdView);
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void initView(View view) {
        ibIcon = (ImageView) view.findViewById(R.id.iv_pwd_icon);//密码输入框icon
        etPassword = (EditText) view.findViewById(R.id.et_pwd_edittext);//密码输入框
        flRightBtn = (FrameLayout) view.findViewById(R.id.fl_action_button);//右侧功能按钮的输入框
        bottomBorder = view.findViewById(R.id.view_bottom_border);//底边
    }

    /**
     * 设置按钮的点击事件
     */
    private void setListener() {
        inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                if (!TextUtils.isEmpty(charSequence)) {
                    String inputStr = charSequence.toString();
                    if (TextUtils.isEmpty(pwdDigits)) {
                        //如果没有匹配条件就不去匹配
                        return null;
                    } else if (inputStr.matches(pwdDigits)) {
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
        //设置过滤器
        etPassword.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});

        //清除内容按钮
        if (ibClear != null) {
            ibClear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword != null) {
                        //清空输入的内容
                        etPassword.setText("");
                        //如果密码处于可见状态就设置为不可见状态
                        if (EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == etPassword.getInputType()) {
                            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ibShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                        }
                    }
                }
            });
        }

        if (ibShowHidePwd != null) {
            //显示隐藏密码按钮
            ibShowHidePwd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etPassword != null) {
                        if (EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == etPassword.getInputType()) {
                            //如果可见就设置为不可见
                            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ibShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                        } else {
                            //如果不可见就设置为可见
                            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            ibShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                        }
                        //执行上面的代码后光标会处于输入框的最前方,所以把光标位置挪到文字的最后面
                        etPassword.setSelection(etPassword.getText().toString().length());
                    }
                }
            });

        }

        if (etPassword != null) {
            etPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String s = etPassword.getText().toString();
                    if (hasFocus) {
                        // 此处为得到焦点时的处理内容
                        if (!TextUtils.isEmpty(s)) {
                            flRightBtn.setVisibility(VISIBLE);
                        } else {
                            flRightBtn.setVisibility(GONE);
                        }
                        bottomBorder.setBackgroundColor(focusedBorderColor);//输入框获取焦点的底边颜色
                    } else {
                        // 此处为失去焦点时的处理内容
                        if (EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == etPassword.getInputType()) {
                            //如果可见就设置为不可见
                            etPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ibShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                        }
                        flRightBtn.setVisibility(GONE);
                        bottomBorder.setBackgroundColor(normalBorderColor);//输入框失去焦点的底边颜色
                    }
                    //焦点改变的监听回调接口
                    if (onFocusChangeListener != null) {
                        onFocusChangeListener.onFocusChange(v, hasFocus);
                    }
                }
            });

            //输入内容改变时的监听
            etPassword.addTextChangedListener(new TextWatcher() {
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
                        flRightBtn.setVisibility(VISIBLE);
                    } else {
                        flRightBtn.setVisibility(GONE);
                    }
                    if (textWatchListener != null) {
                        textWatchListener.afterTextChanged(s);
                    }
                }
            });
        }

    }

    /**
     * 设置底部边框距父布局左侧的间距
     *
     * @param space 距离值
     */
    public void setBorderLeftSpace(int space) {
        if (bottomBorder != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
            params.leftMargin = space;//第部边框线的左侧margin
            bottomBorder.setLayoutParams(params);
        }
    }

    /**
     * 设置底部边框距父布局右侧的间距
     *
     * @param space 距离值
     */
    public void setBorderRightSpace(int space) {
        if (bottomBorder != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
            params.rightMargin = space;//第部边框线的右侧margin
            bottomBorder.setLayoutParams(params);
        }
    }

    /**
     * 获取密码输入框的输入内容
     *
     * @return
     */
    public String getText() {
        if (etPassword != null) {
            return etPassword.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * 设置密码输入框的内容
     *
     * @param charSequence 输入框显示的文字
     */
    public void setText(CharSequence charSequence) {
        if (etPassword != null) {
            etPassword.setText(charSequence);
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
    public void setPWDEditTextFocusChangeListener(OnFocusChangeListener
                                                          onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    /**
     * 设置底边的颜色和样式
     *
     * @param borderWidth  边线的宽度
     * @param focusedColor 获取焦点的时候线的颜色
     * @param normalColor  失去焦点的时候线的颜色
     */
    public void setBottomBorder(int borderWidth, @ColorInt int focusedColor,
                                @ColorInt int normalColor) {
        //设置线的宽度
        if (bottomBorder != null) {
            bottomBorder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, borderWidth));
        }
        focusedBorderColor = focusedColor;//输入框获取焦点时候的底边颜色
        normalBorderColor = normalColor;//输入框未获取焦点时候的底边颜色
    }

    /**
     * 设置底部边线是否显示
     *
     * @param visibility 设置底部边线是否显示
     */
    public void setBorderVisibility(@Visibility int visibility) {
        if (bottomBorder != null) {
            bottomBorder.setVisibility(visibility);
        }
    }

    /**
     * 设置密码输入框左侧图标和控件间的距离
     *
     * @param space 距离值
     */
    public void setIconPaddingLeft(int space) {
        if (ibIcon != null) {
            ibIcon.setPadding(space, 0, 0, 0);
        }
    }

    /**
     * 设置密码输入框的文字和左侧图标的间距
     *
     * @param space 距离值
     */
    public void setDrawablePaddingLeft(int space) {
        if (etPassword != null) {
            etPassword.setPadding(space, 0, etPassword.getPaddingRight(), 0);
        }
    }

    /**
     * 设置密码输入框的文字和右侧图标的间距
     *
     * @param space 距离值
     */
    public void setDrawablePaddingRight(int space) {
        if (etPassword != null) {
            etPassword.setPadding(etPassword.getPaddingLeft(), 0, space, 0);
        }
    }

    /**
     * 设置文字大小
     *
     * @param textSize 字号
     */
    public void setTextSize(float textSize) {
        if (etPassword != null) {
            etPassword.setTextSize(COMPLEX_UNIT_PX, textSize);
        }
    }

    /**
     * 设置文字的颜色
     *
     * @param textColor 文字颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        if (etPassword != null) {
            etPassword.setTextColor(textColor);
        }
    }

    /**
     * 设置提示文字
     *
     * @param hint 指示文字
     */
    public void setPWDHint(String hint) {
        if (etPassword != null) {
            etPassword.setHint(hint);
        }
    }

    /**
     * 设置提示文字
     *
     * @param resid 提示的文字
     */
    public void setPWDHint(@StringRes int resid) {
        if (etPassword != null) {
            etPassword.setHint(resid);
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param colorHint 提示文字颜色
     */
    public void setHintTextColor(@ColorInt int colorHint) {
        if (etPassword != null) {
            etPassword.setHintTextColor(colorHint);
        }
    }

    /**
     * 密码输入框左侧的小图标
     *
     * @param drawable 密码输入框左侧小图标Drawable
     */
    public void setIcon(@Nullable Drawable drawable) {
        if (ibIcon != null) {
            ibIcon.setImageDrawable(drawable);
        }
    }

    /**
     * 密码输入框左侧的小图标
     *
     * @param resId 密码输入框左侧小图标资源id
     */
    public void setIcon(@DrawableRes int resId) {
        if (ibIcon != null) {
            ibIcon.setImageResource(resId);
        }
    }

    /**
     * 密码输入框左侧的小图标
     *
     * @param bitmap 密码输入框左侧小图标bitmap
     */
    public void setIcon(Bitmap bitmap) {
        if (ibIcon != null) {
            ibIcon.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置密码显示隐藏按钮图标
     *
     * @param drawable 密码显示隐藏按钮图标Drawable
     */
    public void setPWDSwitch(@Nullable Drawable drawable) {
        if (ibShowHidePwd != null) {
            ibShowHidePwd.setImageDrawable(drawable);
        }
    }

    /**
     * 设置密码显示隐藏按钮图标
     *
     * @param resId 密码显示隐藏按钮图标资源id
     */
    public void setPWDSwitch(@DrawableRes int resId) {
        if (ibShowHidePwd != null) {
            ibShowHidePwd.setImageResource(resId);
        }
    }

    /**
     * 设置密码显示隐藏按钮图标
     *
     * @param bitmap 密码显示隐藏按钮图标bitmap
     */
    public void setPWDSwitch(Bitmap bitmap) {
        if (ibShowHidePwd != null) {
            ibShowHidePwd.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置密码清除按钮图标
     *
     * @param drawable 密码清除按钮图标Drawable
     */
    public void setPWDClear(@Nullable Drawable drawable) {
        if (ibClear != null) {
            ibClear.setImageDrawable(drawable);
        }
    }

    /**
     * 设置密码清除按钮图标
     *
     * @param resid 密码清除按钮图标资源id
     */
    public void setPWDClear(@DrawableRes int resid) {
        if (ibClear != null) {
            ibClear.setImageResource(resid);
        }
    }

    /**
     * 设置密码清除按钮图标
     *
     * @param bitmap 密码清除按钮图标资源bitmap
     */
    public void setPWDClear(Bitmap bitmap) {
        if (ibClear != null) {
            ibClear.setImageBitmap(bitmap);
        }
    }

    /**
     * 设置过滤字符串的正则表达式
     *
     * @param regex 必须是正则表达式,否则过滤不正确
     */
    public void setPWDDigits(String regex) {
        this.pwdDigits = regex;
    }

    /**
     * 设置最大输入的字数
     *
     * @param length 密码输入框最大输入长度
     */
    public void setPWDMaxLength(int length) {
        this.maxLength = length;
        if (etPassword != null) {
            etPassword.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});
        }
    }

    /**
     * 设置密码输入框右侧功能按钮的类型
     *
     * @param type PWDActionType.CLEAR_PASSWORD - 清除按钮-密码显示隐藏按钮,PWDActionType.PASSWORD_CLEAR - 密码显示隐藏按钮-清除按钮
     */
    public void setActionButtonType(@PWDActionType int type) {
        //选择右侧功能按钮的显示样式
        View actionLayout = null;
        switch (type) {
            case CLEAR_PASSWORD:
                //清除按钮-密码显示隐藏按钮
                actionLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_clear_pwd, flRightBtn, false);
                break;
            case PASSWORD_CLEAR:
                //密码显示隐藏按钮-清除按钮
                actionLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_pwd_clear, flRightBtn, false);
                break;
            default:
                break;
        }
        if (flRightBtn != null && actionLayout != null) {
            flRightBtn.removeAllViews();
            ibClear = (ImageButton) actionLayout.findViewById(R.id.ib_clearbtn);//清除内容按钮
            ibShowHidePwd = (ImageButton) actionLayout.findViewById(R.id.ib_pwdbtn);//是否显示密码按钮
            flRightBtn.addView(actionLayout);
            setListener();
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
                Log.w("PasswordEditText", "showSoftInput() can not get focus");
            }
        }
    }

    /**
     * 获取输入框控件 用于显示或隐藏键盘
     *
     * @return 返回的输入框控件
     */
    public EditText getEditText() {
        return etPassword;
    }

    /**
     * 将px转换为与之相等的dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 密码输入框右侧功能按钮的类型
     */
    @IntDef({CLEAR_PASSWORD, PASSWORD_CLEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PWDActionType {
    }

    /**
     * 设置显示底部边框的显示和隐藏
     */
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

}
